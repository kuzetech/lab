package main

import (
	"github.com/rs/zerolog"
	"os"
)

var Logger zerolog.Logger

func init() {
	zerolog.SetGlobalLevel(zerolog.InfoLevel)

	Logger = zerolog.New(os.Stdout).With().Timestamp().Caller().Logger()
}
