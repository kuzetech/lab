package main

import "log"

func main() {
	err := deleteByKey("test")
	log.Println(err)
}
