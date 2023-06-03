package server

import (
	"errors"
	"github.com/rs/zerolog/log"
)

func Run() error {
	log.Info().Msg("error called")
	return errors.New("test")
}
