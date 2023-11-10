package main

import (
	"database/sql"
	"fmt"
	"github.com/ClickHouse/clickhouse-go/v2"
	"log"
	"strings"
	"time"
)

type Clickhouse struct {
	Host       string    // 服务端主机
	Port       int       // 端口
	DB         string    // 数据库
	User       string    // 用户名
	Password   string    // 密码
	Connection *sql.DB   // 建立连接后存放连接
	Rows       *sql.Rows // 运行sql后的结果存放
}

func (c *Clickhouse) Conn() {
	c.Connection = clickhouse.OpenDB(&clickhouse.Options{
		Addr: []string{fmt.Sprintf("%s:%d", c.Host, c.Port)},
		Auth: clickhouse.Auth{
			Database: c.DB,
			Username: c.User,
			Password: c.Password,
		},
		Settings: clickhouse.Settings{
			"max_execution_time": 60,
		},
		DialTimeout: 5 * time.Second,
		Compression: &clickhouse.Compression{
			Method: clickhouse.CompressionBrotli,
			Level:  5,
		},
		Protocol: clickhouse.HTTP,
	})

}

func (c *Clickhouse) ConnDsn() {
	conn, err := sql.Open("clickhouse", fmt.Sprintf("http://%s:%d/%s?username=%s&password=%s", c.Host, c.Port, c.DB, c.User, c.Password))
	if err != nil {
		log.Printf("Connect to the server failed, %s.\n", err.Error())
		return
	}
	c.Connection = conn
}

func (c *Clickhouse) Select(query string) {
	rows, err := c.Connection.Query(query)
	if err != nil {
		log.Printf("Query select failed, %s.\n", err.Error())
		return
	}
	c.Rows = rows
}

func (c *Clickhouse) Show() {
	cols, err := c.Rows.Columns()
	if err != nil {
		log.Printf("Failed to get table columns, %s.\n", err.Error())
		return
	}
	// 一行数据，使用any是为了避开数据类型的问题
	var rows = make([]any, len(cols))
	// 存实际的值，是byte数组，长度以列的数量为准
	var values = make([][]byte, len(cols))
	for i := 0; i < len(cols); i++ {
		rows[i] = &values[i]
	}
	// 打印表头
	fmt.Println(strings.Join(cols, ","))
	for c.Rows.Next() {
		if err = c.Rows.Scan(rows...); err != nil {
			fmt.Println(err)
			return
		}
		var vString []string
		for _, v := range values {
			vString = append(vString, string(v))
		}
		// 逐行打印出来
		fmt.Println(strings.Join(vString, ","))
	}
}

func (c *Clickhouse) Close() {
	c.Connection.Close()
	c.Rows.Close()
}
