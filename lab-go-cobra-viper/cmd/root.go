/*
Copyright © 2023 KuzeTech <370415788@qq.com>
*/
package cmd

import (
	"fmt"
	"github.com/rs/zerolog/log"
	"lab-go-cobra-viper/internal/common"
	"os"

	"github.com/spf13/cobra"
	"github.com/spf13/viper"
)

var rootConfig *common.RootConfig = &common.RootConfig{
	ConfigFile:     "",
	LogLevel:       "info",
	PrometheusAddr: ":2112",
	RatePerSecond:  100,
	EventSize:      10,
}

// rootCmd represents the base command when called without any subcommands
var rootCmd = &cobra.Command{
	Use:   "lab-go-cobra-viper",
	Short: "A brief description of your application",
	Long: `A longer description that spans multiple lines and likely contains
examples and usage of using your application. For example:

Cobra is a CLI library for Go that empowers applications.
This application is a tool to generate the needed files
to quickly create a Cobra application.`,
	// Uncomment the following line if your bare application
	// has an action associated with it:
	Run: func(cmd *cobra.Command, args []string) {
		log.Info().Msg("3. 主命令运行")
	},
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	log.Info().Msg("1. root Execute")
	err := rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	cobra.OnInitialize(initConfig)
	cobra.OnInitialize(initFileConfig)

	cobra.OnFinalize(rootFinalize)
	cobra.OnFinalize(fileFinalize)

	// Here you will define your flags and configuration settings.
	// Cobra supports persistent flags, which, if defined here,
	// will be global for your application.
	rootCmd.PersistentFlags().StringVar(&rootConfig.ConfigFile, "config", rootConfig.ConfigFile, "config file (default is $HOME/.lab-go-cobra-viper.yaml)")
	rootCmd.PersistentFlags().StringVar(&rootConfig.LogLevel, "log-level", rootConfig.LogLevel, "log level")

	// Cobra also supports local flags, which will only run
	// when this action is called directly.
	rootCmd.Flags().BoolP("toggle", "t", false, "Help message for toggle")
}

// initConfig reads in config file and ENV variables if set.
func initConfig() {
	log.Info().Msg("2. root init config")
	if rootConfig.ConfigFile != "" {
		// Use config file from the flag.
		viper.SetConfigFile(rootConfig.ConfigFile)
	} else {
		// Find home directory.
		home, err := os.UserHomeDir()
		cobra.CheckErr(err)

		// Search config in home directory with name ".lab-go-cobra-viper" (without extension).
		viper.AddConfigPath(home)
		viper.SetConfigType("yaml")
		viper.SetConfigName(".lab-go-cobra-viper")
	}

	viper.AutomaticEnv() // read in environment variables that match

	// If a config file is found, read it in.
	if err := viper.ReadInConfig(); err == nil {
		fmt.Fprintln(os.Stderr, "Using config file:", viper.ConfigFileUsed())
	}
}

func rootFinalize() {
	log.Info().Msg("5. root Finalize")
}