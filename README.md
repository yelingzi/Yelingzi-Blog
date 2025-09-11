# Yeingzi-Blog 🌸

## 项目简介
这是一个基于 Spring Boot 后端与 Vue 前端开发的个人博客系统。由于项目是在学习过程中逐步开发并经历过一次重构，代码风格可能存在些许不一致。<br>
本项目特色：
- 集成即时通讯 (IM) 系统，支持在线交流
- 集成看板娘功能（基于 [live2d](https://gitee.com/snows_l/live2d-source) 项目二次开发）
- 前后端分离架构，便于维护与扩展
<br>
如果您喜欢这个项目，欢迎点亮 star 支持！<br>


## 在线预览
- 博客前台：[https://www.yeling.top/](https://www.yeling.top/)
- 博客后台：[https://blog.yeling.top/](https://blog.yeling.top/)
- 演示账号：123456@123456.com
- 演示密码：123456

## 预览效果
![](https://www.yeling.top/image/album/show1.jpg)
![](https://www.yeling.top/image/album/show2.jpg)
![](https://www.yeling.top/image/album/show3.jpg)
![](https://www.yeling.top/image/album/show4.jpg)
![](https://www.yeling.top/image/album/show7.jpg)

## 快速开始
1.环境要求
| 组件    | 最低版本 |
| ----- | ---- |
| JDK   | 17   |
| MySQL | 5.7  |
| Redis | 5.0  |
| Node  | 18+  |
| RabbitMQ | 3.0+ |

2.项目文件
- 前端项目: <br>
  - blog-web：博客前台界面
  - blog-admin：博客后台管理系统
- 后端项目：yelingzi-blog 目录
- 数据库文件：根目录下的 blog.sql
- 密钥文件：jwtKey.json（可自行创建，格式为简单键值对）

3.后端启动步骤
1. 将 blog.sql 文件导入 MySQL 数据库
2.修改配置文件：
  - application.properties
  - application-dev.properties
3. 放置 jwtKey.json 文件到配置中指定的目录
4. 通过 Gradle 下载依赖（也可将 build.gradle 转换为 Maven 格式下载）
5. 启动项目

4.前端启动步骤
```
# 安装依赖
pnpm install

# 开发环境启动
pnpm dev
```
注意：项目未使用对象存储服务 (OSS)，而是采用 Nginx 作为文件服务器。若不经过 Nginx 反向代理，可能导致图片无法正常加载。
```
# 生产环境打包
pnpm build
```
