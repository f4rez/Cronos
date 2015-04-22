package balancer

import (
	"net/http"
	"appengine"
	"question"
)


func Balancer (r *http.Request, QID []int, FID string, SID string, Turn bool, answers []int) {
	c := appengine.NewContext(r)

	questions, err := question.GetQuestionsWithID(c, QID)
	if err != nil {
		c.Infof("Error getting questions")
	}

	stupidQuestionBalancer(questions, answers) // TODO: Write the answer to datastore
}


// Changes the difficulty level of each question based on the current answer.
//
// questions is an array of five questions.
// answers is an array of five answers (length must be equal to length of questions).
//
// Returns the same five questions with adjusted level.
func stupidQuestionBalancer(questions []question.Question, answers []int) ([]question.Question, error) {

	for i,q := range questions {
		if(answers[i] == 0) {
			return questions, nil
		} else if(answers[i] == 1) {
			// Wrong answer
			q.Level = q.Level+10
		} else if(answers[i] == 2) {
			// Right answer
			q.Level = q.Level-10
		}
	}

	return questions, nil
}

/*
// Changes the difficulty level of each question based on:
// - the current answer
// - in relation to the other questions. (???)
// - the users difficulty level.
// - The distance to other questions. (???)
func humbleQuestionBalancer(Questions []Question, FID string, SID string, Turn bool, answers []int, r *http.Request) ([]Question, error) {
	user := nil
	upDiff := 10
	downDiff := 10

	if(Turn) { // TODO: Är det rätt håll??
		user,_,err = users.getUser(c, FID)
	} else {
		user,_,err = users.getUser(c, SID)
	}

	if(err != nil) {
		// Fallback to the stupid one.
		stupidQuestionBalancer(QID, answers, r)
		return nil, errors.New("No user was found.")
	}

	userMaxLevel, questionMaxLevel := getMaxLevels(r)

	userRatio := float64(user.Level) / userMaxLevel

	for i,q := range questions {
		if(answers[i] == 0) {
			return questions,nil
		}

		questionRatio := float64(q.Level) / questionMaxLevel

		// An advanced user does not affect the questions as much as an beginner if the answer is correct.
		downDiff = int((1.0-(userRatio*questionRatio))*10.0)

		if(answers[i] == 1) {
			// Wrong answer
			q.Level = q.Level+upDiff
		} else if(answers[i] == 2) {
			// Right answer
			q.Level = q.Level-downDiff
		}
	}
	return questions, nil
}

// Get the current max of userLevel and questionLevel
func getMaxLevels(r *http.Request) (float64, float64){
	c := appengine.NewContext(r)
	// Get the current max level. TODO: Det här görs för ofta.
	qn := datastore.NewQuery("Users").
	Order("-Level").
	Limit(1)

	var values[]maxUser	// TODO: Correct?
	qn.getAll(c, &values)
	userMaxLevel := values[0].Level

	qn := datastore.NewQuery("Questions").
	Order("-Level").
	Limit(1)

	var values[]maxUser	// TODO: Correct?
	qn.getAll(c, &values)
	questionMaxLevel := values[0].Level

}


// Count users
func countUsers(r *http.Request) int {
	c := appengine.NewContext(r)

	countUsers, err = users.GetCountUsers(c)
	if err != nil {
		c.Infof("Error getting users count")
	}
	return countUsers
}
*/
