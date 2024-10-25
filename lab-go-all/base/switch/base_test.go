package _switch

import (
	"runtime"
	"testing"
)

func Test_base(t *testing.T) {
	switch os := runtime.GOOS; os {
	case "darwin":
		t.Log("X")
	case "linux":
		t.Log("linux")
	default:
		t.Log(os)
	}
}

func Test_multiCase(t *testing.T) {
	var i int = 1
	switch i {
	case 1, 2, 3:
		t.Log("123")
	case 4, 5, 6:
		t.Log("456")
	default:
		t.Log("unknown")
	}
}

func Test_if(t *testing.T) {
	var num int = 1
	switch {
	case 0 <= num && num < 5:
		t.Log("5")
	case 5 <= num && num < 10:
		t.Log("10")
	default:
		t.Log("unknown")
	}

}
