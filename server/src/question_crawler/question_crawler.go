package question_crawler

import (
	"appengine"
	"appengine/urlfetch"
	"encoding/json"
	"errors"
	"fmt"
	"github.com/PuerkitoBio/goquery"
	"html/template"
	"log"
	"net/http"
	"question"
	"strconv"
	"strings"
)

type temp_item struct {
	Year     int
	Question string
}

func Main(w http.ResponseWriter, r *http.Request) {

	// Sample question, should be retrieved from a database
	q := question.Question{ID: 1, Level: 0, Question: "Hämta sida först", Year: 0000}

	t, err := template.ParseFiles("question_crawler/crawler.html")
	if err != nil {
		log.Fatal(err)
	}

	t.Execute(w, q)
}

func Crawl_data(w http.ResponseWriter, r *http.Request) {
	site := r.FormValue("site")
	// "https://sv.wikipedia.org/wiki/Lista_%C3%B6ver_datorspels%C3%A5r"

	templist, _ := get_site(site, r)
	text, _ := json.Marshal(templist)
	fmt.Fprintf(w, string(text))
}

func get_site(uri string, r *http.Request) ([]temp_item, error) {
	c := appengine.NewContext(r)
	client := urlfetch.Client(c)
	resp, err := client.Get(uri)
	if err != nil {
		return nil, err
	}

	doc, err := goquery.NewDocumentFromResponse(resp)
	if err != nil {
		return nil, err
	}

	var list_items []temp_item

	doc.Find("#bodyContent li").Not("#bodyContent #toc li").Each(func(i int, s *goquery.Selection) {
		listitem := s.Text()

		c.Infof("ITEM:\n" + listitem + "\n\n")

		q, err := filter_question(listitem)

		c.Infof("%v", err)

		if err == nil {
			list_items = append(list_items, q)
		}
	})

	//c.Infof("%+v\n", list_items)
	return list_items, nil
}

func filter_question(listitem string) (temp_item, error) {

	var q temp_item
	var err error
	parts := strings.Split(listitem, " – ")

	if len(parts) < 2 {
		parts = strings.Split(listitem, " - ")
		if len(parts) < 2 {
			parts = strings.Split(listitem, ": ")
			if len(parts) < 2 {
				return q, errors.New("The statement is not a complete question.")
			}
		}
	}
	q.Year, err = strconv.Atoi(parts[0])
	if err != nil {
		q.Year, err = strconv.Atoi(parts[0][len(parts[0])-4 : len(parts[0])])
		if err != nil {
			return q, err
		}
	}
	q.Question = parts[1]

	if q.Year <= 0 {
		return q, errors.New("The year is not complete.")
	}

	return q, nil
}

func Store(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	year := r.FormValue("year")
	q := r.FormValue("q")
	level := r.FormValue("level")

	var qu question.Question
	var err error
	qu.ID, err = question.GetCountQuestions(c)
	if err != nil {
		c.Infof("Error getting count: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	qu.Level, err = strconv.Atoi(level)
	if err != nil {
		c.Infof("Error converting level: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	qu.Year, err = strconv.Atoi(year)
	if err != nil {
		c.Infof("Error convertin year: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	qu.Question = q

	if question.HasQuestion(c, qu) {
		c.Infof("Question already in database")
		return
	}

	c.Infof("LEVEL: %+v\n", level)
	question.SaveQuestion(c, qu)
}
