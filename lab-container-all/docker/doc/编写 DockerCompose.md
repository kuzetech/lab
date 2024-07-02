#编写 DockerCompose

## 作用

当我们的业务越来越复杂时，需要多个容器相互配合，甚至需要多个主机组成容器集群才能满足我们的业务需求，这个时候就需要用到容器的编排工具了。因为容器编排工具可以帮助我们批量地创建、调度和管理容器，帮助我们解决规模化容器的部署问题。

Docker Compose 文件主要分为三部分：  
1. services（服务）：服务定义了容器启动的各项配置，就像我们执行docker run命令时传递的容器启动的参数一样，指定了容器应该如何启动，例如容器的启动参数，容器的镜像和环境变量等。
2. networks（网络）：网络定义了容器的网络配置，就像我们执行docker network create命令创建网络配置一样。
3. volumes（数据卷）：数据卷定义了容器的卷配置，就像我们执行docker volume create命令创建数据卷一样。

一个典型的 Docker Compose 文件结构如下：  
```
version: "3"
services:
  nginx:
    ## ... 省略部分配置
networks:
  frontend:
  backend:
volumes:
  db-data:
```

