package main

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"time"
)

/*
	特别注意：
	如果使用 prometheus.New* 则还需要注册 prometheus.MustRegister(MyHistogram)
	直接使用 promauto.New* 则自动注册
*/

/*
	每种监控指标类型基本都有三个方法
	NewCounter		基本方法
	NewCounterVec	指定 lable，可以实现
	NewCounterFunc	上报的值为 func 返回值
*/

var (
	constLabels = map[string]string{
		"my_label": "test",
	}

	MetricRequestCount = promauto.NewCounter(prometheus.CounterOpts{
		Name:        "request_count",
		Help:        "请求总次数",
		ConstLabels: constLabels, // 这样设置 lable 是静态的
	})

	// MetricRequestCountDynamicLabel
	// 可以调用 Metric_Request_Count_Dynamic_Label.With(prometheus.Labels{"path": "/v1/login"}).Inc()
	// 并且只能使用已经注册了的 label
	MetricRequestCountDynamicLabel = promauto.NewCounterVec(
		prometheus.CounterOpts{
			Name: "request_count_dynamic",
			Help: "请求总次数(动态 label 分接口)",
		},
		[]string{"path"},
	)

	MetricCurrentTime = promauto.NewCounterFunc(prometheus.CounterOpts{
		Name: "current_time",
		Help: "当前 unix 时间戳",
	}, func() float64 {
		return float64(time.Now().Unix())
	})

	MetricConfigTotal = promauto.NewGauge(prometheus.GaugeOpts{
		Name: "config_total",
		Help: "配置文件数量",
	})

	// MetricRequestDuration
	// 如果不指定则使用默认 DefBuckets = []float64{.005, .01, .025, .05, .1, .25, .5, 1, 2.5, 5, 10}
	// 也可以使用 Buckets: prometheus.LinearBuckets(0, 1, 10)
	// 手动指定也可以 Buckets: []float64{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
	// 正常都会使用手动指定，使得数据的分布比较均匀，这个跟分位数计算准确度有关，详情可以查看 /doc/prometheus histogram 分位数计算原理.png
	MetricRequestDuration = promauto.NewHistogram(prometheus.HistogramOpts{
		Name:    "request_duration",
		Help:    "请求时长",
		Buckets: []float64{10, 20, 30, 40, 50, 60, 70, 80, 90, 100},
	})

	MetricGaugeIngest = promauto.NewGaugeVec(
		prometheus.GaugeOpts{
			Name: "ingest_receive",
		},
		[]string{"event"},
	)

	MetricGaugeClickhouse = promauto.NewGaugeVec(
		prometheus.GaugeOpts{
			Name: "clickhouse_store",
		},
		[]string{"event"},
	)
)
