/*
Copyright © 2023 KuzeTech <370415788@qq.com>
*/
package cmd

import (
	"github.com/rs/zerolog/log"
	"github.com/spf13/cobra"
	"lab-go-cobra-viper/internal/file"
)

var fileConfig *file.FileConfig = &file.FileConfig{
	GeneratePath: "",
	ServerSize:   10,
}

// fileCmd represents the file command
var fileCmd = &cobra.Command{
	Use:   "file",
	Short: "A brief description of your command",
	Long: `A longer description that spans multiple lines and likely contains examples
and usage of using your command. For example:

Cobra is a CLI library for Go that empowers applications.
This application is a tool to generate the needed files
to quickly create a Cobra application.`,
	Run: func(cmd *cobra.Command, args []string) {
		err := file.Run(rootConfig, fileConfig)
		if err != nil {
			log.Error().Err(err).Msg("run file booster error")
		}
	},
}

func init() {
	rootCmd.AddCommand(fileCmd)

	// Here you will define your flags and configuration settings.

	// Cobra supports Persistent Flags which will work for this command
	// and all subcommands, e.g.:
	// fileCmd.PersistentFlags().String("foo", "", "A help for foo")

	// Cobra supports local flags which will only run when this command
	// is called directly, e.g.:
	// fileCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")

	fileCmd.PersistentFlags().StringVarP(&fileConfig.GeneratePath, "path", "p", fileConfig.GeneratePath, "log generate path")
	fileCmd.MarkPersistentFlagRequired("path")

	fileCmd.PersistentFlags().IntVarP(&fileConfig.ServerSize, "server-size", "s", fileConfig.ServerSize, "server size")
}

func initFileConfig() {
	log.Info().Msg("3. file init config")
}

func fileFinalize() {
	log.Info().Msg("6. file Finalize")
}
