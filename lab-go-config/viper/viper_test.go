package main

import (
	"fmt"
	"github.com/fsnotify/fsnotify"
	"github.com/spf13/viper"
	"testing"
)

func Test_viper(t *testing.T) {
	v := viper.New()

	v.SetConfigName("test") // 查找名为 test 的配置文件，不包含扩展名
	v.SetConfigType("yaml") // 如果文件没有扩展名，就以 yaml 的形式读取

	v.SetConfigFile("/a/b/test.yaml") // 直接制定配置文件路径

	// 在多个目录下查找配置文件，默认使用第一个找到的文件
	v.AddConfigPath("./a")
	v.AddConfigPath("./b")

	// Find and read the config file
	err := v.ReadInConfig()
	if err != nil {
		panic(err)
	}

	// 获取配置文件中的值
	filed := v.GetString("test")
	fmt.Println(filed)

	// 注册配置文件变更监听器
	v.OnConfigChange(func(e fsnotify.Event) {
		fmt.Println("abs path Config file changed:", e.Name)
		// 获取到的时候更新后的值
		fmt.Println(v.GetString("test"))
	})

	// 开启配置文件变更监控
	v.WatchConfig()

	// 主线程永久挂机
	select {}

}
