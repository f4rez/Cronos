package users

import (
	"appengine"
	"appengine/datastore"
	"appengine/user"
)

type Users struct {
	Oid, Name                 string
	FriendList, ChallengeList []string
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
		c.Infof("User: %v key %v", mUser[1], key[0])
		return mUser[1], key[0], nil
	} else {
		c.Infof("Err: %v, user %v", err, mUser[2])
		u := new(Users)
		return *u, nil, err
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
