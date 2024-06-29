package rpc

import (
	"log"
	"net"
	"net/http"
	"net/rpc"
)

type User struct {
	ID   int
	Name string
}

// GetUser rpc 方法
func (u *User) GetUser(id int, user *User) error {
	userMap := map[int]User{
		1: {ID: 1, Name: "frank"},
		2: {ID: 2, Name: "lucy"},
	}
	if userInfo, ok := userMap[id]; ok {
		*user = userInfo
	}
	return nil
}

func runServer() {
	_ = rpc.Register(new(User))
	rpc.HandleHTTP()
	listener, err := net.Listen("tcp", ":8081")
	if err != nil {
		log.Fatal(err)
	}
	err = http.Serve(listener, nil)
	if err != nil {
		log.Fatal(err)
	}
}
