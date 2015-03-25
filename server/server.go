package server

import (
	"fmt"
	"net/http"
	"src/question_crawler"
)

func init() {
	http.HandleFunc("/", handler)
	http.HandleFunc("/question", question)
	http.HandleFunc("/friendlist", friendlist)
	http.HandleFunc("/friend", friend)
	http.HandleFunc("/crawl", crawl)
}

// Init function.
// Fetch latest top-list or something for the start page.
func handler(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")
	fmt.Fprintf(w,"Game id: "+game_id+"\nTop list")
}

// question handles the request and delivery of new questions to each game,
// or each round of a game to be specific.
// question does also receives the answers of the previous question (if exists)
// and corrects the answers.
func question(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")
	answer := r.FormValue("answer") // TODO: This might be five answers, or not?

	fmt.Fprintf(w,"Game id: "+game_id+"\nAnswer(s): "+answer)
}

// Get the friend list
func friendlist(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")

	fmt.Fprintf(w,"Game id: "+game_id+"\nThe friend list")
}

// Handle single friends (add, remove, challenge etc)
func friend(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")
	friend_id := r.FormValue("friend_id")
	action := r.FormValue("action") // add, remove, challenge

	fmt.Fprintf(w,"Game id: "+game_id+"\nFriend id: "+friend_id+"\nAction: "+action+"\nThe friend list")
}

func crawl(w http.ResponseWriter, r *http.Request) {
	question_crawler.Main(w,r)
}
