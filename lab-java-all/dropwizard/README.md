# study-dropwizard

## 官方相关页面
1. [官网地址](https://www.dropwizard.io/en/latest/manual/core.html)
2. [官网可配置项](https://www.dropwizard.io/en/latest/manual/configuration.html#man-configuration)


## 程序启动注意
1. 在 IDEA 启动需要添加 program arguments(`server /Users/huangsw/code/study/study-api-frame/config.yml`)
2. jar 包启动时可参考命令 `java -Ddw.http.port=9090 -Ddw.http.adminPort=9091 -jar yourapp.jar server yourconfig.yml`


## 程序启动访问地址
1. [健康检查接口](http://localhost:7001/healthcheck)
2. [管理页面](http://localhost:7001)
3. [接口访问地址](http://localhost:7000/employees/2)


