package messages

type ChallengeMessage struct {
	OppName, OppID string
}

type GamesMessage struct {
	GID, MPoints, OPoints, Turn int
	OppName, OppID, OppPic      string
	MyTurn                      bool
}

type FinishedGame struct {
	GID, MyScore, OppScore int
	OppName, OppPic        string
}

type StartMessage struct {
	Games      []GamesMessage
	Challenges []ChallengeMessage
	Finished   []FinishedGame
}
