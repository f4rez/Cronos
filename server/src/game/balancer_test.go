package game

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


	questions2 := []question.Question{
		question.Question{1, 1000, 500, "The question"},
		question.Question{1, 154, 500, "The question"},
		question.Question{1, 400, 500, "The question"},
		question.Question{1, 700, 500, "The question"},
		question.Question{1, 300, 500, "The question"}}


	// 1 = wrong, 2 = right
	answers := []int{1,1,1,2,2}
	answers2 := []int{2,1,2,2,2}

	// Easy to advanced users
	user500 := users.Users{"", "", "", nil, nil, 500, nil}
	user750 := users.Users{"", "", "", nil, nil, 750, nil}
	user1000 := users.Users{"", "", "", nil, nil, 1000, nil}
	user1100 := users.Users{"", "", "", nil, nil, 1100, nil}
	user1300 := users.Users{"", "", "", nil, nil, 1300, nil}
	user1700 := users.Users{"", "", "", nil, nil, 1700, nil}
	user2000 := users.Users{"", "", "", nil, nil, 2000, nil}

	userMaxLevel := 2000
	questionMaxLevel := 1000

	questionsEasy, err := humbleQuestionBalancer(questions, answers, user1000, userMaxLevel, questionMaxLevel)
	questionsAdv, err := humbleQuestionBalancer(questions, answers, user2000, userMaxLevel, questionMaxLevel)

	q500, err := humbleQuestionBalancer(questions2, answers2, user500, userMaxLevel, questionMaxLevel)
	q750, err := humbleQuestionBalancer(questions2, answers2, user750, userMaxLevel, questionMaxLevel)
	q1100, err := humbleQuestionBalancer(questions2, answers2, user1100, userMaxLevel, questionMaxLevel)
	q1300, err := humbleQuestionBalancer(questions2, answers2, user1300, userMaxLevel, questionMaxLevel)
	q1700, err := humbleQuestionBalancer(questions2, answers2, user1700, userMaxLevel, questionMaxLevel)
	q2000, err := humbleQuestionBalancer(questions2, answers2, user2000, userMaxLevel, questionMaxLevel)

	if(err != nil) {
		t.Errorf("Could not run Balancer.")
	}

	// If the answer is correct, the question level is lower for the easy user than for the advanced user.
	if(questionsEasy[3].Level < questionsAdv[3].Level) {
		t.Errorf("Is: ", questionsEasy[3].Level, questionsAdv[3].Level)
	}

	// If the answer is incorrect, the question level is lower for the advanced user than for the easy user.
	if(questionsAdv[2].Level < questionsEasy[2].Level) {
		t.Errorf("Is: ", questionsEasy[2].Level, questionsAdv[2].Level)
	}

	// Check that the more advanced the user gets, the less it affects a correct answer.
	if( q500[0].Level < q750[0].Level ||
		q750[0].Level < q1100[0].Level ||
		q1100[0].Level < q1300[0].Level ||
		q1300[0].Level < q1700[0].Level ||
		q1700[0].Level < q2000[0].Level) {
		t.Errorf("The more advanced the user is, the less it should affect a correct answer.")
	}

	// Check that the more advanced the user gets, the less it affects a correct answer.
	if(q500[1].Level > q750[1].Level ||
			q750[1].Level > q1100[1].Level ||
			q1100[1].Level > q1300[1].Level ||
			q1300[1].Level > q1700[1].Level ||
			q1700[1].Level > q2000[1].Level) {
		t.Errorf("The easier the user is, the less it should affect an incorrect answer")
	}
}

/*
func testBalancer(t *testing.T) {
	inst, err := aetest.NewInstance(nil)
	if err != nil {
		t.Fatalf("Could not create instance of aetest.")
	}

	req, err := inst.NewRequest("GET", "/", nil)
	if err != nil {
		t.Fatalf("Could not create new request.")
	}

	theGame := game.Game{

	}

	err := Balancer(req, theGame)

	if err != nil  {
		t.Errorf("Could not run Balancer.")
	}
}*/