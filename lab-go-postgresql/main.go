package main

import (
	"database/sql"
	"fmt"
	"github.com/lib/pq"
	_ "github.com/lib/pq"
	"log"
	"time"
)

var (
	createDBConnStr    = "postgres://postgres:123456@localhost:5432/postgres?sslmode=disable"
	createTableConnStr = "postgres://postgres:123456@localhost:5432/test5?sslmode=disable"
	destDB             = "test5"
	destTable          = "record"
)

func createDBIfNotExist() error {
	db, err := sql.Open("postgres", createDBConnStr)
	if err != nil {
		return fmt.Errorf("open postgres error : %s", err)
	}

	defer db.Close()
	var createTmp = "create database %s"

	_, err = db.Exec(fmt.Sprintf(createTmp, destDB))
	if err != nil {
		pError := err.(*pq.Error)
		if pError.Message == fmt.Sprintf("database \"%s\" already exists", destDB) {
			return nil
		}
		return fmt.Errorf("create database error : %s", err)
	}

	return nil
}

func createTableIfNotExist() error {
	db, err := sql.Open("postgres", createTableConnStr)
	if err != nil {
		return fmt.Errorf("open postgres error : %s", err)
	}

	defer db.Close()
	var createTmp = `
		CREATE TABLE %s (
			"id" serial NOT NULL,
			PRIMARY KEY ("id"),
			"time" integer NOT NULL,
			"host" text NOT NULL,
			"client" text NOT NULL,
			"category" integer NOT NULL,
			"count" integer NOT NULL
	 	)
	`

	_, err = db.Exec(fmt.Sprintf(createTmp, destTable))
	if err != nil {
		pError := err.(*pq.Error)
		if pError.Message == fmt.Sprintf("relation \"%s\" already exists", destTable) {
			return nil
		}
		return fmt.Errorf("create table error : %s", err)
	}

	return nil
}

func main() {

	err := createDBIfNotExist()
	if err != nil {
		log.Fatalf("createDBIfNotExist error : %s", err)
	}

	err = createTableIfNotExist()
	if err != nil {
		log.Fatalf("createTableIfNotExist error : %s", err)
	}

	db, err := sql.Open("postgres", createTableConnStr)
	if err != nil {
		log.Fatalf("open postgres error : %s", err)
	}
	defer db.Close()

	for {
		_, err = db.Exec("INSERT INTO record (time, host, client,category, count) VALUES (0, '1', '2', 3, 4)")
		if err != nil {
			log.Fatal(err)
		}
		time.Sleep(time.Second * 2)
	}

}
