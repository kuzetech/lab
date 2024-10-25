package _error

import (
	"errors"
	"testing"
)

var customerError = errors.New("test")

func returnError() error {
	return customerError
}

func Test_customer(t *testing.T) {
	err := returnError()
	if errors.Is(err, customerError) {
		t.Log("就是这个错误")
	}
}
