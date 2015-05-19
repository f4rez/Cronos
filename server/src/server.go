package server

import (
	"appengine"
	"appengine/user"
	"balancer"
	"fmt"
	"game"
	"net/http"
	"question_crawler"
	"strconv"
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
	http.HandleFunc("/getGameInfo", getGameInfo)
	http.HandleFunc("/balancer", runBalancer)
	http.HandleFunc("/testmax", testMaxLevels)
}

// Init function.
// Fetch latest top-list or something for the start page.
func handler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-type", "text/html; charset=utf-8")
	c := appengine.NewContext(r)
	u := user.Current(c)

	if u == nil {
		url, _ := user.LoginURL(c, "/")
		http.Redirect(w, r, url, http.StatusMovedPermanently)
		return
	}
	url, _ := user.LogoutURL(c, "/")
	fmt.Fprintf(w, "logOutUrl:%v", url)
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

func runBalancer(w http.ResponseWriter, r *http.Request) {

	gameID, _ := strconv.Atoi(r.FormValue("gameID"))
	balancer.Balancer(r, gameID)
}

func testMaxLevels(w http.ResponseWriter, r *http.Request) {
	userMax, qMax := balancer.GetMaxLevels(r)
	fmt.Fprintf(w, "Usermax is: %s and Questionmax is %s", strconv.Itoa(userMax), strconv.Itoa(qMax))
}
