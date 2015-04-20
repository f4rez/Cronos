package balancer

import (
	"testing"
	"question"
)

func TestStupidQuestionBalancer(t *testing.T) {
	questions := []question.Question{
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"}}

	answers := []int{1,1,1,2,2}

	questions, err := stupidQuestionBalancer(questions, answers)

	if(err != nil) {
		t.Errorf("Could not run Balancer.")
	}

}
