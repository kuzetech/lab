package main

import (
	"os"
	"os/signal"
	"syscall"
)

func waitingForAppTerm() {
	ch := make(chan os.Signal, 1)
	signal.Notify(ch, syscall.SIGTERM, syscall.SIGINT)
	<-ch
}
