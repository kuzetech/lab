package file

import (
	"github.com/stretchr/testify/require"
	"io/ioutil"
	"os"
	"testing"
)

func Test_open_file(t *testing.T) {
	assertions := require.New(t)

	file, err := os.Open("./files/createTime.txt")

	if os.IsNotExist(err) {
		t.Log("not")
	} else {
		t.Log("yes")
		d, err := ioutil.ReadAll(file)
		assertions.Nil(err)
		t.Log(string(d))
	}

}
