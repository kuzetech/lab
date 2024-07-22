package _concurrent

import "testing"

/*
	gollback 也是用来处理一组子任务的执行的，不过它解决了 ErrGroup 收集子任务返回结果的痛点。
	使用 ErrGroup 时，如果你要收集所有子任务的结果和错误，你需要定义额外的变量收集执行结果和错误，但是这个库可以提供更便利的方式。
*/

func Test_gollback(t *testing.T) {

}
