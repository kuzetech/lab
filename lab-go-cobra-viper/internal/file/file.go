package file

import "github.com/rs/zerolog/log"

func Run(config *Config) error {
	log.Info().Msg("4. file called")
	err := config.checkParameters()
	return err
}
