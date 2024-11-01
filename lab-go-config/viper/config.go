package main

import (
	"errors"
	"github.com/spf13/viper"
)

type Config struct {
	Endpoints                string `mapstructure:"ck_endpoints" yaml:"ck_endpoints"`
	Username                 string `mapstructure:"ck_username" yaml:"ck_username"`
	Password                 string `mapstructure:"ck_password" yaml:"ck_password"`
	ConcurrentQueryNum       int    `mapstructure:"concurrent_query_num" yaml:"concurrent_query_num"`
	StatisticianDatabase     string `mapstructure:"statistician_database" yaml:"statistician_database"`
	SchedulingMinuteInterval int    `mapstructure:"scheduling_minute_interval" yaml:"scheduling_minute_interval"`
	QueryDelayMinutes        int    `mapstructure:"query_delay_minutes" yaml:"query_delay_minutes"`
	QueryStatisticalMinutes  int    `mapstructure:"query_statistical_minutes" yaml:"query_statistical_minutes"`
}

func ReadConfig(path string) (*Config, error) {
	v := viper.New()

	/*
		优先级如下 ：
			默认配置
			从配置文件读取数据
			实时查看和重新读取配置文件（可选）
			从环境变量中读取
			从远程配置系统(etcd 或 Consul)读取数据并监听变化
			从命令行参数读取
			从 buffer 中读取
			设置显式值
	*/

	// 设置默认值，优先级最低
	v.SetDefault("ck_endpoints", "localhost:9000")
	v.SetDefault("ck_username", "demo")
	v.SetDefault("ck_password", "demo")
	v.SetDefault("concurrent_query_num", 1)
	v.SetDefault("statistician_database", "demo")
	v.SetDefault("scheduling_minute_interval", 5)
	v.SetDefault("query_delay_minutes", 5)
	v.SetDefault("query_statistical_minutes", 5)

	// 设置环境变量前缀，默认会变成 STATISTICIAN_
	v.SetEnvPrefix("statistician")
	// 允许从环境变量中读取，例如 STATISTICIAN_CK_ENDPOINTS
	v.AutomaticEnv()

	if path != "" {
		v.SetConfigType("yaml")
		v.SetConfigFile("/a/b/test.yaml")
		err := v.ReadInConfig()
		if err != nil {
			return nil, err
		}
	}

	// 通过上述代码，这个时候配置包含了 默认值、配置文件、环境变量
	// viper 中会同时保存这三份数据
	// 当使用这些 key 的时候，根据优先级依次寻找

	var c Config
	err := v.Unmarshal(&c)
	if err != nil {
		return nil, err
	}

	err = c.checkConfig()
	if err != nil {
		return nil, err
	}

	return &c, nil
}

func (c *Config) checkConfig() error {
	if c.Endpoints == "" {
		return errors.New("clickhouse endpoints can not be empty")
	}
	if c.Password == "" {
		return errors.New("clickhouse password can not be empty")
	}
	if c.Username == "" {
		return errors.New("clickhouse username can not be empty")
	}
	if c.StatisticianDatabase == "" {
		return errors.New("statistician database can not be empty")
	}
	if c.ConcurrentQueryNum <= 0 {
		return errors.New("concurrent query num illegal")
	}
	if c.SchedulingMinuteInterval <= 0 {
		return errors.New("scheduling minute interval illegal")
	}
	if c.QueryDelayMinutes <= 0 {
		return errors.New("query delay minutes illegal")
	}
	if c.QueryStatisticalMinutes <= 0 {
		return errors.New("query statistical minutes illegal")
	}
	return nil
}
