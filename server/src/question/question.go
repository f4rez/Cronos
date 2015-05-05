package question

import (
	"appengine"
	"appengine/datastore"
	"math/rand"
	"time"
)

var NUMBER_OF_QUESTIONS = 6

type Question struct {
	ID, Level, Year int
	Question        string
}

func QuestionKey(c appengine.Context) *datastore.Key {
	return datastore.NewKey(c, "Question", "default_question", 0, nil)
}

func SaveQuestion(c appengine.Context, question Question) {
	key := datastore.NewIncompleteKey(c, "Question", QuestionKey(c))
	_, err := datastore.Put(c, key, &question)
	if err != nil {
		c.Infof("Error in saving question: ", err)
	}
}

// Return true if there exist a question with the same text in datastore.
func HasQuestion(c appengine.Context, qu Question) bool {
	q := datastore.NewQuery("Question").Filter("Question =", qu.Question)

	var values []Question
	q.GetAll(c, &values)

	if len(values) == 0 {
		return false
	}
	return true
}

func GetQuestions(c appengine.Context) ([]Question, []*datastore.Key, error) {
	question := make([]Question, NUMBER_OF_QUESTIONS)
	keys := make([]*datastore.Key, NUMBER_OF_QUESTIONS)
	max, _ := GetCountQuestions(c)
	max--
	values := getRandomValues(c, NUMBER_OF_QUESTIONS, max)
	for i, value := range values {
		keys[i], _ = getKeyForIndex(c, value)
	}
	err := datastore.GetMulti(c, keys, question)
	if err != nil {
		c.Infof("Error in GetQuestions: %v", err)
		return question, keys, err
	}
	return question, keys, nil
}

func GetQuestionsWithPrevious(c appengine.Context, prev []int) ([]Question, []*datastore.Key, error) {
	question := make([]Question, NUMBER_OF_QUESTIONS)
	keys := make([]*datastore.Key, NUMBER_OF_QUESTIONS)
	max, _ := GetCountQuestions(c)
	max--
	values := getRandomValuesWithPrevious(c, NUMBER_OF_QUESTIONS, max, prev)
	c.Infof("New values = %v", values)
	for i, value := range values {
		keys[i], _ = getKeyForIndex(c, value)
	}
	err := datastore.GetMulti(c, keys, question)
	if err != nil {
		c.Infof("Error in GetQuestions: %v", err)
		return question, keys, err
	}
	return question, keys, nil
}

func GetQuestionsWithID(c appengine.Context, ids []int) ([]Question, error) {
	question := make([]Question, NUMBER_OF_QUESTIONS)
	keys := make([]*datastore.Key, NUMBER_OF_QUESTIONS)
	for i, value := range ids {
		key, err2 := getKeyForIndex(c, value)
		if err2 != nil {
			return question, err2
		} else {
			keys[i] = key
		}
	}
	err := datastore.GetMulti(c, keys, question)
	if err != nil {
		return question, err
	}
	return question, nil

}

func getRandomValues(c appengine.Context, numberOfValues, maxValue int) []int {
	numbers := make([]int, numberOfValues)
	rand.Seed(time.Now().UTC().UnixNano())
	for i, _ := range numbers {

		numbers[i] = rand.Intn(maxValue)
	}
	if containsDubble(c, numbers) {
		return numbers
	} else {
		return getRandomValues(c, numberOfValues, maxValue)
	}
}

func getRandomValuesWithPrevious(c appengine.Context, numberOfValues, maxValue int, prev []int) []int {
	numbers := make([]int, numberOfValues)
	rand.Seed(time.Now().UTC().UnixNano())
	for i, _ := range numbers {

		numbers[i] = getRandomValue(c, prev, maxValue)
	}
	if containsDubble(c, numbers) {
		return numbers
	} else {

		return getRandomValuesWithPrevious(c, numberOfValues, maxValue, prev)
	}
}

func getRandomValue(c appengine.Context, nrs []int, maxValue int) int {
	r := rand.Intn(maxValue)
	for _, a := range nrs {
		if r == a {
			return getRandomValue(c, nrs, maxValue)
		}
	}
	return r
}

func containsDubble(c appengine.Context, nr []int) bool {
	for _, numb := range nr {
		if !isAlone(c, numb, nr) {
			return false
		}
	}
	return true
}

func isAlone(c appengine.Context, nr int, numbers []int) bool {
	first := 0
	for _, value := range numbers {
		if value == nr {
			first++
		}
	}
	if first > 1 {
		return false
	}
	return true
}

func GetCountQuestions(c appengine.Context) (int, error) {
	query := datastore.NewQuery("Question").KeysOnly()
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
	if len(keys) > 0 {
		return keys[0], err
	} else {
		return nil, err
	}
}
