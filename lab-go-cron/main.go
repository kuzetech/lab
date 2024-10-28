package main

import (
	"fmt"
	"github.com/go-co-op/gocron/v2"
	"time"
)

// 这里无法使用 printAll(items ...any) 这样的方法
func printAll(a string, b int) {
	fmt.Println(a, b)
}

func main() {
	// create a scheduler
	s, err := gocron.NewScheduler()
	if err != nil {
		panic(err)
	}

	// add a job to the scheduler
	j, err := s.NewJob(
		gocron.DurationJob(5*time.Second),
		gocron.NewTask(printAll, "hello", 1),
	)
	if err != nil {
		panic(err)
	}

	// each job has a unique id
	fmt.Println("JobId = ", j.ID())

	// start the scheduler
	s.Start()

	// block until you are ready to shut down
	waitingForAppTerm()

	// when you're done, shut it down
	err = s.Shutdown()
	if err != nil {
		panic(err)
	}
}
