package main

func main() {

	var (
		host  = "localhost"
		port  = 8123
		user  = "default"
		pass  = ""
		db    = "default"
		query = "show databases"
	)

	c := Clickhouse{
		Host:     host,
		Port:     port,
		User:     user,
		Password: pass,
		DB:       db,
	}

	c.ConnDsn()
	c.Select(query)
	c.Show()
}
