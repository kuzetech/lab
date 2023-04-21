package cmd

import (
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"github.com/rs/zerolog"
	"github.com/rs/zerolog/log"
	"net/http"
	"os"
	"time"
)

func SetupLogging(level string) error {
	lv, err := zerolog.ParseLevel(level)
	if err != nil {
		return err
	}
	zerolog.SetGlobalLevel(lv)
	log.Logger = log.Output(zerolog.ConsoleWriter{
		Out:        os.Stderr,
		NoColor:    true,
		TimeFormat: time.RFC3339,
	})
	return nil
}

func StartPrometheusClient(listenAddr string) {
	log.Info().Str("addr", listenAddr).Msg("exposing metrics")

	mux := http.NewServeMux()
	handler := promhttp.Handler()

	mux.Handle("/", handler)
	mux.Handle("/metrics", handler)

	s := http.Server{
		Handler: mux,
		Addr:    listenAddr,
	}
	go func() {
		panic(s.ListenAndServe())
	}()
}
