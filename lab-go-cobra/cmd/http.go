package cmd

import (
	"git.sofunny.io/data-analysis/turbine/misc/ingest-booster/internal/http"
	"github.com/rs/zerolog/log"
	"github.com/spf13/cobra"
)

var httpCmd = &cobra.Command{
	Use:   "http",
	Short: "A brief description of your command",
	Run: func(cmd *cobra.Command, args []string) {
		err := http.Run()
		if err != nil {
			log.Error().Err(err).Msg("run http booster error")
		}
	},
}

func init() {
	rootCmd.AddCommand(httpCmd)
}
