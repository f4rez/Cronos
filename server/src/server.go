package server

import (
	"appengine"
	"appengine/user"
	"fmt"
	"net/http"

	"game"
	"question_crawler"
	"users"
)

func init() {
	http.HandleFunc("/", handler)
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
	http.HandleFunc("/search", searchUsers)
	http.HandleFunc("/test", test)
	http.HandleFunc("/getGameInfo", getGameInfo)
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

// Get the friend list
func friendlist(w http.ResponseWriter, r *http.Request) {
	users.GetFriendListHandler(w, r)
}

// Handle single friends (add, remove, challenge etc)
func friend(w http.ResponseWriter, r *http.Request) {
	users.FriendHandler(w, r)
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

func searchUsers(w http.ResponseWriter, r *http.Request) {
	users.SearchUsers(w, r)
}

func getGameInfo(w http.ResponseWriter, r *http.Request) {
	game.GetGameInfo(w, r)
}
func test(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := new(users.Users)
	u.Oid = "1234567"
	u.Name = "Nils nilsson"
	u.SaveUser(c)
	u.Oid = "1"
	u.Name = "Erik Fransson"
	u.SaveUser(c)
	u.Oid = "12"
	u.Name = "Erik Olsson"
	u.SaveUser(c)
	u.Oid = "13"
	u.Name = "Jonas Fransson"
	u.SaveUser(c)
	u.Oid = "14"
	u.Name = "Josef Svensson"
	u.SaveUser(c)
	u.Oid = "15"
	u.Name = "Erik "
	u.SaveUser(c)
	u.Oid = "16"
	u.Name = "Fransson"
	u.SaveUser(c)
	u.Oid = "17"
	u.Name = "Olsson"
	u.SaveUser(c)
	u.Oid = "18"
	u.Name = "Nils"
	u.SaveUser(c)
	u.Oid = "19"
	u.Name = "Johanna"
	u.SaveUser(c)

}
