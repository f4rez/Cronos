package question_crawler

import (
	"html/template"
	"log"
	"net/http"
	"src/question"
)

func Main(w http.ResponseWriter, r *http.Request) {

	// Sample question, should be retrieved from a database
	q := question.Question{ID: 1, Level: 1000, Question: "hej", Year: 1980}

	t, err := template.ParseFiles("src/question_crawler/crawler.html")
	if err != nil {
		log.Fatal(err)
	}

	t.Execute(w, q)
}
