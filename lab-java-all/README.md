## JAVA 实验项目

### Maven多模块工程打包指定模块

工程执行如下命令：mvn clean package -pl 指定模块工程名 -am

参数说明：  
-am --also-make 同时构建所列模块的依赖模块；  
-amd -also-make-dependents 同时构建依赖于所列模块的模块；  
-pl --projects 构建制定的模块，模块间用逗号分隔；  
-rf -resume-from 从指定的模块恢复反应堆。  



