package csv

import (
	"bufio"
	"encoding/csv"
	"fmt"
	"github.com/stretchr/testify/require"
	"io"
	"os"
	"testing"
)

type Content struct {
	deviceId string `json:"deviceId"`
	time     string `json:"time"`
	event    string `json:"event"`
	step     string `json:"step"`
}

func Test_csv(t *testing.T) {
	assertions := require.New(t)

	csvFile, err := os.Open("yc_4y13.csv")
	assertions.Nil(err)
	defer csvFile.Close()

	reader := csv.NewReader(bufio.NewReader(csvFile))
	var contents = make([]*Content, 0, 3)
	var count = 0
	for {

		if count == 3 {
			break
		}

		line, err := reader.Read()

		if err == io.EOF {
			break
		} else if err != nil {
			t.Fatal(err)
		}

		contents = append(contents, &Content{
			deviceId: line[0],
			time:     line[1],
			event:    line[2],
			step:     line[3],
		})

		count = count + 1
	}

	for _, c := range contents {
		fmt.Println(c)
	}
}
