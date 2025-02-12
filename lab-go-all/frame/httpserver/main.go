package main

import (
	"fmt"
	"net/http"
)

func handler(w http.ResponseWriter, r *http.Request) {
	// 设置状态码
	w.WriteHeader(http.StatusOK) // 200 状态码
	// 设置响应头（可选）
	w.Header().Set("Content-Type", "application/json")
	// 响应内容
	response := `{"message": "Hello, World!"}`
	w.Write([]byte(response))
}

func main() {
	// 绑定路由
	http.HandleFunc("/", handler)

	// 启动 HTTP 服务器
	err := http.ListenAndServe(":8080", nil)
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
}
