package main

import (
	"fmt"
	"log"
	"net/http"
)

func main() {
	// 如果路径不以 / 结尾，则表示固定的路径
	http.HandleFunc("/hello", func(writer http.ResponseWriter, request *http.Request) {
		fmt.Println("test")
	})

	// 如果路径以 / 结尾，则匹配子树，如 /test/1  /test/2/3  多层级都可以匹配
	http.HandleFunc("/test/", func(writer http.ResponseWriter, request *http.Request) {
		writer.Write([]byte("abc"))
	})

	// 如果路径有多个匹配，则采用匹配路径最长的那个进行处理
	http.HandleFunc("/test/a/b/c", func(writer http.ResponseWriter, request *http.Request) {
		writer.Write([]byte("1111"))
	})

	http.HandleFunc("/test/get", func(writer http.ResponseWriter, request *http.Request) {
		http.Redirect(writer, request, fmt.Sprintf("%s?%s", "/test/get/result", request.URL.RawQuery), http.StatusFound)
	})

	http.HandleFunc("/test/get/result", func(writer http.ResponseWriter, request *http.Request) {
		log.Println(request.URL.RawQuery)
		writer.Write([]byte("ok"))
	})

	// 如果没有找到则返回 404

	http.ListenAndServe(":8080", nil)
}
