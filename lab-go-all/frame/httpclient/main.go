package main

import (
	"github.com/rs/zerolog/log"
	"net/http"
	"time"
)

func main() {
	client := &http.Client{
		Timeout: time.Second * 10,
	}

	_, err := client.Get("http://localhost:8081/header")
	if err != nil {
		log.Err(err)
	}
}
