package question_crawler

import (
	"fmt"
	"net/http"
	"src/question"
	"time"
)

func Main(w http.ResponseWriter, r *http.Request) {
	q := question.Question{Level: 1000, Question: "", Year: 1980}
	fmt.Fprintf(w, q)
}
