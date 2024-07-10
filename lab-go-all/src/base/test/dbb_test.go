package _test

import (
	. "github.com/smartystreets/goconvey/convey"
	"testing"
)

func Test_bdd(t *testing.T) {
	Convey("Given 2 even numbers", t, func() {
		a := 2
		b := 4

		Convey("When add the two numbers", func() {
			c := a + b

			Convey("Then the result is still even", func() {
				So(c%2, ShouldEqual, 0)
			})
		})
	})

	// 当你想在 webUI 上查看执行结果
	// 可以执行 $ go install github.com/smartystreets/goconvey
	// 然后 test 文件目录下启动 GoConvey web server 执行命令 $ $GOPATH/bin/goconvey
	// 启动后会自动遍历目录执行所有 test 方法，然后将结果展示在 web 中
	// 打开 http://localhost:8080 查看结果
	// 修改测试用例后保存文件将自动重新加载
}
