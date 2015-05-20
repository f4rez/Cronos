package balancer

import (
	"appengine"
	"appengine/datastore"
	"errors"
	"game"
	"math"
	"net/http"
	"question"
	"strconv"
	"users"
)

//func Balancer(r *http.Request, game Game) error {
func Balancer(r *http.Request, gameID int) error {
	c := appengine.NewContext(r)
	game, _, err := game.GetGame(c, gameID)

	if err != nil {
		c.Infof("Could not get the game")
	}

	var user users.Users
	var answers []int

	userMaxLevel, questionMaxLevel := GetMaxLevels(r)

	// Get the users
	user1, _, _ := users.GetUser(c, game.FID)
	user2, _, _ := users.GetUser(c, game.SID)

	// Loop over each game round
	for _, round := range game.Rounds {
		QID := round.QuestionSID

		questions, err := question.GetQuestionsWithID(c, QID)
		if err != nil {
			c.Infof("Error getting questions")
		}

		// Loop over both users (always two)
		i := 0
		for i < 2 {

			// Balance according to each user, one at the time.
			if i == 0 {
				user = user1
				answers = round.PlayerOneAnswers
			} else {
				user = user2
				answers = round.PlayerTwoAnswerss
			}

			updatedQuestions, err := humbleQuestionBalancer(questions, answers, user, userMaxLevel, questionMaxLevel) // TODO: Write the answer to datastore, it is returned here but does not go anywhere...

			if err != nil {
				return errors.New("Some error updating questions.")
			}

			for _, q := range updatedQuestions {
				question.UpdateQuestion(c, q)
			}

			i++
		}

		// Do the Elo user balancing
		new1, new2, err := userBalancer(user1.Level, user2.Level, round.PlayerOnePoints, round.PlayerTwoPoints)

		if err != nil {
			c.Infof("Error balancing users")
		}

		user1.UpdateLevel(new1)
		user2.UpdateLevel(new2)
	}

	return nil
}

func userBalancer(u1 int, u2 int, user1points int, user2points int) (int, int, error) {
	// Scale factor
	K := 16.0
	var new1 float64
	var new2 float64

	user1 := float64(u1)
	user2 := float64(u2)

	// expected percentage for user1 to win
	est1 := 1.0 / (1.0 + math.Pow(10, (user2-user1)/400.0))

	if user1points > user2points {
		// If user1 won
		new1 = user1 + K*(1.0-est1)
		new2 = user2 + K*(0-(1.0-est1))

	} else if user1points < user2points {
		// If user2 won
		new1 = user1 + K*(0-est1)
		new2 = user2 + K*(1-(1-est1))
	} else {
		// If draw TODO
	}

	return int(new1), int(new2), nil
}

// Changes the difficulty level of each question based on the current answer.
//
// questions is an array of five questions.
// answers is an array of five answers (length must be equal to length of questions).
//
// Returns the same five questions with adjusted level.
func stupidQuestionBalancer(questions []question.Question, answers []int) ([]question.Question, error) {

	for i, _ := range questions {
		if answers[i] == 0 {
			return questions, nil
		} else if answers[i] == 1 {
			// Wrong answer
			questions[i].Level = questions[i].Level + 10
		} else if answers[i] == 2 {
			// Right answer
			questions[i].Level = questions[i].Level - 10
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

	for i, _ := range questions {
		if answers[i] == 0 {
			return questions, nil
		}

		questionRatio := float64(questions[i].Level) / questionMaxLevel

		if answers[i] == 1 {
			// Wrong answer

			// An advanced user affects the questions more than a beginner if the answer is wrong.
			upDiff = int((userRatio * questionRatio) * 10.0)
			questions[i].Level = questions[i].Level + upDiff

		} else if answers[i] == 2 {
			// Right answer

			// An advanced user does not affect the questions as much as a beginner if the answer is correct.
			downDiff = int((1.0 - (userRatio * questionRatio)) * 10.0)
			questions[i].Level = questions[i].Level - downDiff
		}
	}
	return questions, nil
}

// Get the current max of userLevel and questionLevel
func GetMaxLevels(r *http.Request) (int, int) {
	c := appengine.NewContext(r)
	// Get the current max level. TODO: Det här görs för ofta.
	qn := datastore.NewQuery("Users").
		Order("-Level").
		Limit(1)

	dbUsers := make([]users.Users, 1, 1)

	_, err := qn.GetAll(c, &dbUsers)

	if err != nil {
		c.Infof("error getting users")
	}

	c.Infof("length %s", strconv.Itoa(len(dbUsers)))

	userMaxLevel := dbUsers[0].Level

	qn = datastore.NewQuery("Questions").
		Order("-Level").
		Limit(1)

	dbQuestions := make([]question.Question, 1, 1)

	_, err = qn.GetAll(c, &dbQuestions)

	if err != nil {
		c.Infof("error getting questions")
	}
	c.Infof(dbUsers[0].Name)
	questionMaxLevel := dbQuestions[0].Level

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
