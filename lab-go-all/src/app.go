package main

import (
	"errors"
	"fmt"
	"io"
	"net/http"
)

func main() {
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
