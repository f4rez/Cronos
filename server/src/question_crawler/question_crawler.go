package question_crawler

import (
	"log"
	//"fmt"
	//"io/ioutil"
	"appengine"
	"appengine/urlfetch"
	//"regexp"
	"encoding/xml"
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

func Crawl_data(w http.ResponseWriter, r *http.Request) {
	get_site("https://sv.wikipedia.org/wiki/Lista_%C3%B6ver_datorspels%C3%A5r", r)

	//fmt.Fprintf(w,"%+v\n", q)

	//re := regexp.MustCompile("")
	//items := re.FindAllString(html, 0)
	//fmt.Fprintf(w, html)
}


// Get the html of the web page specified in uri.
func get_site(uri string, r *http.Request) {
	c := appengine.NewContext(r)
	client := urlfetch.Client(c)

	resp, err := client.Get(uri)
	if err != nil {
		log.Fatal(err)
	}
	//defer resp.Body.close()

	d := xml.NewDecoder(resp.Body)
	d.Strict = false
	d.AutoClose = xml.HTMLAutoClose
	d.Entity = xml.HTMLEntity

	type list_item struct {
		Data string `xml:",chardata"`
	}

	var list_items []list_item

	for {
		t,_ := d.Token()
		if t == nil {
			break
		}

		switch se := t.(type) {
		case xml.StartElement:
			if se.Name.Local == "li" {
				var q list_item
				d.DecodeElement(&q, &se)
				list_items = append(list_items, q)


				c.Infof("%+v\n", q)

			}
		}
	}
	//body, err := ioutil.ReadAll(resp.Body)
	//return string(body)
}
