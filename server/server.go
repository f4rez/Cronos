package server

import (
	"appengine"
	"appengine/user"
	"encoding/json"
	"fmt"
	"net/http"

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
	http.HandleFunc("/test", addTestQuestions)
	http.HandleFunc("/registerNewUser", register)
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

	}
}

func crawl(w http.ResponseWriter, r *http.Request) {
	question_crawler.Main(w, r)
}

func addTestQuestions(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	question.AddSomeQuestionsForTesting(c)

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
	question_crawler.Crawl_data(w,r)
}
