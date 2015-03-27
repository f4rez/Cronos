package question

import (
	"appengine"
	"appengine/datastore"
	"math/rand"
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

func GetQuestions(c appengine.Context) ([]Question, []*datastore.Key) {
	question := make([]Question, 0, 10)
	keys := make([]*datastore.Key, 0, 10)
	max := getCountQuestions(c)
	values := getRandomValues(5, max)
	for value, i := range values {
		keys[i] = getKeyForINdex(c, value)
	}
	key, err := qn.GetAll(c, &question)

	return question, key
}

func getRandomValues(numberOfValues, maxValue int) []int {
	numbers := make([]int, numberOfValues)
	for _, i := range numbers {
		numbers[i] = rand.Intn(maxValue)
	}
	return numbers
}

func getCountQuestions(c appengine.Context) (int, error) {
	query := datastore.NewQuery("Question")
	count, err := query.Count(c)
	return count, err

}

func getKeyForIndex(c appengine.Context, id int) (*datastore.Key, error) {
	qn := datastore.NewQuery("Question").
		Ancestor(QuestionKey(c)).
		Limit(1).
		Filter("ID =", id).
		KeysOnly()
	keys, err := qn.GetAll(c, nil)

	return keys[0], err
}
