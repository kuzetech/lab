package _test

import "testing"

func Test_fail_error(t *testing.T) {
	t.Log("测试 fail 和 error")
	t.Fail()
	t.Error()
	t.Log("后续代码继续执行")
}

func Test_failnow(t *testing.T) {
	t.Log("测试 FailNow")
	t.FailNow()
	t.Log("后续代码继续执行")
}

func Test_fatal(t *testing.T) {
	t.Fatal("测试 fatal")
	t.Log("后续代码继续执行")
}

func Test_suceess(t *testing.T) {
	t.Log("其他测试用例还是都会执行")
}
