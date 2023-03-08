package normal

import (
	"context"
	"testing"
	"time"
)

var SubChannel = "chan"

func Test_pub_sub(t *testing.T) {
	client := NewDefaultRedisClient()

	subscribe := client.c.Subscribe(context.Background(), SubChannel)
	defer subscribe.Close()

	ch := subscribe.Channel()

	go func() {
		for {
			select {
			case n := <-time.After(time.Second):
				cmd := client.c.Publish(context.Background(), SubChannel, n.Format(time.RFC3339))
				if cmd.Err() != nil {
					t.Logf("pub error: %v", cmd.Err())
				}
			}
		}
	}()

	for {
		select {
		case msg := <-ch:
			t.Logf("rev msg: %s", msg)
		}
	}
}
