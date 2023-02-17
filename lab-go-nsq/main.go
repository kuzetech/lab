package main

import (
	"log"
	"os"
	"os/signal"
	"syscall"
)

func main() {

	producer := createProducer()
	consumer := createConsumer()

	// Synchronously publish a single message to the specified topic.
	// Messages can also be sent asynchronously and/or in batches.
	err := producer.Publish(UseTopic, []byte("hello"))
	if err != nil {
		log.Fatal(err)
	}

	// wait for signal to exit
	sigChan := make(chan os.Signal, 1)
	signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
	<-sigChan

	// Gracefully stop the producer when appropriate (e.g. before shutting down the service)
	producer.Stop()

	// Gracefully stop the consumer.
	consumer.Stop()

}
