# JSON 相关测试

## 关于 json 工具的选择

Jsoniter 的 Golang 版本可以比标准库 encoding/json 快 6 倍之多。  
Jsoniter 百分百兼容 encoding/json 并且很多使用方法更加便捷。

easyjson 使用代码生成的方式替换了原生的反射方式优化性能，但是使用略显麻烦