package balancer

import (
	"net/http"
	"appengine"
	"question"
	"errors"
	"users"
	"appengine/datastore"
)


func Balancer (r *http.Request, QID []int, FID string, SID string, Turn bool, answers []int) (error) {
	c := appengine.NewContext(r)
	var user users.Users

	questions, err := question.GetQuestionsWithID(c, QID)
	if err != nil {
		c.Infof("Error getting questions")
	}

	// Get the current user
	if(Turn) { // TODO: Är det rätt håll??
		user,_,err = users.GetUser(c, FID)
	} else {
		user,_,err = users.GetUser(c, SID)
	}

	if(err != nil) {
		// Fallback to the stupid one.
		stupidQuestionBalancer(questions, answers) // TODO: Write the answer to datastore, it is returned here but does not go anywhere...
		return errors.New("No user was found.")
	}

	userMaxLevel, questionMaxLevel := getMaxLevels(r)
	humbleQuestionBalancer(questions, answers, user, userMaxLevel, questionMaxLevel) // TODO: Write the answer to datastore, it is returned here but does not go anywhere...

	return nil
}


// Changes the difficulty level of each question based on the current answer.
//
// questions is an array of five questions.
// answers is an array of five answers (length must be equal to length of questions).
//
// Returns the same five questions with adjusted level.
func stupidQuestionBalancer(questions []question.Question, answers []int) ([]question.Question, error) {

	for i,_ := range questions {
		if(answers[i] == 0) {
			return questions, nil
		} else if(answers[i] == 1) {
			// Wrong answer
			questions[i].Level = questions[i].Level+10
		} else if(answers[i] == 2) {
			// Right answer
			questions[i].Level = questions[i].Level-10
		}
	}

	return questions, nil
}


// Changes the difficulty level of each question based on:
// - the current answer
// - in relation to the other questions. (???)
// - the users difficulty level.
// - The distance to other questions. (???)
func humbleQuestionBalancer(questions []question.Question, answers []int, user users.Users, userMaxLevelint int, questionMaxLevelint int) ([]question.Question, error) {
	upDiff := 10
	downDiff := 10

	userMaxLevel := float64(userMaxLevelint)
	questionMaxLevel := float64(userMaxLevelint)

	userRatio := float64(user.Level) / userMaxLevel

	for i,_ := range questions {
		if(answers[i] == 0) {
			return questions,nil
		}

		questionRatio := float64(questions[i].Level) / questionMaxLevel

		// An advanced user does not affect the questions as much as an beginner if the answer is correct.
		downDiff = int((1.0-(userRatio*questionRatio))*10.0)

		if(answers[i] == 1) {
			// Wrong answer
			questions[i].Level = questions[i].Level+upDiff
		} else if(answers[i] == 2) {
			// Right answer
			questions[i].Level = questions[i].Level-downDiff
		}
	}
	return questions, nil
}

// Get the current max of userLevel and questionLevel
func getMaxLevels(r *http.Request) (int, int){
	c := appengine.NewContext(r)
	// Get the current max level. TODO: Det här görs för ofta.
	qn := datastore.NewQuery("Users").
	Order("-Level").
	Limit(1)

	var values []users.Users

	t := qn.Run(c)
	t.Next(values)
	userMaxLevel := values[0].Level

	qn = datastore.NewQuery("Questions").
	Order("-Level").
	Limit(1)
	
	t = qn.Run(c)
	t.Next(values)
	questionMaxLevel := values[0].Level

	return userMaxLevel, questionMaxLevel
}


// Count users
func countUsers(r *http.Request) int {
	c := appengine.NewContext(r)

	count, err := users.GetCountUsers(c)
	if err != nil {
		c.Infof("Error getting users count")
	}
	return count
}
