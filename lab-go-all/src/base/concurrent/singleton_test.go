package _concurrent

import (
	"fmt"
	"sync"
	"testing"
	"unsafe"
)

type singleton struct {
}

var once sync.Once
var instance *singleton

func GetSingletonInstance() *singleton {
	once.Do(func() {
		fmt.Println("create object")
		instance = new(singleton)
	})
	return instance
}

func Test_singleton(t *testing.T) {
	var wg sync.WaitGroup
	wg.Add(5)

	for i := 0; i < 5; i++ {
		go func() {
			instance := GetSingletonInstance()
			// 得到的对象的地址都是一致的
			fmt.Printf("%x \n", unsafe.Pointer(instance))
			wg.Done()
		}()
	}

	wg.Wait()
}
