## 相关说明

go 可以通过使用 os.Args 获取传入命令行参数，但是很难用
   也可以使用 flag 包做一些简单处理，相对于 os 更便捷

pflag 包与 flag 包的工作原理甚至是代码实现都是类似的，下面是 pflag 相对 flag 的一些优势：
    1. 支持更加精细的参数类型：例如，flag 只支持 uint 和 uint64，而 pflag 额外支持 uint8、uint16、int32 等类型。
    2.支持更多参数类型：ip、ip mask、ip net、count、以及所有类型的 slice 类型。
    3.兼容标准 flag 库的 Flag 和 FlagSet：pflag 更像是对 flag 的扩展。
    4.原生支持更丰富的功能：支持 shorthand、deprecated、hidden 等高级功能。

构建现代应用程序时，我们更希望直接传入配置文件的路径，然后程序可以读取配置文件并实现变更监听机制，viper 就有如下功能：
1. 设置默认值 
2. 从JSON，TOML，YAML，HCL和Java属性配置文件中读取 
3. 实时监控和重新读取配置文件（可选） 
4. 从环境变量中读取 
5. 从远程配置系统（etcd或Consul）读取，并观察变化 
6. 从命令行标志读取 
7. 从缓冲区读取 
8. 设置显式值