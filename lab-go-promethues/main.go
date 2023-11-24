package main

import (
	"fmt"
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	io_prometheus_client "github.com/prometheus/client_model/go"
	"net/http"
	"time"
)

func main() {
	// 如果直接使用 http.ListenAndServe(":2112", nil)
	// handler 为 nil 的情况下会默认使用 DefaultServeMux，为一个全局变量
	// 任何程序在任何地方都可以修改 DefaultServeMux ，导致不安全
	// 因此我们使用 http.NewServeMux() 创建一个自定义的 ServeMux

	// 定义每一个 uri 处理器
	mux := http.NewServeMux()
	mux.Handle("/", promhttp.Handler())
	mux.Handle("/metrics", promhttp.Handler())

	mux.HandleFunc("/login", func(writer http.ResponseWriter, request *http.Request) {
		startTime := time.Now().UnixMilli()
		MetricRequestCount.Inc()
		MetricRequestCountDynamicLabel.With(prometheus.Labels{"path": "/login"}).Inc()
		endTime := time.Now().UnixMilli()
		MetricRequestDuration.Observe(float64(endTime - startTime))
		writer.Write([]byte("login"))
	})

	mux.HandleFunc("/add", func(writer http.ResponseWriter, request *http.Request) {
		startTime := time.Now().UnixMilli()
		MetricRequestCount.Inc()
		MetricRequestCountDynamicLabel.With(prometheus.Labels{"path": "/addConfig"}).Inc()
		MetricConfigTotal.Inc() // 也可以使用 set 方法直接设置值
		endTime := time.Now().UnixMilli()
		MetricRequestDuration.Observe(float64(endTime - startTime))
		writer.Write([]byte("addConfig"))
	})

	mux.HandleFunc("/get", func(writer http.ResponseWriter, request *http.Request) {
		var m = &io_prometheus_client.Metric{}
		err := MetricRequestCount.Write(m)
		if err != nil {
			writer.Write([]byte(err.Error()))
		}
		result := fmt.Sprintf("%v", *m.GetCounter().Value)
		writer.Write([]byte(result))
	})

	mux.HandleFunc("/delete", func(writer http.ResponseWriter, request *http.Request) {
		startTime := time.Now().UnixMilli()
		MetricRequestCount.Inc()
		MetricRequestCountDynamicLabel.With(prometheus.Labels{"path": "/deleteConfig"}).Inc()
		MetricConfigTotal.Dec()
		endTime := time.Now().UnixMilli()
		MetricRequestDuration.Observe(float64(endTime - startTime))
		writer.Write([]byte("deleteConfig"))
	})

	// 动态注册指标
	mux.HandleFunc("/registered", func(writer http.ResponseWriter, request *http.Request) {
		test := promauto.NewGauge(prometheus.GaugeOpts{
			Name: "test22222",
			Help: "test22222",
		})
		test.Set(10)
		writer.Write([]byte("registered"))
	})

	mux.HandleFunc("/ingest", func(writer http.ResponseWriter, request *http.Request) {
		MetricGaugeIngest.With(prometheus.Labels{"event": "login"}).Add(1)
		writer.Write([]byte("ingest"))
	})

	mux.HandleFunc("/clickhouse", func(writer http.ResponseWriter, request *http.Request) {
		MetricGaugeClickhouse.With(prometheus.Labels{"event": "login"}).Add(1)
		writer.Write([]byte("clickhouse"))
	})

	// 配置 http server 的各方面
	s := &http.Server{
		Addr:         ":2112", // 前面部分省略默认使用 http://127.0.0.1
		Handler:      mux,
		ReadTimeout:  30 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	s.ListenAndServe()
}
