package _sort

import (
	"fmt"
	"sort"
	"testing"
)

func Test_string_slice(t *testing.T) {
	strSlice2 := make([]string, 8)
	strSlice2[0] = "SuccessWal-99999"
	strSlice2[1] = "SuccessWal-99996"
	strSlice2[2] = "SuccessWal-99993"
	strSlice2[3] = "SuccessWal-99997"

	strSlice2[4] = "SuccessWal-00005"
	strSlice2[5] = "SuccessWal-00003"
	strSlice2[6] = "SuccessWal-00004"
	strSlice2[7] = "SuccessWal-00016"

	fmt.Printf("strSlice: %v\n", strSlice2)
	sort.Strings(strSlice2)
	fmt.Printf("strSlice: %v\n", strSlice2)
}
