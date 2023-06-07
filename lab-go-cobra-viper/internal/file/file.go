package file

import (
	"github.com/rs/zerolog/log"
	"lab-go-cobra-viper/internal/common"
)

func Run(rootConfig *common.RootConfig, modeConfig *FileConfig) error {
	log.Info().Msg("4. file called")
	log.Info().Msg(rootConfig.LogLevel)
	log.Info().Msg(modeConfig.GeneratePath)
	return nil
}
