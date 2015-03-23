package server

import (
	"fmt"
	"net/http"
)

func init() {
	http.HandleFunc("/", handler)
	http.HandleFunc("/question", question)
}

func handler(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w,"hello")
}

// question handles the request and delivery of new questions to each game.
// question does also receives the answers of the previous question (if exists)
// and corrects the answers.
func question(w http.ResponseWriter, r *http.Request) {
	game_id := r.FormValue("game_id")
	answer := r.FormValue("answer") // TODO: This might be five answers, or not?

	fmt.Fprintf(w,"Game id: "+game_id+"\nAnswer(s): "+answer)
}
