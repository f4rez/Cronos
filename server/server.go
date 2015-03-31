package server

import (
	"appengine"
	"appengine/user"
	"encoding/json"
	"fmt"
	"net/http"
	"strconv"

	"src/game"
	"src/question"
	"src/question_crawler"
	"src/users"
)

type GameInitMessage struct {
	GID              int
	Opp_name, Opp_ID string
}

func init() {
	http.HandleFunc("/", handler)
	http.HandleFunc("/question", mquestion)
	http.HandleFunc("/friendlist", friendlist)
	http.HandleFunc("/friend", friend)
	http.HandleFunc("/crawl", crawl)
	http.HandleFunc("/crawl_data", crawl_data)
	http.HandleFunc("/test", addTestQuestions)
	http.HandleFunc("/registerNewUser", register)
	http.HandleFunc("/joinGame", joinGame)
	http.HandleFunc("/store", store)
}

// Init function.
// Fetch latest top-list or something for the start page.
func handler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-type", "text/html; charset=utf-8")
	c := appengine.NewContext(r)
	u := user.Current(c)

	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	url, _ := user.LogoutURL(c, "/")
	fmt.Fprintf(w, `Welcome, %s! (<a href="%s">sign out</a>)`, u, url)
}

// question handles the request and delivery of new questions to each game,
// or each round of a game to be specific.
// question does also receives the answers of the previous question (if exists)
// and corrects the answers.
func mquestion(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	//game_id := r.FormValue("game_id")
	answer := r.FormValue("answer") // TODO: This might be five answers, or not?
	questions, _, _ := question.GetQuestions(c)
	prev := make([]int, 1, 10)
	for _, q := range questions {
		prev = append(prev, q.ID)
	}
	q2, _, _ := question.GetQuestionsWithPrevious(c, prev)
	questions = append(questions, q2...)
	rString, _ := json.Marshal(questions)
	fmt.Fprintf(w, "Game id: "+string(rString)+"\nAnswer(s): "+answer)
}

// Get the friend list
func friendlist(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")

	fmt.Fprintf(w, "Game id: "+game_id+"\nThe friend list")
}

// Handle single friends (add, remove, challenge etc)
func friend(w http.ResponseWriter, r *http.Request) {
	friend_id := r.FormValue("friend_id")
	action := r.FormValue("action") // add, remove, challenge
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u != nil {
		var err error
		switch action {
		case "add":
			err = users.AddFriend(c, u.ID, friend_id)
		case "remove":
			err = users.RemoveFriend(c, u.ID, friend_id)
		case "challenge":
			err = users.ChallengeFriend(c, u.ID, friend_id)
		}
		if err != nil {
			c.Infof("Error preforming action: %v, Error: %v", action, err)
			http.Error(w, err.Error(), http.StatusInternalServerError)

		} else {
			fmt.Fprintf(w, "ID: "+u.ID+"\nFriend id: "+friend_id+"\nAction: "+action+"\nThe friend list")
		}
	} else {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
}

func answers(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)

	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}

	a1, err1 := strconv.ParseInt(r.FormValue("a1"), 10, 32)
	a2, err2 := strconv.ParseInt(r.FormValue("a2"), 10, 32)
	a3, err3 := strconv.ParseInt(r.FormValue("a3"), 10, 32)
	a4, err4 := strconv.ParseInt(r.FormValue("a4"), 10, 32)
	a5, err5 := strconv.ParseInt(r.FormValue("a5"), 10, 32)
	gID, err6 := strconv.ParseInt(r.FormValue("game_id"), 10, 32)
	if err1 != nil || err2 != nil || err3 != nil || err4 != nil || err5 != nil || err6 != nil {
		fmt.Fprintf(w, "error parsing input")
	}

	game.ParseRoundData(c, u.ID, (int)(gID), (int)(a1), (int)(a2), (int)(a3), (int)(a4), (int)(a5))

}

func crawl(w http.ResponseWriter, r *http.Request) {
	question_crawler.Main(w, r)
}

func store(w http.ResponseWriter, r *http.Request) {
	question_crawler.Store(w, r)
}

func addTestQuestions(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	question.AddSomeQuestionsForTesting(c)

}

func joinGame(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}

	game, _, err := game.FindFreeGame(c)

	if err != nil {
		c.Infof("Error joingGame: %v", err)
	}
	if game.SID == "" {
		fmt.Fprintf(w, "Game ID: %d", game.GID)
	} else {
		mUser, _, err2 := users.GetUser(c, game.FID)
		if err2 != nil {
			c.Infof("User not found: %v", err2)
		}

		initMess := new(GameInitMessage)
		initMess.GID = game.GID
		initMess.Opp_ID = mUser.Oid
		initMess.Opp_name = mUser.Name
		c.Infof("mUser is: %v", mUser)

		str, err3 := json.Marshal(initMess)
		fmt.Fprint(w, string(str))

		if err3 != nil {
			c.Infof("Fel i ", err3)
		}

	}

}

func register(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-type", "text/html; charset=utf-8")
	c := appengine.NewContext(r)
	users, _ := users.MakeUser(c)
	if users.Oid != "" {
		c.Infof("Saving user on registring ID = %v", users.Oid)
		users.SaveUser(c)
	} else {
		c.Infof("Error login: %v", users)
		url, _ := user.LoginURL(c, "/registerNewUser")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
	}
}

func crawl_data(w http.ResponseWriter, r *http.Request) {
	question_crawler.Crawl_data(w, r)
}
