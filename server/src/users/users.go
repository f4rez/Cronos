package users

import (
	"appengine"
	"appengine/datastore"
	"appengine/user"
	"encoding/json"
	"fmt"
	"net/http"
)

type Users struct {
	Oid, Name, Picture, Token, RealName string
	FriendList                          []Friend       `json:"-"`
	ChallengeList                       []Challange    `json:"-"`
	Games                               []int          `json:"-"`
	FinishedGames                       []FinishedGame `json:"-"`
	Won, Draw, Lost, Level              int
}

type FinishedGame struct {
	GID, MyScore, OppScore int
	OppName, OppPic        string
}

type Friend struct {
	Won, Lost, Draw int
	Name, FriendID  string
}

type Challange struct {
	OppName, OppID string
}

type FoundUser struct {
	Usr      Users
	IsFriend bool
}

func UserKey(c appengine.Context) *datastore.Key {
	return datastore.NewKey(c, "User", "default_user", 0, nil)
}

func (users *Users) SaveUser(c appengine.Context) {
	key := datastore.NewIncompleteKey(c, "Users", UserKey(c))
	_, err := datastore.Put(c, key, users)
	if err != nil {
		c.Infof("Error in saving user: ", err)
	}
}

func (users *Users) UpdateUser(c appengine.Context, key *datastore.Key) {
	c.Infof("Updating user : %v", users)
	_, err := datastore.Put(c, key, users)
	if err != nil {
		c.Infof("Error in updating user: ", err)
	}
}

func (users *Users) AddGame(gid int) {
	users.Games = append(users.Games, gid)
}

func (users *Users) AddFinishedGame(gid int, opName string, myScore, oppScore int, oppPic string) {
	f := new(FinishedGame)
	f.GID = gid
	f.OppName = opName
	f.MyScore = myScore
	f.OppScore = oppScore
	f.OppPic = oppPic
	users.FinishedGames = append(users.FinishedGames, *f)
}

func (users *Users) UpdateLevel(newLevel int) {
	users.Level = newLevel
}

func MakeUser(c appengine.Context) (Users, error) {
	users := new(Users)
	u := user.Current(c)
	if u != nil {
		users.Oid = u.ID
		users.Name = u.String()
		users.Level = 1000
		return *users, nil
	} else {
		err := new(error)
		return *users, *err
	}
}

func GetUser(c appengine.Context, Oid string) (Users, *datastore.Key, error) {
	mUser := make([]Users, 1, 1)
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(1).
		Filter("Oid =", Oid)
	if key, err := qn.GetAll(c, &mUser); len(key) > 0 {
		c.Infof("Fetched User: %v", mUser[1].Name)
		return mUser[1], key[0], nil
	} else {
		u := new(Users)
		return *u, nil, err
	}

}

func getUsers(c appengine.Context, ids []Friend) ([]Users, []*datastore.Key, error) {
	c.Infof("ids: %v", ids)
	friends := make([]Users, len(ids), 100)
	keys := make([]*datastore.Key, len(ids), 100)
	for i, value := range ids {
		key, err2 := getKeyForIndex(c, value.FriendID)
		keys[i] = key
		if err2 != nil {
			c.Infof("Error getUsers1: %v", err2)
			return friends, keys, err2
		}
	}
	c.Infof("keys: %v", keys)
	err := datastore.GetMulti(c, keys, friends)
	if err != nil {
		c.Infof("Error getUsers2: %v, keys: %v", err, keys)
		return friends, keys, err
	}
	c.Infof("Friends: %v, keys: %v", friends, keys)
	return friends, keys, nil

}

func GetCountUsers(c appengine.Context) (int, error) {
	query := datastore.NewQuery("Question").
		KeysOnly()
	count, err := query.Count(c)
	return count, err
}

func GetFriendList(c appengine.Context, id string) ([]Users, error) {
	mUser, _, err := GetUser(c, id)
	if err == nil {
		users, _, err2 := getUsers(c, mUser.FriendList)
		if err2 == nil {
			return users, nil
		}
		return make([]Users, 1), err2
	}
	return make([]Users, 1), err

}

func getKeyForIndex(c appengine.Context, id string) (*datastore.Key, error) {
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(1).
		Filter("Oid =", id).
		KeysOnly()
	keys, err := qn.GetAll(c, nil)
	if len(keys) > 0 {
		return keys[0], err
	} else {
		return nil, err
	}
}

func AddFriend(c appengine.Context, mOid, fOid string) error {
	c.Infof("Adding Oid: %v to %v friendlist", fOid, mOid)
	mUser, mKey, err := GetUser(c, mOid)
	fUser, _, err2 := GetUser(c, fOid)
	if mKey == nil {
		c.Infof("Error adding friend: %v", err)
		return err
	}
	if err2 != nil {
		c.Infof("Error adding friend: %v", err2)
		return err2
	}
	c.Infof("Adding friend to %v", mUser)
	mUser.AddFriend(fUser)
	mUser.UpdateUser(c, mKey)
	return nil
}

func RemoveFriend(c appengine.Context, mOid, fOid string) error {
	c.Infof("Removing Oid: %v from %v friendlist", fOid, mOid)
	mUser, mKey, err := GetUser(c, mOid)
	if err == nil {
		c.Infof("")
		mUser.RemoveFriend(fOid)
		mUser.UpdateUser(c, mKey)
		return nil
	} else {
		c.Infof("sss:%v", err)
		return err
	}

}

func ChallengeFriend(c appengine.Context, mOid, fOid string) error {
	u := user.Current(c)
	mUser, mKey, err := GetUser(c, fOid)
	if err != nil {
		mUser.ChallengeFrom(mOid, u.String())
		mUser.UpdateUser(c, mKey)
		return nil
	} else {
		return err
	}

}

func findUsersByName(c appengine.Context, name string) ([]Users, error) {
	mUser := make([]Users, 0, 10)
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(10).
		Filter("Name =", name)
	if key, err := qn.GetAll(c, &mUser); len(key) > 0 {
		return mUser, nil
	} else {

		return mUser, err
	}
}

func GameEnded(c appengine.Context, GID int, FID, SID string, one, two int, onePic, twoPic string) error {
	usr1, key1, err1 := GetUser(c, FID)
	usr2, key2, err2 := GetUser(c, SID)
	if err1 != nil {
		return err1
	}
	if err2 != nil {
		return err2
	}
	usr1.RemoveGame(GID)
	usr2.RemoveGame(GID)
	usr1.AddFinishedGame(GID, usr2.Name, one, two, twoPic)
	usr2.AddFinishedGame(GID, usr1.Name, two, one, onePic)
	if one > two {
		usr1.Won++
		usr2.Lost++
	} else if two > one {
		usr2.Won++
		usr1.Lost++
	} else {
		usr1.Draw++
		usr2.Draw++
	}
	ChangeFriendStatIfFriends(usr1, usr2, one, two)
	usr1.UpdateUser(c, key1)
	usr2.UpdateUser(c, key2)
	return nil
}

func ChangeFriendStatIfFriends(usr1, usr2 Users, one, two int) {
	for _, f := range usr1.FriendList {
		if one > two {
			f.Won++
		} else if two > one {
			f.Lost++
		} else {
			f.Draw++
		}
	}
	for _, f2 := range usr2.FriendList {
		if one > two {
			f2.Lost++
		} else if two > one {
			f2.Won++
		} else {
			f2.Draw++
		}
	}

}

func SearchUsers(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		c.Infof("Not logged in")
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	typee := r.FormValue("type")
	parameter := r.FormValue("search")
	usr, _, uErr := GetUser(c, u.ID)
	if uErr != nil {
		c.Infof("Error getting user : %v", uErr)
		http.Error(w, uErr.Error(), http.StatusInternalServerError)
		return
	}
	switch typee {
	case "Name":
		users, err := findUsersByName(c, parameter)
		if err != nil {
			c.Infof("Error : %v", err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		mess := make([]FoundUser, len(users))
		for i, mUser := range users {
			tmp := new(FoundUser)
			tmp.Usr = mUser
			tmp.IsFriend = false
			for _, friend := range usr.FriendList {
				if friend.FriendID == mUser.Oid {
					tmp.IsFriend = true
					break
				}
			}
			mess[i] = *tmp
		}

		ret, err2 := json.Marshal(mess)
		if err2 != nil {
			c.Infof("Error marshal: %v", err2)
			http.Error(w, err2.Error(), http.StatusInternalServerError)
			return
		}
		c.Infof("Sending: %v", string(ret))
		fmt.Fprintf(w, string(ret))
		break
	case "ID":
		c.Infof("D")
		users, _, err := GetUser(c, parameter)
		if err != nil {
			c.Infof("Error : %v", err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		mess := make([]FoundUser, 1)

		tmp := new(FoundUser)
		tmp.Usr = users
		tmp.IsFriend = false
		for _, friend := range usr.FriendList {
			if friend.FriendID == users.Oid {
				tmp.IsFriend = true
				break
			}
		}
		mess[0] = *tmp

		ret, err2 := json.Marshal(mess)
		if err2 != nil {
			c.Infof("Error marshal: %v", err2)
			http.Error(w, err2.Error(), http.StatusInternalServerError)
			return
		}
		c.Infof("Sending: %v", string(ret))
		fmt.Fprintf(w, string(ret))
		break
	}

}

func GetFriendListHandler(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u == nil {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	if !IsUserSignedIn(c) {
		fmt.Fprintf(w, "Not Registerd")
		return
	}
	friendList, err := GetFriendList(c, u.ID)
	if err != nil {
		c.Infof("Error marshal: %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	fString, _ := json.Marshal(friendList)
	fmt.Fprintf(w, string(fString))
}

func FriendHandler(w http.ResponseWriter, r *http.Request) {
	friend_id := r.FormValue("friend_id")
	action := r.FormValue("action") // add, remove, challenge
	c := appengine.NewContext(r)
	u := user.Current(c)
	if u != nil {
		var err error
		if !IsUserSignedIn(c) {
			fmt.Fprintf(w, "Not Registerd")
			return
		}
		switch action {
		case "add":
			err = AddFriend(c, u.ID, friend_id)
		case "remove":
			err = RemoveFriend(c, u.ID, friend_id)
		case "challenge":
			err = ChallengeFriend(c, u.ID, friend_id)
		}
		if err != nil {
			c.Infof("Error preforming action: %v, Error: %v", action, err)
			http.Error(w, err.Error(), http.StatusInternalServerError)

		} else {
			fmt.Fprintf(w, "ID: "+u.ID+"\nFriend id: "+friend_id+"\nAction: "+action+"\nThe friend list")
		}
	} else {
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}

}

func IsUserSignedIn(c appengine.Context) bool {
	u := user.Current(c)
	c.Infof("User: %v", u)
	if u == nil {
		return false
	}
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(1).
		Filter("Oid =", u.ID)
	count, err := qn.Count(c)
	if err != nil {
		return false
	}
	c.Infof("count = %v", count)
	if count > 0 {
		return true
	} else {
		return false
	}
}

func RegisterUser(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	pic := r.FormValue("pic")
	real_name := r.FormValue("real_name")

	u := user.Current(c)
	c.Infof("User: ", u)
	if u == nil {
		fmt.Fprintf(w, "Error, not signed in")
	}
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(1).
		Filter("Oid =", u.ID)
	count, err := qn.Count(c)
	if err != nil {
		c.Infof("Error : %v", err)
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	c.Infof("count = %v", c)
	if count > 0 {
		usr, _, uErr := GetUser(c, u.ID)
		if uErr != nil {
			c.Infof("Error : %v", uErr)
			http.Error(w, uErr.Error(), http.StatusInternalServerError)
			return
		}
		mess, jErr := json.Marshal(usr)
		if jErr != nil {
			c.Infof("Error : %v", jErr)
			http.Error(w, jErr.Error(), http.StatusInternalServerError)
			return
		}
		fmt.Fprintf(w, string(mess))
		return
	} else {
		users, _ := MakeUser(c)
		if users.Oid != "" {
			c.Infof("Saving user on registring ID = %v", users.Oid)
			users.Picture = pic
			users.RealName = real_name
			users.SaveUser(c)
			mess, jErr := json.Marshal(users)
			if jErr != nil {
				c.Infof("Error : %v", jErr)
				http.Error(w, jErr.Error(), http.StatusInternalServerError)
				return
			}
			fmt.Fprintf(w, string(mess))
			return
		} else {
			c.Infof("Error login: %v", users)
			url, _ := user.LoginURL(c, "/registerNewUser")
			fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		}
	}
}

func (u *Users) AddFriend(us Users) {
	friend := new(Friend)
	friend.FriendID = us.Oid
	friend.Name = us.Name
	friend.Draw = 0
	friend.Won = 0
	friend.Lost = 0
	u.FriendList = append(u.FriendList, *friend)
}

func (u *Users) RemoveFriend(Oid string) {
	mpos := posFriend(u.FriendList, Oid)
	if mpos < 0 {
		return
	}
	first := u.FriendList[0:mpos]
	second := u.FriendList[(mpos + 1):len(u.FriendList)]
	u.FriendList = append(first, second...)
}

func (u *Users) RemoveGame(Oid int) {
	mpos := posInt(u.Games, Oid)
	if mpos < 0 {
		return
	}
	first := u.Games[0:mpos]
	second := u.Games[(mpos + 1):len(u.Games)]
	u.Games = append(first, second...)
}

func (u *Users) RemoveFinishedGame(Oid int) {
	mpos := posFinishedGame(u.FinishedGames, Oid)
	if mpos < 0 {
		return
	}
	first := u.FinishedGames[0:mpos]
	second := u.FinishedGames[(mpos + 1):len(u.FinishedGames)]
	u.FinishedGames = append(first, second...)
}

func (u *Users) ChallengeFrom(Oid, Name string) {
	challange := *new(Challange)
	challange.OppName = Name
	challange.OppID = Oid
	u.ChallengeList = append(u.ChallengeList, challange)
}

func (u *Users) RemoveChallenge(Oid string) {
	mpos := posChall(u.ChallengeList, Oid)
	if mpos < 0 {
		return
	}
	first := u.ChallengeList[0:mpos]
	second := u.ChallengeList[(mpos + 1):len(u.ChallengeList)]
	u.ChallengeList = append(first, second...)
}

func pos(slice []string, value string) int {
	for p, v := range slice {
		if v == value {
			return p
		}
	}
	return -1
}

func posInt(slice []int, value int) int {
	for p, v := range slice {
		if v == value {
			return p
		}
	}
	return -1
}

func posFinishedGame(slice []FinishedGame, value int) int {
	for p, v := range slice {
		if v.GID == value {
			return p
		}
	}
	return -1
}

func posFriend(slice []Friend, value string) int {
	for p, v := range slice {
		if v.FriendID == value {
			return p
		}
	}
	return -1
}

func posChall(slice []Challange, value string) int {
	for p, v := range slice {
		if v.OppID == value {
			return p
		}
	}
	return -1
}
