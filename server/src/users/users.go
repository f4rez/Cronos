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
	Oid           string
	Name          string
	FriendList    []string `json:"-"`
	ChallengeList []string `json:"-"`
	Level         int
	Games         []int `json:"-"`
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

func MakeUser(c appengine.Context) (Users, error) {
	users := new(Users)
	u := user.Current(c)
	if u != nil {
		users.Oid = u.ID
		users.Name = u.String()
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

func getUsers(c appengine.Context, ids []string) ([]Users, []*datastore.Key, error) {
	c.Infof("ids: %v", ids)
	friends := make([]Users, len(ids), 100)
	keys := make([]*datastore.Key, len(ids), 100)
	for i, value := range ids {
		key, err2 := getKeyForIndex(c, value)
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
	if mKey == nil {
		c.Infof("Error adding friend: %v", err)
		return err

	} else {
		c.Infof("Adding friend to %v", mUser)
		mUser.AddFriend(fOid)
		mUser.UpdateUser(c, mKey)
		return nil
	}

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
	c.Infof("Chalenging Oid: %v, mOid = %v", fOid, mOid)
	mUser, mKey, err := GetUser(c, fOid)
	if err != nil {
		mUser.ChallengeFrom(mOid)
		mUser.UpdateUser(c, mKey)
		return nil
	} else {
		return err
	}

}

func findUsersByName(c appengine.Context, name string) ([]Users, error) {
	mUser := make([]Users, 1, 10)
	qn := datastore.NewQuery("Users").
		Ancestor(UserKey(c)).
		Limit(10).
		Filter("Name =", name)
	if key, err := qn.GetAll(c, &mUser); len(key) > 0 {
		c.Infof("Fetched User: %v", mUser[1].Name)
		return mUser, nil
	} else {

		return mUser, err
	}
}

func SearchUsers(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)
	u := user.Current(c)
	c.Infof("A")
	if u == nil {
		c.Infof("Not logged in")
		url, _ := user.LoginURL(c, "/")
		fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		return
	}
	typee := r.FormValue("type")
	parameter := r.FormValue("search")

	switch typee {
	case "Name":
		users, err := findUsersByName(c, parameter)
		if err != nil {
			c.Infof("Error : %v", err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		ret, err2 := json.Marshal(users)
		if err2 != nil {
			c.Infof("Error marshal: %v", err2)
			http.Error(w, err2.Error(), http.StatusInternalServerError)
			return
		}
		fmt.Fprintf(w, string(ret))
		break
	case "ID":
		users, _, err := GetUser(c, parameter)
		if err != nil {
			c.Infof("Error : %v", err)
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		ret, err2 := json.Marshal(users)
		if err2 != nil {
			c.Infof("Error marshal: %v", err2)
			http.Error(w, err2.Error(), http.StatusInternalServerError)
			return
		}
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

func RegisterUser(w http.ResponseWriter, r *http.Request) {
	c := appengine.NewContext(r)

	u := user.Current(c)
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
		return
	} else {
		users, _ := MakeUser(c)
		if users.Oid != "" {
			c.Infof("Saving user on registring ID = %v", users.Oid)
			users.SaveUser(c)
			return
		} else {
			c.Infof("Error login: %v", users)
			url, _ := user.LoginURL(c, "/registerNewUser")
			fmt.Fprintf(w, `<a href="%s">Sign in or register</a>`, url)
		}
	}
}

func (u *Users) AddFriend(Oid string) {
	u.FriendList = append(u.FriendList, Oid)
}

func (u *Users) RemoveFriend(Oid string) {
	mpos := pos(u.FriendList, Oid)
	if mpos < 0 {
		return
	}
	first := u.FriendList[0:mpos]
	second := u.FriendList[(mpos + 1):len(u.FriendList)]
	u.FriendList = append(first, second...)
}

func (u *Users) ChallengeFrom(Oid string) {
	u.ChallengeList = append(u.ChallengeList, Oid)
}

func (u *Users) RemoveChallenge(Oid string) {
	mpos := pos(u.ChallengeList, Oid)
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
