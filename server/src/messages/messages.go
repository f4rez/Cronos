package messages

import (
	"appengine"
	"appengine/user"
	"encoding/json"
	"fmt"
	"net/http"
	"src/game"
	"src/users"
)

type GameInitMessage struct {
	GID              int
	Opp_name, Opp_ID string
}

type gamesMessage struct {
	GID, MPoints, OPoints, Turn int
	OppName, OppID              string
	MyTurn                      bool
}

type startMessage struct {
	Games []gamesMessage
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

	games, _, gErr := game.GetGames(c, users.Games)
	if gErr != nil {
		c.Infof("Error getting users: %v", gErr)
		http.Error(w, gErr.Error(), http.StatusInternalServerError)
	}
	mess := make([]gamesMessage, len(games))
	for i, mGame := range games {
		g := new(gamesMessage)
		g.GID = mGame.GID
		g.MyTurn = mGame.IsUsersTurn(u.ID)
		g.Turn = mGame.NumberOfTurns
		pointOne, pointTwo := game.CalculateScore(mGame)
		if u.ID == mGame.FID {
			g.MPoints = pointOne
			g.OPoints = pointTwo
			g.OppID = mGame.SID
			g.OppName = mGame.SName
		} else {
			g.MPoints = pointTwo
			g.OPoints = pointOne
			g.OppID = mGame.FID
			g.OppName = mGame.FName
		}
		mess[i] = *g
	}
	m, err := json.Marshal(mess)
	fmt.Fprintf(w, string(m))

}
