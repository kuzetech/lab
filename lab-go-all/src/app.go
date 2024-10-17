package main

import (
	"bytes"
	"errors"
	"fmt"
	"io"
	"net/http"
	"time"
)

func test() {
	url := "http://localhost:8080/v1/collect"

	req, err := http.NewRequest("POST", url, nil)

	client := &http.Client{}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("请求失败:", err)
		return
	}
	for {
		b := make([]byte, 10)
		n, err := resp.Body.Read(b)
		if n > 0 {
			println(string(b[:n]))
			panic("panic halfway")
		}
		if errors.Is(err, io.EOF) {
			break
		}
		if err != nil {
			panic(err)
		}

	}
}

func main() {
	// 创建一个大数据体，例如 100 MB
	dataSize := 100 * 1024 * 1024                    // 100 MB
	largeData := bytes.Repeat([]byte("a"), dataSize) // 用 'a' 填充

	// 创建一个 HTTP 请求
	req, err := http.NewRequest("POST", "http://localhost:8080", io.NopCloser(bytes.NewReader(largeData)))
	if err != nil {
		fmt.Println("Error creating request:", err)
		return
	}

	// 设置请求头
	req.ContentLength = int64(len(largeData))

	// 创建 HTTP 客户端并设置自定义传输
	client := &http.Client{
		Transport: &http.Transport{
			DisableKeepAlives: true, // 关闭连接保持
		},
	}

	// 使用 goroutine 发送请求，并在中间关闭连接
	go func() {
		resp, err := client.Do(req)
		if err != nil {
			fmt.Println("Error sending request:", err)
			return
		}
		defer resp.Body.Close()
		fmt.Println("Response status:", resp.Status)
	}()

	// 模拟部分数据发送后关闭连接
	time.Sleep(100 * time.Millisecond) // 等待 2 秒，模拟部分数据已发送

	panic("1111")
}
