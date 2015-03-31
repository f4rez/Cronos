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
		c.Infof("Error in saving question: ", err)
	}
}

// Return true if there exist a question with the same text in datastore.
func HasQuestion(c appengine.Context, qu Question) bool {
	q := datastore.NewQuery("Question").Filter("Question =", qu.Question)

	var values []Question
	q.GetAll(c, &values)

	if(len(values) == 0){
		return false
	}
	return true
}

func GetQuestions(c appengine.Context) ([]Question, []*datastore.Key, error) {
	question := make([]Question, 5, 10)
	keys := make([]*datastore.Key, 5, 10)
	max, _ := GetCountQuestions(c)
	max--
	values := getRandomValues(c, 5, max)
	for i, value := range values {
		keys[i], _ = getKeyForIndex(c, value)
	}
	err := datastore.GetMulti(c, keys, question)
	a := len(question)
	b := len(keys)
	c.Infof("Error getQuestions = %v, Len of Questions = %v, len of keys %v", err, a, b)
	return question, keys, err
}

func GetQuestionsWithPrevious(c appengine.Context, prev []int) ([]Question, []*datastore.Key, error) {
	question := make([]Question, 5, 10)
	keys := make([]*datastore.Key, 5, 10)
	max, _ := GetCountQuestions(c)
	max--
	values := getRandomValuesWithPrevious(c, 5, max, prev)
	for i, value := range values {
		keys[i], _ = getKeyForIndex(c, value)
	}
	err := datastore.GetMulti(c, keys, question)
	a := len(question)
	b := len(keys)
	c.Infof("Error getQuestions = %v, Len of Questions = %v, len of keys %v", err, a, b)
	return question, keys, err
}

func GetQuestionsWithID(c appengine.Context, ids []int) ([]Question, error) {
	question := make([]Question, 5, 10)
	keys := make([]*datastore.Key, 5, 10)
	for i, value := range ids {
		keys[i], _ = getKeyForIndex(c, value)
	}
	err := datastore.GetMulti(c, keys, question)
	
	return question, err

}

func getRandomValues(c appengine.Context, numberOfValues, maxValue int) []int {
	numbers := make([]int, numberOfValues)
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

func AddSomeQuestionsForTesting(c appengine.Context) {
	a := Question{1, 2, 30, "hehe1"}
	b := Question{2, 21, 31, "hehe2"}
	cc := Question{3, 22, 32, "hehe3"}
	d := Question{4, 23, 33, "hehe4"}
	e := Question{5, 24, 34, "hehe5"}
	f := Question{6, 25, 35, "hehe6"}
	g := Question{7, 26, 36, "hehe7"}
	h := Question{8, 27, 37, "hehe8"}
	i := Question{9, 28, 38, "hehe9"}
	j := Question{0, 29, 39, "hehe99"}
	k := Question{10, 30, 40, "hehe999"}
	l := Question{11, 31, 41, "hehe9999"}

	SaveQuestion(c, a)
	SaveQuestion(c, b)
	SaveQuestion(c, cc)
	SaveQuestion(c, d)
	SaveQuestion(c, e)
	SaveQuestion(c, f)
	SaveQuestion(c, g)
	SaveQuestion(c, h)
	SaveQuestion(c, i)
	SaveQuestion(c, j)
	SaveQuestion(c, k)
	SaveQuestion(c, l)

}
