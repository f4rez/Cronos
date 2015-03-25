package question

import (
	time
)

type Question struct {
	ID, Level int
	Question string
	Date time.Time
}
