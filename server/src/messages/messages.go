package messages

type ChallengeMessage struct {
	OppName, OppID string
}

type GamesMessage struct {
	GID, MPoints, OPoints, Turn int
	OppName, OppID              string
	MyTurn                      bool
}

type FinishedGame struct {
	GID, MyScore, OppScore int
	OppName                string
}

type StartMessage struct {
	Games      []GamesMessage
	Challenges []ChallengeMessage
	Finished   []FinishedGame
}
