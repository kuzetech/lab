package file

import (
	"bufio"
	"io"
	"os"
	"testing"
)

/*
	BenchmarkWriteFile-4           10  127,205,360 ns/op        118 B/op        4 allocs/op
	BenchmarkWriteFileBuffered-4  300    5,978,219 ns/op      4,208 B/op        5 allocs/op
	BenchmarkReadFile-4            20   53,226,890 ns/op        115 B/op        4 allocs/op
	BenchmarkReadFileBuffered-4   200    7,518,203 ns/op  3,204,217 B/op  200,005 allocs/op
*/

func BenchmarkWriteFile(b *testing.B) {
	for n := 0; n < b.N; n++ {
		f, err := os.Create("/tmp/test.txt")
		if err != nil {
			panic(err)
		}

		for i := 0; i < 100000; i++ {
			f.WriteString("some text!\n")
		}

		f.Close()
	}
}

func BenchmarkWriteFileBuffered(b *testing.B) {
	for n := 0; n < b.N; n++ {
		f, err := os.Create("/tmp/test.txt")
		if err != nil {
			panic(err)
		}

		w := bufio.NewWriter(f)

		for i := 0; i < 100000; i++ {
			w.WriteString("some text!\n")
		}

		w.Flush()
		f.Close()
	}
}

func BenchmarkReadFile(b *testing.B) {
	for n := 0; n < b.N; n++ {
		f, err := os.Open("/tmp/test.txt")
		if err != nil {
			panic(err)
		}

		b := make([]byte, 10)

		_, err = f.Read(b)
		for err == nil {
			_, err = f.Read(b)
		}
		if err != io.EOF {
			panic(err)
		}

		f.Close()
	}
}

func BenchmarkReadFileBuffered(b *testing.B) {
	for n := 0; n < b.N; n++ {
		f, err := os.Open("/tmp/test.txt")
		if err != nil {
			panic(err)
		}

		r := bufio.NewReader(f)

		_, err = r.ReadString('\n')
		for err == nil {
			_, err = r.ReadString('\n')
		}
		if err != io.EOF {
			panic(err)
		}

		f.Close()
	}
}
