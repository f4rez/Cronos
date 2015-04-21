package server

import (
	"appengine"
	"appengine/user"
	"encoding/json"
	"fmt"
	"net/http"

	"src/game"
	"src/question"
	"src/question_crawler"
	"src/users"
)

func init() {
	http.HandleFunc("/", handler)
	http.HandleFunc("/question", mquestion)
	http.HandleFunc("/friendlist", friendlist)
	http.HandleFunc("/friend", friend)
	http.HandleFunc("/crawl", crawl)
	http.HandleFunc("/crawl_data", crawl_data)
	http.HandleFunc("/startMess", startPageMessage)
	http.HandleFunc("/registerNewUser", register)
	http.HandleFunc("/joinGame", joinGame)
	http.HandleFunc("/store", store)
	http.HandleFunc("/match", matchHandler)
	http.HandleFunc("/challenger", challengerHandler)
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
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	friendList, _ := users.GetFriendList(c, u.ID)
	fString, _ := json.Marshal(friendList)
	fmt.Fprintf(w, string(fString))
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
func crawl(w http.ResponseWriter, r *http.Request) {
	question_crawler.Main(w, r)
}

func store(w http.ResponseWriter, r *http.Request) {
	question_crawler.Store(w, r)
}

func joinGame(w http.ResponseWriter, r *http.Request) {
	game.JoinGame(w, r)
}

func register(w http.ResponseWriter, r *http.Request) {
	users.RegisterUser(w, r)
}

func crawl_data(w http.ResponseWriter, r *http.Request) {
	question_crawler.Crawl_data(w, r)
}

func matchHandler(w http.ResponseWriter, r *http.Request) {
	game.MatchHandler(w, r)
}

func startPageMessage(w http.ResponseWriter, r *http.Request) {
	game.GetStartPageMessage(w, r)
}

func challengerHandler(w http.ResponseWriter, r *http.Request) {
	game.ChallengerHandler(w, r)
}
