### DashDocsetApp 使用指南

#### 概述

`DashDocsetApp` 是一个用于自动化生成和转换 docset 的应用程序，通过 REST API 提供了以下功能：

- 自动化转化 jar 成 docset 到默认存储位置
- 批量自动化转化 jar 到 docset
- 同步转化 web jar 到 docset 到响应流
- 下载服务器本地的 docset（已弃用）

### 使用方法

#### 前提条件

确保在使用 `DashDocsetApp` 之前，你已经准备好以下环境：

- Java 开发环境（建议使用 JDK 8 及以上版本）
- Maven 构建工具
- 一个支持 REST API 调用的 HTTP 客户端或浏览器

#### 设置和运行

1. **下载和构建**

   ```bash
   git clone https://github.com/yourusername/DashDocsetApp.git
   cd DashDocsetApp
   mvn clean package
   ```

   这将克隆仓库，并使用 Maven 打包应用程序。

2. **配置**

   修改 `application.properties` 文件，设置存储位置和其他必要的配置。

3. **运行**

   ```bash
   mvn spring-boot:run
   ```

   这将启动应用程序，默认端口为 `8080`。

#### API 文档

以下是 `DashDocsetApp` 提供的主要 API 端点：

- **自动化转化 jar 到 docset**

  ```http
  POST /dash/auto-convert-jar-to-dash-docset?jar={jar_download_url}
  ```

  参数：
  - `jar`: jar 文件的下载链接地址

- **批量自动化转化 jar 到 docset**

  ```http
  POST /dash/batch-auto-convert-jar-to-dash-docset
  ```

  请求体：
  ```json
  [
    "jar_download_url_1",
    "jar_download_url_2",
    ...
  ]
  ```

- **同步转化 web jar 到 docset 到响应流**

  ```http
  POST /dash/convert-web-jar-to-dash-docset?webjar={webjar_download_url}
  ```

  参数：
  - `webjar`: web jar 文件的下载链接地址

#### 注意事项

- `/dash/download-docset` 接口已被标记为废弃，建议使用更新的接口替代。

### 联系和支持

如果你在使用 `DashDocsetApp` 过程中遇到问题或有任何建议，请随时联系我们。

- Email: sylerxu@gmail.com
- GitHub Issues: [https://github.com/yourusername/DashDocsetApp/issues](https://github.com/yourusername/DashDocsetApp/issues)

### 授权许可

这个项目使用 [MIT](LICENSE) 许可证，详细信息请查阅 LICENSE 文件。

### 致谢

感谢您使用 `DashDocsetApp`！

---
