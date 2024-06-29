package rpc

import (
	"fmt"
	"log"
	"net/rpc"
)

var client *rpc.Client

func runClient() {
	c, err := rpc.DialHTTP("tcp", ":8081")
	if err != nil {
		log.Fatal(err)
	}
	client = c
}

func callSync() {
	id := 1
	var user User
	_ = client.Call("User.GetUser", id, &user)
	fmt.Println(user)
}

func callAsync() {
	id := 2
	var user User
	userCall := client.Go("User.GetUser", id, &user, nil)
	if replyCall := <-userCall.Done; replyCall != nil {
		fmt.Println(user)
	}
}
