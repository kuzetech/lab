package cmd

import (
	"fmt"
	"github.com/spf13/cobra"
	"os"
)

var rootCmd = &cobra.Command{
	Use:   "ingest-booster",
	Short: "A brief description of your application",
}

var (
	rootFlagLogLevel       = "info"
	rootFlagPrometheusAddr = ":2112"
	rootFlagRatePerSecond  = 100
	rootFlagEventSize      = 10
)

func Execute() {
	err := SetupLogging(rootFlagLogLevel)
	if err != nil {
		fmt.Println("zerolog init error : ", err.Error())
		os.Exit(1)
	}

	StartPrometheusClient(rootFlagPrometheusAddr)

	err = rootCmd.Execute()
	if err != nil {
		os.Exit(1)
	}
}

func init() {
	rootCmd.PersistentFlags().StringVarP(&rootFlagLogLevel, "log-level", "l", rootFlagLogLevel, "log level")
	rootCmd.PersistentFlags().StringVarP(&rootFlagPrometheusAddr, "prometheus-addr", "a", rootFlagPrometheusAddr, "prometheus listen address")

	rootCmd.PersistentFlags().IntVarP(&rootFlagRatePerSecond, "rate", "r", rootFlagRatePerSecond, "generate rate per second")
	rootCmd.PersistentFlags().IntVarP(&rootFlagEventSize, "event-size", "e", rootFlagEventSize, "generate event size")
	// rootCmd.MarkPersistentFlagRequired("rate")
}
