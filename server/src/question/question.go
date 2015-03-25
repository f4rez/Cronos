package question

import (
	"rand"
	"time"
)

type Question struct {
	ID, Level, Year int
	Question        string
}

func QuestionKey(c appengine.Context) *datastore.Key {
	// The string "default_guestbook" here could be varied to have multiple guestbooks.
	return datastore.NewKey(c, "Question", "default_question", 0, nil)
}

func SaveQuestion(c appengine.Context, question Question) {
	key := datastore.NewIncompleteKey(c, "Question", QuestionKey(c))
	_, err := datastore.Put(c, key, &question)
	if err != nil {
		c.Infof("Error in saving: ", err)
	}
}

func GetTeams(c appengine.Context) ([]Question, []*datastore.Key) {
	question := make([]Question, 0, 10)
	keys := make([]*datastore.Key, 10)
	max := getCountQuestions(c)
	values := getRandomValues(5, max)
	qn := datastore.NewQuery("Question").
		Ancestor(QuestionKey(c)).
		Limit(1).
		keysOnly()
	key, err := qn.GetAll(c, &teams)
	for i, team := range keys {
		if team != nil {
			c.Infof("%v. key = %v", i, team)
		}
	}

	return teams, keys
}

func getRandomValues(numberOfValues, maxValue int) []int {
	numbers := make([]int, numberOfValues)
	for value, _ := range numbers {
		value = rand.Intn(maxValue)
	}

	return numbers
}

func getCountQuestions(c appengine.Context) int {
	query := datastore.NewQuery("Question")
	return query.Count()

}
