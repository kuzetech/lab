package main

import (
	"fmt"
	"github.com/gorilla/mux"
	"net/http"
)

func main() {
	r := mux.NewRouter()

	r.HandleFunc("/v1/md/{app}", func(w http.ResponseWriter, r *http.Request) {
		vars := mux.Vars(r)
		linkName := vars["app"]
		w.Write([]byte(linkName))
	}).Methods(http.MethodGet)

	err := http.ListenAndServe(":8081", r)
	if err != nil {
		fmt.Println("Error starting server:", err)
	}
}
