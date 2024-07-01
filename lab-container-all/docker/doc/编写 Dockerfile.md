#编写 Dockerfile

## 常用指令

- FROM
  - Dockerfile 除了注释第一行必须是 FROM ，FROM 后面跟镜像名称，代表我们要基于哪个基础镜像构建我们的容器。
- RUN
  - RUN 后面跟一个具体的命令，类似于 Linux 命令行执行命令。
- ADD
  - 拷贝本机文件或者远程文件到镜像内
- COPY
  - 拷贝本机文件到镜像内
- USER
  - 指定容器启动的用户
- ENTRYPOINT
  - 容器的启动命令
- CMD
  - CMD 为 ENTRYPOINT 指令提供默认参数，也可以单独使用 CMD 指定容器启动参数
- ENV
  - 指定容器运行时的环境变量，格式为 key=value
- ARG
  - 定义外部变量，构建镜像时可以使用 build-arg = 的格式传递参数用于构建
- EXPOSE
  - 指定容器监听的端口，格式为 [port]/tcp 或者 [port]/udp
- WORKDIR
  - 为 Dockerfile 中跟在其后的所有 RUN、CMD、ENTRYPOINT、COPY 和 ADD 命令设置工作目录。

## 相关案例

```
FROM centos:7
COPY nginx.repo /etc/yum.repos.d/nginx.repo
RUN yum install -y nginx
EXPOSE 80
ENV HOST=mynginx
CMD ["nginx","-g","daemon off;"]
```

## 编写原则

1. 单一职责
    > 由于容器的本质是进程，一个容器代表一个进程，因此不同功能的应用应该尽量拆分为不同的容器，每个容器只负责单一业务进程
2. 提供注释信息
    > Dockerfile 也是一种代码，我们应该保持良好的代码编写习惯，晦涩难懂的代码尽量添加注释，让协作者可以一目了然地知道每一行代码的作用，并且方便扩展和使用
3. 保持容器最小化
    > 应该避免安装无用的软件包，比如在一个 nginx 镜像中，我并不需要安装 vim 、gcc 等开发编译工具。这样不仅可以加快容器构建速度，而且可以避免镜像体积过大
4. 合理选择基础镜像
    > 容器的核心是应用，因此只要基础镜像能够满足应用的运行环境即可。例如一个Java类型的应用运行时只需要JRE，并不需要JDK，因此我们的基础镜像只需要安装JRE环境即可
5. 使用 .dockerignore 文件
    > 使用.dockerignore文件允许我们在构建时，忽略一些不需要参与构建的文件，从而提升构建效率
6. 尽量使用构建缓存缩短镜像构建时间
    > Docker 构建过程中，每一条 Dockerfile 指令都会提交为一个镜像层，下一条指令都是基于上一条指令构建的。如果构建时发现要构建的镜像层的父镜像层已经存在，并且下一条命令使用了相同的指令，即可命中构建缓存。因此，基于 Docker 构建时的缓存特性，我们可以把不轻易改变的指令放到 Dockerfile 前面（例如安装软件包），而可能经常发生改变的指令放在 Dockerfile 末尾（例如编译应用程序）
7. 正确设置时区
    > 从 Docker Hub 拉取的官方操作系统镜像大多数都是 UTC 时间（世界标准时间）。如果你想要在容器中使用中国区标准时间（东八区），请根据使用的操作系统修改相应的时区信息
8. 最小化镜像层数
    > 构建镜像时尽可能地减少 Dockerfile 指令行数



## 指令书写建议

1. RUN
    > RUN指令在构建时将会生成一个新的镜像层并且执行RUN指令后面的内容。当RUN指令后面跟的内容比较复杂时，建议使用反斜杠（\） 结尾并且换行，如下
    ```
    FROM centos:7
    RUN yum install -y automake \
                        curl \
                        python \
                        vim
    ```
2. CMD 和 ENTRYPOINT
    > CMD和ENTRYPOINT指令都是容器运行的命令入口。  
    相同之处是基本使用格式都分成exec模式和shell模式：
      1. exec模式：CMD/ENTRYPOINT ["command" , "param"]  
      2. shell模式：CMD/ENTRYPOINT command param  
   > 两个指令的区别是：
      1. 如果使用了ENTRYPOINT指令，启动 Docker 容器时需要使用 --entrypoint 参数才能覆盖 Dockerfile 中的ENTRYPOINT指令 ，而使用CMD设置的命令则可以被docker run后面的参数直接覆盖。
      2. ENTRYPOINT指令可以结合CMD指令使用，也可以单独使用，而CMD指令只能单独使用
   > 最佳实践是：
      1. 使用shell模式时，Docker 会以 /bin/sh -c command 的方式执行命令。相当于我们把启动命令放在了 shell 进程中执行，等效于执行 /bin/sh -c "task command" 命令。因此 shell 模式启动的进程在容器中实际上并不是 1 号进程。
      2. 使用 exec 模式启动容器时，容器的 1 号进程就是 CMD/ENTRYPOINT 中指定的命令
      3. 无论使用CMD还是ENTRYPOINT，都尽量使用exec模式
      4. 如果你希望你的镜像足够灵活，推荐使用CMD指令
      5. 如果你的镜像只执行单一的具体程序，并且不希望用户在执行docker run时覆盖默认程序，建议使用ENTRYPOINT
3. ADD 和 COPY
    > ADD和COPY指令功能类似，都是从外部往容器内添加文件。但是COPY指令只支持基本的文件和文件夹拷贝功能，ADD则支持更多文件来源类型，比如自动提取 tar 包，并且可以支持源文件为 URL 格式。更推荐你使用COPY指令，因为COPY指令更加透明，仅支持本地文件向容器拷贝，而且使用COPY指令可以更好地利用构建缓存，有效减小镜像体积
4. WORKDIR
    > 为了使构建过程更加清晰明了，推荐使用 WORKDIR 来指定容器的工作路径，应该尽量避免使用 RUN cd /work/path && do some work 这样的指令