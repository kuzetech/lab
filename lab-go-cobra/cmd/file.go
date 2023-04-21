package cmd

import (
	"git.sofunny.io/data-analysis/turbine/misc/ingest-booster/internal/file"
	"github.com/rs/zerolog/log"
	"github.com/spf13/cobra"
)

var fileCmd = &cobra.Command{
	Use:   "file",
	Short: "generate log file",
	Run: func(cmd *cobra.Command, args []string) {
		err := file.Run(&file.Config{
			GeneratePath:  fileFlagGeneratePath,
			RatePerSecond: rootFlagRatePerSecond,
			EventSize:     rootFlagEventSize,
			ServerSize:    fileFlagServerSize,
		})
		if err != nil {
			log.Error().Err(err).Msg("run file booster error")
		}
	},
}

var (
	fileFlagGeneratePath = ""
	fileFlagServerSize   = 10
)

func init() {
	rootCmd.AddCommand(fileCmd)

	fileCmd.PersistentFlags().StringVarP(&fileFlagGeneratePath, "path", "p", fileFlagGeneratePath, "log generate path")
	fileCmd.MarkPersistentFlagRequired("path")

	fileCmd.PersistentFlags().IntVarP(&fileFlagServerSize, "server-size", "s", fileFlagServerSize, "server size")

}
