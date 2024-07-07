package _interface

type Writer interface {
}

type Reader interface {
}

type WriterReader interface {
	Reader
	Writer
}

/**
在 go 语言中倾向使用小的接口，很多接口只包含一个接口，这样实现压力小
较大的接口往往由多个小接口组合
并且只实现必要功能接口
*/
