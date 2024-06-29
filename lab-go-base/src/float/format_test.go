package float

import (
	"fmt"
	"strconv"
	"testing"
)

func Test_format(t *testing.T) {
	s := "5555.23456789012345678901234567890"

	t.Log(s)

	f, err := strconv.ParseFloat(s, 64)
	if err != nil {
		fmt.Printf("Error: %v\n", err)
		return
	}

	t.Log(f)

	parseResult := fmt.Sprintf("%.30f", f)

	t.Log(parseResult)

	if parseResult != s {
		fmt.Printf("Warning: %s has lost precision after conversion\n", s)
	}
}
