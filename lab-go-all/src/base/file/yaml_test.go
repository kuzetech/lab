package file

import (
	"github.com/stretchr/testify/require"
	"gopkg.in/yaml.v3"
	"io/ioutil"
	"testing"
)

type Content struct {
	A string `json:"a" yaml:"a"`
	B string `json:"b" yaml:"b"`
	C string `json:"c" yaml:"c"`
}

func Test_yaml_tool(t *testing.T) {
	assertions := require.New(t)
	yamlFile, err := ioutil.ReadFile("test.yaml")
	assertions.Nil(err)

	var c Content
	err = yaml.Unmarshal(yamlFile, &c)
	assertions.Nil(err)

	t.Log(c.A)
	t.Log(c.B)
	t.Log(c.C)

}
