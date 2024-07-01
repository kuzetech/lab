## 官网地址

https://dolphinscheduler.apache.org/

## 执行步骤

1. 执行 `docker-compose --profile schema up -d` 初始化 postgresql 中的表结构
2. 执行 `docker-compose --profile all up -d` 启动所有服务, 如果 master 节点起不来就多执行几次
3. 可以通过 `http://localhost:12345/dolphinscheduler/ui` 访问操作界面，默认的用户和密码分别为 admin 和 dolphinscheduler123
4. 可以通过 `http://localhost:12345/dolphinscheduler/doc.html?language=zh_CN&lang=cn` 访问 api 说明文档
5. 原生的 rest api 创建 dag 的接口参数十分复杂，导致创建流程十分痛苦，推荐使用 `https://dolphinscheduler.apache.org/python/3.0.0/index.html` PyDolphinScheduler 工具生成工作流程

## 关于任务之间的参数传递
1. 可以参考[官网指导文档](https://dolphinscheduler.apache.org/zh-cn/docs/latest/user_doc/guide/parameter/context.html)
2. 官网的例子过于简单，都是静态变量，我需要的是传入一个变量，经过计算得出另一个变量，例如传入静态变量 input = "666", 传出动态变量 output = "my" + $input
3. 实现上述例子的 shell 脚本如下：
    ```shell
    echo "$input"
    value="my${input}"
    echo '${setValue(output='$value')}'  
    # echo '${setValue(mytable='${value}')}'    # 或者写成这样也行，但是里面的单引号是必须的  
    ```
4. 原理如下
   - 首先，dolphin 会扫描所有带 ${} 的变量值，仅将第一层级替换成系统参数的值。只带 $ 符号的不会，生成如下的 shell 脚本如下
     ```shell
        echo "$input"
        value="my666"
        echo '${setValue(output='$value')}'  
    ```
   - 然后，将生成的 shell 脚本传到 worker 上执行
   - 因此，第一句话的输出为空，因为在linux shell 执行环境中未定义
   - 因此，第二句话 ${table} 先被替换成 666 的值，整个语句就变成 `value="my6666"`
   - 因此，第三句话中 value 如果是个变量必须加上单引号，不然会被当成常量。如果加上单引号后的变量不存在，会直接报错

## 关于如何使用 PyDolphinScheduler
1. [官方文档](https://dolphinscheduler.apache.org/python/3.0.0/index.html)
2. 本机运行 `python3 --version` 确认版本 > 3.6，否则自行安装
3. 本机运行 `python3 -m pip install apache-dolphinscheduler==3.0.0b2` 安装 PyDolphinScheduler
4. 本机运行 `pydolphinscheduler config --init` 初始化 PyDolphinScheduler 配置文件，示例配置文件可以参考项目根目录下的 pydolphinscheduler-config.yaml 文件
5. 如果想知道 PyDolphinScheduler 配置文件的存储路径，可以再执行一次 `pydolphinscheduler config --init`，会报错已存在异常，然后告诉你配置文件的路径
6. 本机运行 `pydolphinscheduler config --set java_gateway.address 127.0.0.1` 设置 api server 的连接地址，本身 dolphin scheduler api server 就集成了 PyDolphinScheduler，并且通过 25333 端口访问
7. 本机本项目目录下运行 `python tutorial.py` 将 tutorial.py 文件定义的 dag 提交到 api server，特别注意文件中的 tenant 字段的值，必须是存在的租户
8. 运行成功后，web 页面多出 “project-pydolphin” 项目，并自动创建了一个 dag 工作流，并且自动上线和调度