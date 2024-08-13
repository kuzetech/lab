package filewatch

import (
	"fmt"
	"github.com/hpcloud/tail"
	"github.com/stretchr/testify/assert"
	"testing"
)

func Test_base(t *testing.T) {
	tf, err := tail.TailFile("./temp/test.log", tail.Config{Follow: true})
	assert.Nil(t, err)
	for line := range tf.Lines {
		fmt.Println(line.Text)
	}
}
