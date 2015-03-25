package question_crawler

import (
	"fmt"
	"net/http"
)

func Main(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, "hello")
}
