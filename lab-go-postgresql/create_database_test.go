package main

import (
	"database/sql"
	_ "github.com/lib/pq"
	"github.com/stretchr/testify/require"
	"testing"
)

func Test_create_database(t *testing.T) {
	assertions := require.New(t)

	var connStr = "postgres://postgres:123456@localhost:5432/test4?sslmode=disable"

	db, err := sql.Open("postgres", connStr)
	assertions.Nil(err)

	_, err = db.Exec("create database test4")
	assertions.Nil(err)
}
