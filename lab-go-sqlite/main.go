package main

import (
	"database/sql"
	"fmt"
	_ "github.com/mattn/go-sqlite3"
	"log"
	"os"
)

var (
	createWalTableSql = `
	  create table if not exists wal (
	      id integer not null primary key, 
	      content blob
	  );
	`

	deleteWalTableItemSql = "delete from wal where id = ?;"

	insertWalTableSql = "insert into wal(content) values(?)"

	selectWalTableAllSql = "select id, content from wal"
)

func main() {

	var path = "./db/wal.db"
	os.Remove(path)

	db, err := sql.Open("sqlite3", path)
	if err != nil {
		log.Fatal(err)
	}

	_, err = db.Exec(createWalTableSql)
	if err != nil {
		log.Fatal(err)
	}

	result, err := db.Exec(insertWalTableSql, []byte("test1"))
	if err != nil {
		log.Fatal(err)
	}

	insertId, err := result.LastInsertId()
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(insertId)

	prepare, err := db.Prepare(insertWalTableSql)
	if err != nil {
		log.Fatal(err)
	}

	result2, err := prepare.Exec([]byte("test2"))
	if err != nil {
		log.Fatal(err)
	}

	insertId2, err := result2.LastInsertId()
	if err != nil {
		log.Fatal(err)
	}

	fmt.Println(insertId2)

	_, err = db.Exec(deleteWalTableItemSql, 2)
	if err != nil {
		log.Fatal(err)
	}

	rows, err := db.Query(selectWalTableAllSql)
	if err != nil {
		log.Fatal(err)
	}
	defer rows.Close()
	for rows.Next() {
		var id int64
		var content string
		err = rows.Scan(&id, &content)
		if err != nil {
			log.Fatal(err)
		}
		fmt.Println(id, content)
	}
	err = rows.Err()
	if err != nil {
		log.Fatal(err)
	}

}
