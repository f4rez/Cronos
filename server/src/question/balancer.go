package question

import (
	"net/http"
	"appengine"
)

func Balancer (r *http.Request, QID []int, FID string, SID string, Turn bool, answers []int) {
	stupidQuestionBalancer(QID, answers, r)
}

func stupidQuestionBalancer(QID []int, answers []int, r *http.Request) {
	c := appengine.NewContext(r)
	questions, err := GetQuestionsWithID(c, QID)
	if err != nil {
		c.Infof("Error getting questions")
	}

	for i,_ := range questions {
		if(answers[i] == 0) {
			return
		} else if(answers[i] == 1) {
			// Wrong answer
			questions[i].Level = questions[i].Level+1
		} else if(answers[i] == 2) {
			// Right answer
			questions[i].Level = questions[i].Level-1
		}
	}
	/*
	countUsers, err = users.GetCountUsers(c)
	if err != nil {
		c.Infof("Error getting users count")
	}
	*/
}
