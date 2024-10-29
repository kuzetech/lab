package main

import (
	"fmt"
	"github.com/google/uuid"
	"os"
	"os/signal"
	"syscall"
)

func waitingForAppTerm() {
	ch := make(chan os.Signal, 1)
	signal.Notify(ch, syscall.SIGTERM, syscall.SIGINT)
	<-ch
}

func getJobName(index string) string {
	return fmt.Sprintf("%s-%s", index, uuid.New().String())
}
