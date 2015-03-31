package question_crawler

import (
	"log"
	//"fmt"
	//"io/ioutil"
	"appengine"
	"appengine/urlfetch"
	//"regexp"
	//"encoding/xml"
	"net/http"
	"src/question"
	"html/template"
	"github.com/PuerkitoBio/goquery"
	"strings"
	"strconv"
	"errors"
	"encoding/json"
	"fmt"
)

type temp_item struct {
	Year int
	Question string
}

func Main(w http.ResponseWriter, r *http.Request) {

	// Sample question, should be retrieved from a database
	q := question.Question{ID: 1, Level: 1000, Question: "hej", Year: 1980}

	t, err := template.ParseFiles("src/question_crawler/crawler.html")
	if err != nil {
		log.Fatal(err)
	}

	t.Execute(w, q)
}


func Crawl_data(w http.ResponseWriter, r *http.Request) {
	templist,_ := get_site("https://sv.wikipedia.org/wiki/Lista_%C3%B6ver_datorspels%C3%A5r", r)
	text,_ := json.Marshal(templist)
	fmt.Fprintf(w, string(text))

	//fmt.Fprintf(w,"%+v\n", q)

	//re := regexp.MustCompile("")
	//items := re.FindAllString(html, 0)
	//fmt.Fprintf(w, html)
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

		c.Infof("ITEM:\n"+listitem+"\n\n")

		q, err := filter_question(listitem)

		//c.Infof("%+v\n", err)

		if(err == nil) {
			list_items = append(list_items, q)
		}
	})

	//c.Infof("%+v\n", list_items)
	return list_items, nil
}

func filter_question(listitem string) (temp_item, error) {

	var q temp_item
	parts := strings.Split(listitem, " – ")

	if(len(parts) < 2) {
		return q, errors.New("The statement is not a complete question.")
	}
	q.Year, _ = strconv.Atoi(parts[0])
	q.Question = parts[1]

	return q, nil
}

func Store(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	year := r.FormValue("year")
	q := r.FormValue("question")
	level := r.FormValue("level")

	var qu question.Question
	qu.ID, _ = question.GetCountQuestions(c)
	qu.Level,_ = strconv.Atoi(level)
	qu.Year,_ = strconv.Atoi(year)
	qu.Question = q

	question.SaveQuestion(c, qu)
}
