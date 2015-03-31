package game

import (
	"appengine"
	"appengine/datastore"
	"appengine/user"
	"encoding/json"
	"src/question"
	"time"
	"net/http"
	"fmt"
	"strconv"
)

type Game struct {
	FID, SID   string
	GID        int
	Rounds     []Round `datastore:"-"`
	RoundsJson []byte
	Turn       bool
	Created    time.Time
}

type Round struct {
	QuestionSID        []int
	fAnswers, sAnswers []int
	fPoints, sPoints int
}

func GameKey(c appengine.Context) *datastore.Key {
	return datastore.NewKey(c, "game", "default_game", 0, nil)
}

func (game *Game) SaveGame(c appengine.Context) (*datastore.Key, error) {
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
	c.Infof("Updating game : %v", game)
	_, err2 := datastore.Put(c, key, game)
	if err2 != nil {
		c.Infof("Error in updating game: ", err)
	}
	return err2
}

func (g *Game) ChangeTurn() {
	g.Turn = !g.Turn
}

func CreateGame(c appengine.Context, FID string) (Game, *datastore.Key, error) {
	g := new(Game)
	g.FID = FID
	g.Created = time.Now()
	g.Rounds = append(g.Rounds, FirstRound(c))
	key, err := g.SaveGame(c)

	return *g, key, err
}

func (g *Game) AddLastPlayer(SID string) {
	g.SID = SID
}

func GetGame(c appengine.Context, id int) (Game, *datastore.Key, error) {
	game := make([]Game, 1, 1)
	qn := datastore.NewQuery("Game").
		Ancestor(GameKey(c)).
		Limit(1).
		Filter("Id =", id)
	if key, err := qn.GetAll(c, &game); len(key) > 0 {
		json.Unmarshal(game[1].RoundsJson, game[1].Rounds)
		c.Infof("User: %v key %v", game[1], key[0])
		return game[1], key[0], nil
	} else {
		c.Infof("Err: %v, user %v", err, game[0])
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
	if err != nil {
		return *new(Game), nil, err
	}
	if count == 0 {
		c.Infof("Count: %v", count)
		u := user.Current(c)
		return CreateGame(c, u.ID)
	} else {
		game := make([]Game, 1, 1)
		if key, err := qn.GetAll(c, &game); len(key) > 0 {
			mGame := game[1]
			json.Unmarshal(mGame.RoundsJson, mGame.Rounds)
			u := user.Current(c)
			mGame.SID = u.ID
			c.Infof("User: %v key %v", mGame, key[0])

			mGame.UpdateGame(c, key[0])
			return mGame, key[0], nil
		} else {
			c.Infof("Err: %v, user %v", err, game[0])
			u := new(Game)
			return *u, nil, err
		}

	}
}

func FirstRound(c appengine.Context) Round {
	round := new(Round)
	questions, _, err := question.GetQuestions(c)
	if err == nil {
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

	questions, _, err := question.GetQuestionsWithPrevious(c, prev)
	if err != nil {
		c.Infof("Error adding new Round err : %v", err)
	} else {
		round := new(Round)
		round.QuestionSID = getIDs(questions)
		g.Rounds = append(g.Rounds, *round)
	}

}

func (g *Game) isUsersTurn(id string) bool {
	if id == g.FID {
		return g.Turn
	} else {
		return !g.Turn
	}
}

func getIDs(q []question.Question) []int {
	id := make([]int, 0, 5)
	for _, qu := range q {
		id = append(id, qu.ID)
	}
	return id
}

func (g *Game) getNewestRound() Round {
	return g.Rounds[len(g.Rounds)-1]
}

func (g *Game) haveAnswersQ(id string) bool {
	r := g.getNewestRound()
	if id == g.FID {
		return len(r.fAnswers) > 0
	} else {
		return len(r.sAnswers) > 0
	}
}

func MatchHandler(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
 	gID, err := strconv.ParseInt(r.FormValue("game_id"), 10, 32)
	if err != nil {
		c.Infof("Error parsing gameID: ", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	game, _, err2 := GetGame(c, int(gID))
	if err2 != nil {
		c.Infof("Error getting game: ", err)
		http.Error(w, err2.Error(), http.StatusInternalServerError)
		return
	}
	if !game.isUsersTurn(u.ID) {
		fmt.Fprintf(w, "Det är inte din tur pucko...")
		return
	}
	if game.haveAnswersQ(u.ID) {
		game.AddNewRound(c)
		r := game.getNewestRound()
		questions, _ := question.GetQuestionsWithID(c, r.QuestionSID)
		str, _ := json.Marshal(questions)
		fmt.Fprintf(w, "q: %v", str)
	}
	a1, err1 := strconv.ParseInt(r.FormValue("a1"), 10, 32)
	a2, err2 := strconv.ParseInt(r.FormValue("a2"), 10, 32)
	a3, err3 := strconv.ParseInt(r.FormValue("a3"), 10, 32)
	a4, err4 := strconv.ParseInt(r.FormValue("a4"), 10, 32)
	a5, err5 := strconv.ParseInt(r.FormValue("a5"), 10, 32)
		if err1 != nil || err2 != nil || err3 != nil || err4 != nil || err5 != nil {
		fmt.Fprintf(w, "error parsing input")
	}

}

func ParseRoundData(c appengine.Context, uID string, GID, a1, a2, a3, a4, a5 int) {
	g, key, err := GetGame(c, GID)
	if err != nil {
		c.Infof("Error Parsing round data: %v", err)
	} else {
		if g.isUsersTurn(uID) {
			round := g.getNewestRound()
			ans := []int{a1, a2, a3, a4, a5}
			if uID == g.FID {
				round.fAnswers = ans
				if round.sAnswers == nil {
					g.ChangeTurn()
					return
				} else {
					g.AddNewRound(c)
				}
			} else {
				round.sAnswers = ans
				if round.fAnswers == nil {
					g.ChangeTurn()
					return
				} else {
					g.AddNewRound(c)
				}
			}
			g.UpdateGame(c, key)
		} else {
			c.Infof("Not Users Turn")
			return
		}
	}
}
