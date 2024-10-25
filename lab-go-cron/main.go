package main

import (
	"fmt"
	"github.com/go-co-op/gocron/v2"
	"time"
)

func main() {
	// create a scheduler
	s, err := gocron.NewScheduler()
	if err != nil {
		// handle error
	}

	// add a job to the scheduler
	j, err := s.NewJob(
		gocron.DurationJob(
			10*time.Second,
		),
		gocron.NewTask(
			func(a string, b int) {
				// do things
				fmt.Println(fmt.Sprintf("%s - %d", a, b))
			},
			"hello",
			1,
		),
	)
	if err != nil {
		// handle error
	}
	// each job has a unique id
	fmt.Println(j.ID())

	// start the scheduler
	s.Start()

	// block until you are ready to shut down
	waitingForAppTerm()

	// when you're done, shut it down
	err = s.Shutdown()
	if err != nil {
		// handle error
	}
}
