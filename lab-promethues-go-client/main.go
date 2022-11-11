package main

import (
	"github.com/prometheus/client_golang/prometheus"
	"github.com/prometheus/client_golang/prometheus/promauto"
	"github.com/prometheus/client_golang/prometheus/promhttp"
	"log"
	"math/rand"
	"net/http"
	"os"
	"strconv"
	"time"
)

var (
	hostname, _ = os.Hostname()

	constLabels = map[string]string{
		"hostname": hostname,
	}

	// 每种监控指标类型基本都有三个方法
	// NewCounter		基本方法
	// NewCounterVec	指定 lable
	// NewCounterFunc	上报的值为 func 返回值，如果是 counter 类型返回值必须满足一直递增，另外还需要保证方法是线程安全，可以利用该方法即使程序停止了也能继续上传

	// 实现静态 lable
	MyCounter = promauto.NewCounter(prometheus.CounterOpts{
		Namespace:   "my", // 在指标名前面统一加入前缀，最终指标名为 my_counter_total
		Name:        "counter_total",
		Help:        "自定义counter",
		ConstLabels: constLabels,
	})

	MyCounter2 = promauto.NewCounterFunc(prometheus.CounterOpts{
		Namespace:   "my", // 在指标名前面统一加入前缀，最终指标名为 my_counter_total
		Name:        "counter_total2",
		Help:        "自定义counter2",
		ConstLabels: constLabels,
	}, func() float64 {
		return float64(time.Now().Unix())
	})

	// 可以实现动态 lable
	MyGauge = promauto.NewGaugeVec(
		prometheus.GaugeOpts{
			Namespace: "my", // 在指标名前面统一加入前缀
			Name:      "gauge_num",
			Help:      "自定义gauge",
		},
		[]string{
			"hostname",
			"path",
		},
	)

	// 定义histogram
	// 在 prometheus 服务端计算分位数
	MyHistogram = prometheus.NewHistogram(prometheus.HistogramOpts{
		Namespace: "my", // 在指标名前面统一加入前缀
		Name:      "histogram_bucket",
		Help:      "自定义histogram",
		// 如果不指定则使用默认 DefBuckets = []float64{.005, .01, .025, .05, .1, .25, .5, 1, 2.5, 5, 10}
		// Buckets: []float64{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
		// Buckets: prometheus.LinearBuckets(0, 1, 10),
		// 需要注意分位数算法并不会真的全部数据排序后再取
		// 而是看数据分布在哪些桶，通过桶的上下限直接估算
		// 如果数据分布不均匀或者桶分配不好，可能会导致误差很大
	})

	// 定义 Summary
	// 在 client 端计算分位数
	MySummary = prometheus.NewSummary(prometheus.SummaryOpts{
		Namespace: "my", // 在指标名前面统一加入前缀
		Name:      "summary_bucket",
		Help:      "自定义summary",
		// 这部分可以算好后在set
		// 如果没有指定桶，指标就只有 sum 和 count
		Objectives: map[float64]float64{
			0.5:  1, // 前面的 0.5 0.9 0.99 表示分位数
			0.9:  5, // 后面的1 5 9 设置都是无效的，只有等数据进来后才会重新统计
			0.99: 9,
		},
	})
)

func main() {
	// 如果是 Histogram 或者 Summary 类型必须注册
	prometheus.MustRegister(MyHistogram)
	prometheus.MustRegister(MySummary)

	// 如果直接使用 http.ListenAndServe(":2112", nil)
	// handler 为 nil 的情况下会默认使用 DefaultServeMux，为一个全局变量
	// 任何程序在任何地方都可以修改 DefaultServeMux ，导致不安全
	// 因此我们使用 http.NewServeMux() 创建一个自定义的 ServeMux

	// 定义每一个 uri 的处理器
	mux := http.NewServeMux()
	mux.Handle("/", promhttp.Handler())
	mux.Handle("/metrics", promhttp.Handler())

	mux.HandleFunc("/login", func(writer http.ResponseWriter, request *http.Request) {
		MyCounter.Inc()
		writer.Write([]byte("login success"))
	})

	mux.HandleFunc("/addOrder", func(writer http.ResponseWriter, request *http.Request) {
		// 只能使用上面已经注册了的 lable
		// 可以使用这个方式实现动态 lable, 如果是静态 lable 这个方式就很冗余了
		MyGauge.With(prometheus.Labels{"hostname": hostname}).Inc()
		writer.Write([]byte("add order success"))
	})

	mux.HandleFunc("/cancelOrder", func(writer http.ResponseWriter, request *http.Request) {
		MyGauge.With(prometheus.Labels{"hostname": hostname}).Dec()
		writer.Write([]byte("cancel order success"))
	})

	mux.HandleFunc("/testHis", func(writer http.ResponseWriter, request *http.Request) {
		num := rand.Float64()
		MyHistogram.Observe(num)
		numStr := strconv.FormatFloat(num, 'f', -1, 64)
		writer.Write([]byte("random value is " + numStr))
	})

	// 配置 http server 的各方面
	s := &http.Server{
		Addr:         ":2112", // 前面部分省略默认使用 http://127.0.0.1
		Handler:      mux,
		ReadTimeout:  30 * time.Second,
		WriteTimeout: 30 * time.Second,
	}

	go func() {
		for {
			time.Sleep(time.Second * 1)

			MyCounter.Inc()

			MyGauge.With(prometheus.Labels{"hostname": hostname, "path": "/test1"}).Inc()
			MyGauge.With(prometheus.Labels{"hostname": hostname, "path": "/test2"}).Add(2)
			MyGauge.With(prometheus.Labels{"hostname": hostname, "path": "/test3"}).Add(3)

			num := rand.Float64() * 10
			numStr := strconv.FormatFloat(num, 'f', -1, 64)
			MyHistogram.Observe(num)

			MySummary.Observe(num)

			log.Println(numStr)

		}
	}()

	s.ListenAndServe()
}
