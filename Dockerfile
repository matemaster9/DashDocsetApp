# 使用官方的 OpenJDK 17 镜像作为基础镜像
FROM openjdk:17-jdk-slim

# 将本地的 JAR 文件复制到容器中的 `/app` 目录
COPY target/DashDocsetApp-1.0.2.jar /app/

COPY bin/javadocset /app/bin/

COPY bin/application.yml /app/

# 设置工作目录为 `/app`
WORKDIR /app

# 声明容器要监听的端口号（如果有需要的话）
EXPOSE 8090

# 指定容器启动时运行的命令
#CMD ["java", "-jar", "DashDocsetApp-1.0.1.jar"]

# Start with a shell
CMD ["tail", "-f", "/dev/null"]


## 宣告应用失败，这里不能docker部署 因为javadocset关键转化组件没有找到Linux版本 所以功能只能mac版本部署