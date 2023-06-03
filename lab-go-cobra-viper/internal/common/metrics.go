package common

import (
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"github.com/rs/zerolog/log"
	"net/http"
)

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
