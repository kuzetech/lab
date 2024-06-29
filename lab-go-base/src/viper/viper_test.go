package viper

import (
	"fmt"
	"github.com/fsnotify/fsnotify"
	"testing"
)
import "github.com/spf13/viper"

func Test_viper(t *testing.T) {
	v := viper.New()

	// 查找名为 test 的配置文件，不包含扩展名
	v.SetConfigName("test")

	// 如果文件没有扩展名，就以 yaml 的形式读取
	v.SetConfigType("yaml")

	// 在多个目录下查找 test.yaml 的配置文件
	// 多个路径的情况下，读取找到的第一个文件
	v.AddConfigPath("./a")
	v.AddConfigPath("./b")

	// 直接制定配置文件路径
	// v.SetConfigFile("/a/b/test.yaml")

	// Find and read the config file
	err := v.ReadInConfig()

	// Handle errors reading the config file
	if err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); ok {
			fmt.Println("找不到配置文件")
		} else {
			// Config file was found but another error was produced
			panic(fmt.Errorf("fatal error config file: %w", err))
		}
	}

	// 获取配置文件中的值
	filed := v.GetString("test")
	fmt.Println(filed)

	// 监听配置文件的变更
	v.OnConfigChange(func(e fsnotify.Event) {
		fmt.Println("abs path Config file changed:", e.Name)
		// 获取到的时候更新后的值
		fmt.Println(v.GetString("test"))
	})
	v.WatchConfig()

	// 主线程永久挂机
	select {}

}
