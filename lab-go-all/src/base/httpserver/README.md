# 相关说明

go 提供的 http server 本身性能很好，但是在可用性上有些欠缺

比如解析各种参数，实现 restful 接口等等

所以通常会搭配其他的更好用的 router 例如 httprouter

但是我们一般更加倾向于直接使用框架 gin