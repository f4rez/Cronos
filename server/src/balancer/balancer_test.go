package balancer

import (
	"testing"
	"question"
	"users"
)

func TestStupidQuestionBalancer(t *testing.T) {
	questions := []question.Question{
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"}}

	// 1 = wrong, 2 = right
	answers := []int{1,1,1,2,2}

	questions, err := stupidQuestionBalancer(questions, answers)

	if(err != nil) {
		t.Errorf("Could not run Balancer.")
	}

	if(questions[0].Level != 1010) {
		t.Errorf("Expected 1010, is ", questions[0].Level)
	}

	if(questions[4].Level != 990) {
		t.Errorf("Expected 990, is ", questions[4].Level)
	}
}

func TestHumbleQuestionBalancer(t *testing.T) {
	questions := []question.Question{
		question.Question{1, 700, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 700, 500, "The question"},
		question.Question{1, 1000, 500, "The question"}}

	// 1 = wrong, 2 = right
	answers := []int{1,1,1,2,2}

	// Easy user
	userEasy := users.Users{"", "", nil, nil, nil, 1000}

	// This is a more advanced user.
	userAdv := users.Users{"", "", nil, nil, nil, 2000}

	userMaxLevel := 1000
	questionMaxLevel := 1000

	questionsEasy, err := humbleQuestionBalancer(questions, answers, userEasy, userMaxLevel, questionMaxLevel)
	questionsAdv, err := humbleQuestionBalancer(questions, answers, userAdv, userMaxLevel, questionMaxLevel)

	if(err != nil) {
		t.Errorf("Could not run Balancer.")
	}

	// If the answer is correct, the question level is lower for the easy user than for the advanced user.
	if(questionsEasy[3].Level < questionsAdv[3].Level) {
		t.Errorf("Is: ", questionsEasy[3].Level, questionsAdv[3].Level)
	}
}
