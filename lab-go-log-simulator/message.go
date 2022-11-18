package main

type Message struct {
	Type string      `name:"type" json:"type"`
	Data interface{} `name:"data" json:"data"`
}

type Content struct {
	A string
	B string
	C string
	D string
	E string
	F string
}

var content = Content{
	A: "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
	B: "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
	C: "cccccccccccccccccccccccccccccc",
	D: "dddddddddddddddddddddddddddddd",
	E: "eeeeeeeeeeeeeeeeeeeeeeeeeeeeeee",
	F: "fffffffffffffffffffffffffffffff",
}

var message = Message{
	Type: "Event",
	Data: content,
}
