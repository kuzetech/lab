package file

import "strconv"

type Message struct {
	Type string      `name:"type" json:"type"`
	Data interface{} `name:"data" json:"data"`
}

type Content struct {
	A     string
	B     string
	C     string
	D     string
	E     string
	F     string
	Event string `name:"event" json:"event"`
}

var content = Content{
	A: "aaa",
	B: "bbb",
	C: "ccc",
	D: "ddd",
	E: "eee",
	F: "fff",
}

func GenerateMessage(count int) *Message {
	content.Event = "event" + strconv.Itoa(count)
	return &Message{
		Type: "Event",
		Data: content,
	}
}
