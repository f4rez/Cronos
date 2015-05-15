package game

import (
	"appengine"
	"appengine/datastore"
	"appengine/taskqueue"
	"appengine/user"
	"encoding/json"
	"errors"
	"fmt"
	"messages"
	"net/http"
	"net/url"
	"question"
	"strconv"
	"time"
	"users"
)

type Game struct {
	FID, SID, FName, SName, FPic, SPic string
	GID                                int
	Rounds                             []Round `datastore:"-"`
	NumberOfTurns                      int
	RoundsJson                         []byte `json:"-"`
	Turn                               bool
	Created                            time.Time `json:"-"`
}

type Round struct {
	QuestionSID       []int
	PlayerOneAnswers  []int
	PlayerTwoAnswerss []int
	PlayerOnePoints   int
	PlayerTwoPoints   int
}

func GameKey(c appengine.Context) *datastore.Key {
	return datastore.NewKey(c, "game", "default_game", 0, nil)
}

func (game *Game) SaveGame(c appengine.Context) (*datastore.Key, error) {
	c.Infof("Saving game: %v", game.GID)
	str, err := json.Marshal(game.Rounds)
	if err != nil {
		return nil, err
	}
	game.RoundsJson = str
	key := datastore.NewIncompleteKey(c, "Game", GameKey(c))
	k, err2 := datastore.Put(c, key, game)
	if err != nil {
		c.Infof("Error in saving game: ", err)
	}
	return k, err2
}

func (game *Game) UpdateGame(c appengine.Context, key *datastore.Key) error {
	str, err := json.Marshal(game.Rounds)
	if err != nil {
		return err
	}
	game.RoundsJson = str
	c.Infof("Updating game : %v", game.GID)
	_, err2 := datastore.Put(c, key, game)
	if err2 != nil {
		c.Infof("Error in updating game: ", err2)
	}
	return err2
}

func (g *Game) ChangeTurn() {
	g.Turn = !g.Turn
}

func CreateGame(c appengine.Context, user users.Users, uKey *datastore.Key) (Game, error) {
	g := new(Game)
	g.FID = user.Oid
	g.FName = user.Name
	g.FPic = user.Picture
	g.Created = time.Now()
	g.Rounds = append(g.Rounds, FirstRound(c))
	g.NumberOfTurns = 1
	g.Turn = false
	id, err := getHighesMatchID(c)
	if err != nil {
		return *new(Game), err
	}
	g.GID = id + 1
	c.Infof("Created game: %v", g)
	user.AddGame(g.GID)
	user.UpdateUser(c, uKey)
	return *g, nil
}

func getHighesMatchID(c appengine.Context) (int, error) {
	game := make([]Game, 1, 1)
	qn := datastore.NewQuery("Game").
		Ancestor(GameKey(c)).
		Limit(1).
		Order("-Created")
	if key, err := qn.GetAll(c, &game); len(key) > 0 {
		return game[1].GID, nil
	} else {
		return 0, err
	}
}

func GetGames(c appengine.Context, ids []int) ([]Game, []*datastore.Key, error) {
	c.Infof("ids: %v", ids)
	games := make([]Game, len(ids), len(ids))
	keys := make([]*datastore.Key, len(ids), len(ids))
	for i, value := range ids {
		key, err2 := getKeyForIndex(c, value)
		keys[i] = key
		if err2 != nil {
			c.Infof("Error gettingGameKey: %v", err2)
			return games, keys, err2
		}
	}
	err := datastore.GetMulti(c, keys, games)
	if err != nil {
		c.Infof("Error getting games: %v, keys: %v", err, keys)
		return games, keys, err
	}

	for i, g := range games {
		temp := make([]Round, 5, 5)
		temp2 := g.RoundsJson
		err2 := json.Unmarshal(temp2, &temp)
		if err2 != nil {
			c.Infof("Error converting Json %v", err2)
			return games, nil, err2
		}
		g.Rounds = temp
		games[i] = g
	}
	return games, keys, nil

}

func getKeyForIndex(c appengine.Context, id int) (*datastore.Key, error) {
	qn := datastore.NewQuery("Game").
		Ancestor(GameKey(c)).
		Limit(1).
		Filter("GID =", id).
		KeysOnly()
	keys, err := qn.GetAll(c, nil)
	if len(keys) > 0 {
		return keys[0], err
	} else {
		return nil, err
	}
}

func GetGame(c appengine.Context, id int) (Game, *datastore.Key, error) {
	game := make([]Game, 1, 1)
	qn := datastore.NewQuery("Game").
		Ancestor(GameKey(c)).
		Limit(1).
		Filter("GID =", id)
	if key, err := qn.GetAll(c, &game); len(key) > 0 {
		temp := make([]Round, 10, 10)
		temp2 := game[1].RoundsJson
		err2 := json.Unmarshal(temp2, &temp)
		if err2 != nil {
			u := new(Game)
			return *u, nil, err2
		}
		game[1].Rounds = temp
		c.Infof("Game: %v", game[1].GID)
		return game[1], key[0], nil
	} else {
		c.Infof("Err: %v, len(key): %v, id = %v", err, len(key), id)
		u := new(Game)
		return *u, nil, err
	}

}

func FindFreeGame(c appengine.Context) (Game, *datastore.Key, error) {
	qn := datastore.NewQuery("Game").
		Ancestor(GameKey(c)).
		Order("-Created").
		Filter("SID =", "")
	count, err := qn.Count(c)
	u := user.Current(c)
	user, uKey, uErr := users.GetUser(c, u.ID)
	if uErr != nil {
		return *new(Game), nil, uErr
	}

	if err != nil {
		return *new(Game), nil, err
	}
	if count == 0 {
		g, err := CreateGame(c, user, uKey)
		return g, nil, err
	} else {
		game := make([]Game, 1, 1)
		if key, err := qn.GetAll(c, &game); len(key) > 0 {
			if u.ID == game[1].FID {
				mErr := errors.New("Du är redan med i det senaste ska")
				return *new(Game), nil, mErr
			}
			temp := make([]Round, 1, 1)
			temp2 := game[1].RoundsJson
			err := json.Unmarshal(temp2, &temp)
			if err != nil {
				u := new(Game)
				return *u, nil, err
			}
			game[1].Rounds = temp

			game[1].SID = u.ID
			game[1].SName = user.Name
			game[1].SPic = user.Picture
			game[1].UpdateGame(c, key[0])
			user.AddGame(game[1].GID)
			user.UpdateUser(c, uKey)
			return game[1], key[0], nil
		} else {
			c.Infof("Err: %v", err)
			u := new(Game)
			return *u, nil, err
		}

	}
}

func FirstRound(c appengine.Context) Round {
	round := new(Round)
	padding := make([]int, 1)
	padding[0] = 1
	round.PlayerOneAnswers = padding
	round.PlayerTwoAnswerss = padding
	round.PlayerTwoPoints = -1
	round.PlayerOnePoints = -1
	questions, _, err := question.GetQuestions(c)
	if err != nil {
		c.Infof("Error making firstROund: %v", err)
	} else {
		for _, q := range questions {
			round.QuestionSID = append(round.QuestionSID, q.ID)
		}
	}
	return *round
}

func (g *Game) AddNewRound(c appengine.Context) {
	prev := make([]int, 1, 10)
	for _, round := range g.Rounds {
		prev = append(prev, round.QuestionSID...)
	}
	c.Infof("previous numbers in add new Round: %v", prev)
	questions, _, err := question.GetQuestionsWithPrevious(c, prev)
	if err != nil {
		c.Infof("Error adding new Round err : %v", err)
	} else {
		round := new(Round)
		round.QuestionSID = getIDs(questions)
		round.PlayerTwoPoints = -1
		round.PlayerOnePoints = -1
		g.Rounds = append(g.Rounds, *round)
		g.NumberOfTurns++
	}

}

func (g *Game) findPrevAndAddQuestions(c appengine.Context) {
	prev := make([]int, 1, 10)
	for _, round := range g.Rounds {
		prev = append(prev, round.QuestionSID...)
	}
	c.Infof("previous numbers in add new Round: %v", prev)
	questions, _, err := question.GetQuestionsWithPrevious(c, prev)
	if err != nil {
		c.Infof("Error getting questionsWithPrev : %v", err)
	} else {
		g.getNewestRound(c).QuestionSID = getIDs(questions)
	}
}

func (g *Game) IsUsersTurn(id string) bool {
	if id == g.FID {
		return !g.Turn
	} else {
		return g.Turn
	}
}

func getIDs(q []question.Question) []int {
	id := make([]int, 0, 5)
	for _, qu := range q {
		id = append(id, qu.ID)
	}
	return id
}

func (g *Game) getNewestRound(c appengine.Context) *Round {
	if len(g.Rounds) == 0 {
		g.AddNewRound(c)
		return &g.Rounds[0]
	}
	return &(g.Rounds[len(g.Rounds)-1])
}

func JoinGame(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}

	mGame, _, err := FindFreeGame(c)
	if err != nil {
		c.Infof("Error joingGame: %v", err)
	} else {
		mGame.SaveGame(c)
	}
	GetStartPageMessage(w, r)
}

func GetGameInfo(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		c.Infof("Not logged in")
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	id, pErr := strconv.ParseInt(r.FormValue("game_id"), 10, 32)
	if pErr != nil {
		c.Infof("Error parseing int: %v", pErr)
		http.Error(w, pErr.Error(), http.StatusInternalServerError)
		return
	}
	mGame, _, err := GetGame(c, int(id))
	if err != nil {
		c.Infof("Error getting game: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	mess, jErr := json.Marshal(mGame)
	if jErr != nil {
		c.Infof("Error json marshal: %v", jErr)
		http.Error(w, jErr.Error(), http.StatusInternalServerError)
		return
	}
	fmt.Fprintf(w, string(mess))

}

func ChallengerHandler(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		c.Infof("Not logged in")
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}

	mUser, uKey, uErr := users.GetUser(c, u.ID)
	if uErr != nil {
		c.Infof("Error getting user: %v", uErr)
		http.Error(w, uErr.Error(), http.StatusInternalServerError)
		return
	}
	opp := r.FormValue("Opponent")
	mUser.RemoveChallenge(opp)

	answer := r.FormValue("answer")
	if answer == "accept" {
		game, err := CreateGame(c, mUser, uKey)
		if err != nil {
			c.Infof("Error creating game: %v", err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		oppUser, oKey, oErr := users.GetUser(c, opp)
		if oErr != nil {
			c.Infof("Error getting user: %v", oErr)
			http.Error(w, oErr.Error(), http.StatusInternalServerError)
			return
		}
		game.SID = oppUser.Oid
		game.SName = oppUser.Name
		game.SaveGame(c)

		oppUser.AddGame(game.GID)
		oppUser.UpdateUser(c, oKey)
	}

}

func MatchHandler(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)

	if u == nil {
		c.Infof("Not logged in")
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	gID, err := strconv.ParseInt(r.FormValue("game_id"), 10, 32)
	if err != nil {
		c.Infof("Error parsing gameID: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	game, key, err2 := GetGame(c, int(gID))
	if err2 != nil {
		c.Infof("Error getting game: %v", err)
		http.Error(w, err2.Error(), http.StatusInternalServerError)
		return
	}
	if !game.IsUsersTurn(u.ID) {
		err3 := errors.New("Det är inte din tur doe...")
		http.Error(w, err3.Error(), http.StatusInternalServerError)

		return
	}
	action := r.FormValue("action")
	switch action {
	case "getQuestions":
		c.Infof("game %v", game)
		q, err4 := question.GetQuestionsWithID(c, game.getNewestRound(c).QuestionSID)
		if err4 != nil {
			if len(game.getNewestRound(c).QuestionSID) < 5 {
				game.findPrevAndAddQuestions(c)
				q, err4 = question.GetQuestionsWithID(c, game.getNewestRound(c).QuestionSID)
				if err4 != nil {
					c.Infof("Error getting questions: %v", err4)
					http.Error(w, err4.Error(), http.StatusInternalServerError)
					return
				}
			}

		}
		mess, errJson := json.Marshal(q)
		if errJson != nil {
			c.Infof("Error marshal: ", errJson)
			http.Error(w, errJson.Error(), http.StatusInternalServerError)
			return
		}
		c.Infof("mess: %v", string(mess))
		fmt.Fprint(w, string(mess))
		break
	case "answerQuestions":
		a1, errA1 := strconv.ParseInt(r.FormValue("a1"), 10, 32)
		a2, errA2 := strconv.ParseInt(r.FormValue("a2"), 10, 32)
		a3, errA3 := strconv.ParseInt(r.FormValue("a3"), 10, 32)
		a4, errA4 := strconv.ParseInt(r.FormValue("a4"), 10, 32)
		a5, errA5 := strconv.ParseInt(r.FormValue("a5"), 10, 32)
		if errA1 != nil || errA2 != nil || errA3 != nil || errA4 != nil || errA5 != nil {
			fmt.Fprintf(w, "error parsing input")
			http.Error(w, errA2.Error(), http.StatusInternalServerError)
		}

		points := calculatePoints(a1, a2, a3, a4, a5)
		one, two := CalculateScore(game)
		ParseRoundData(c, u.ID, game, key, int(a1), int(a2), int(a3), int(a4), int(a5), points)
		c.Infof("Points: %v", points)

		if game.NumberOfTurns >= 5 && game.SID == u.ID {

			uErr := users.GameEnded(c, game.GID, game.FID, game.SID, one, two)
			if uErr != nil {
				fmt.Fprintf(w, "error getting users to remove Game")
				http.Error(w, uErr.Error(), http.StatusInternalServerError)
			}
			if one > two {
				fmt.Fprintf(w, "Winner is %v with %v points, loser is %v with %v points", game.FID, one, game.SID, two)
			} else {
				if one < two {
					fmt.Fprintf(w, "Winner is %v with %v points, loser is %v with %v points", game.SID, two, game.FID, one)
				} else {
					if one == two {
						fmt.Fprintf(w, "It's a draw finalscore: %v - %v", one, two)
					}
				}
			}

			// Call balancer here!
			//Balancer(r, game)

			t := taskqueue.NewPOSTTask("/balancer", url.Values{
				"gameID": {string(game.GID)},
			})
			taskqueue.Add(c, t, "") // add t to the default queue; c is appengine.Context

		}

		fmt.Fprintf(w, "Dina svar har registrerats")
		break

	}
}

func CalculateScore(g Game) (int, int) {
	playerOne := 0
	playerTwo := 0
	for _, round := range g.Rounds {
		playerOne += round.PlayerOnePoints
		playerTwo += round.PlayerTwoPoints
	}
	return playerOne, playerTwo
}

func calculatePoints(a1, a2, a3, a4, a5 int64) int {
	points := 0
	if a1 == 2 {
		if a2 == 2 {
			if a3 == 2 {
				if a4 == 2 {
					if a5 == 2 {
						points = 6
						return points
					}
					points = 4
					return points
				}
				points = 3
				return points
			}
			points = 2
			return points
		}
		points = 1
		return points
	}
	return points
}

func ParseRoundData(c appengine.Context, uID string, g Game, key *datastore.Key, a1, a2, a3, a4, a5, points int) {
	round := g.getNewestRound(c)
	ans := []int{a1, a2, a3, a4, a5}
	if uID == g.FID {
		c.Infof("a")
		round.PlayerOneAnswers = ans
		round.PlayerOnePoints = points
		if len(round.PlayerTwoAnswerss) < 2 {
			c.Infof("b")
			g.ChangeTurn()
		} else {
			c.Infof("c")
			if g.NumberOfTurns < 5 {
				g.AddNewRound(c)
			}
		}
	} else {
		c.Infof("e")
		round.PlayerTwoAnswerss = ans
		round.PlayerTwoPoints = points
		if len(round.PlayerOneAnswers) < 2 {
			c.Infof("f")
			g.ChangeTurn()

		} else {
			c.Infof("g")
			if g.NumberOfTurns < 5 {
				g.AddNewRound(c)
			}
		}
	}
	g.UpdateGame(c, key)
}

func GetStartPageMessage(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	users, _, err := users.GetUser(c, u.ID)
	if err != nil {
		c.Infof("Error getting users: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
	}

	games, _, gErr := GetGames(c, users.Games)
	if gErr != nil {
		c.Infof("Error getting games: %v", gErr)
		http.Error(w, gErr.Error(), http.StatusInternalServerError)
	}

	startMess := new(messages.StartMessage)
	mess := make([]messages.GamesMessage, len(games))
	for i, mGame := range games {
		g := new(messages.GamesMessage)
		g.GID = mGame.GID
		g.MyTurn = mGame.IsUsersTurn(u.ID)
		g.Turn = mGame.NumberOfTurns
		pointOne, pointTwo := CalculateScore(mGame)
		c.Infof("score %v - %v", pointOne, pointTwo)
		c.Infof("%v", mGame.Rounds)
		if u.ID == mGame.FID {
			g.MPoints = pointOne
			g.OPoints = pointTwo
			g.OppID = mGame.SID
			g.OppName = mGame.SName
			g.OppPic = mGame.SPic
		} else {
			g.MPoints = pointTwo
			g.OPoints = pointOne
			g.OppID = mGame.FID
			g.OppName = mGame.FName
			g.OppPic = mGame.FPic
		}
		mess[i] = *g
	}
	startMess.Games = mess
	finished := make([]messages.FinishedGame, len(users.FinishedGames))
	for i, fGame := range users.FinishedGames {
		f := new(messages.FinishedGame)
		f.GID = fGame.GID
		f.MyScore = fGame.MyScore
		f.OppScore = fGame.OppScore
		f.OppName = fGame.OppName
		finished[i] = *f
	}
	c.Infof("Finished %v", finished)
	startMess.Finished = finished

	chall := make([]messages.ChallengeMessage, len(users.ChallengeList))
	for i, challenge := range users.ChallengeList {
		f := new(messages.ChallengeMessage)
		f.OppName = challenge.OppName
		f.OppID = challenge.OppID
		chall[i] = *f
	}
	startMess.Challenges = chall

	m, err := json.Marshal(startMess)
	fmt.Fprintf(w, string(m))

}
