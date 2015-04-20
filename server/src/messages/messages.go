package messages

type GameInitMessage struct {
	GID              int
	Opp_name, Opp_ID string
}

type GamesMessage struct {
	GID, MPoints, OPoints, Turn int
	OppName, OppID              string
	MyTurn                      bool
}

type StartMessage struct {
	Games      []GamesMessage
	Challenges []GameInitMessage
}
