package common

type RootConfig struct {
	ConfigFile     string
	LogLevel       string
	PrometheusAddr string
	RatePerSecond  int
	EventSize      int
}
