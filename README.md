# 智能社区服务平台

## 项目简介

智能社区服务平台是一个基于Java Web技术的社区服务管理系统，旨在为社区居民、物业管理人员和服务商提供一个集成的数字化服务平台。该平台整合了社区公告、报修服务、邻里互动、活动组织等功能，为社区居民提供一站式服务。

## 技术栈

- **后端**：Servlet、Filter、Listener、JDBC
- **前端**：HTML5、CSS3、JavaScript、Bootstrap
- **数据库**：MySQL 8.0+
- **服务器**：Tomcat 9+
- **构建工具**：Maven
- **其他**：JWT、Jackson、Apache Commons等

## 功能模块

### 1. 用户中心模块
- 多角色注册（居民、物业管理员、服务商）
- 实名认证机制
- 个人空间管理
- 消息中心

### 2. 社区服务模块
- 智能报修系统（在线提交、进度跟踪、服务评价）
- 物业服务（物业费缴纳、停车位管理、访客登记）

### 3. 社区互动模块
- 邻里圈（社区朋友圈，分享生活动态）
- 二手市场（闲置物品交易）
- 技能交换（居民技能共享）
- 失物招领（发布和查询失物信息）

### 4. 活动组织模块
- 活动发布（物业或居民发布社区活动）
- 在线报名
- 活动签到（二维码签到系统）
- 活动回顾（照片、评价分享）

## 项目结构

```
社区管理系统/
├── database/                 # 数据库脚本
│   └── smart_community.sql   # 数据库初始化脚本
├── docs/                     # 技术文档
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/hyit/zyj/icss/
│   │   │       ├── dao/      # 数据访问层
│   │   │       ├── filter/   # 过滤器
│   │   │       ├── listener/ # 监听器
│   │   │       ├── model/    # 实体类
│   │   │       ├── service/  # 业务逻辑层
│   │   │       ├── servlet/  # 控制器层
│   │   │       └── util/     # 工具类
│   │   └── webapp/
│   │       ├── WEB-INF/
│   │       │   ├── web.xml   # Web配置文件
│   │       │   └── application.properties # 应用配置文件
│   │       ├── index.html    # 首页
│   │       ├── login.html    # 登录页面
│   │       └── register.html # 注册页面
│   └── test/                 # 测试代码
│       └── java/
│           └── edu/hyit/zyj/icss/
│               └── service/
│                   └── UserServiceTest.java # 用户服务测试
├── pom.xml                   # Maven配置文件
└── README.md                 # 项目说明文件
```

## 数据库设计

### 核心表结构

1. **用户表(users)**：存储多角色用户信息
2. **报修工单表(repair_orders)**：存储报修工单信息
3. **社区活动表(community_events)**：存储社区活动信息
4. **邻里圈动态表(community_posts)**：存储邻里圈动态信息
5. **系统操作日志表(system_logs)**：存储系统操作日志

## 测试

### 单元测试
项目使用JUnit 5进行单元测试，可以通过Maven命令运行测试：

```bash
mvn test
```

目前实现了以下服务类的测试用例：

1. **UserService测试**：
   - 用户注册功能测试
   - 重复注册测试
   - 用户登录功能测试
   - 错误密码登录测试
   - 不存在用户登录测试

2. **RepairService测试**：
   - 创建报修工单测试
   - 获取报修工单测试
   - 更新工单状态测试
   - 分配服务商测试
   - 完成工单测试

3. **CommunityEventService测试**：
   - 创建社区活动测试
   - 获取社区活动测试
   - 更新活动状态测试
   - 更新参与人数测试
   - 获取已发布活动测试

4. **CommunityPostService测试**：
   - 创建邻里圈动态测试
   - 获取邻里圈动态测试
   - 更新点赞数测试
   - 更新评论数测试
   - 删除动态测试
   - 获取公开动态测试

## 部署说明

### 环境要求
- JDK 8+
- MySQL 8.0+
- Tomcat 9+
- Maven 3.6+

### 部署步骤

1. **导入数据库**
   ```sql
   CREATE DATABASE smart_community CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE smart_community;
   SOURCE database/smart_community.sql;
   ```

2. **修改应用配置**
   修改 `src/main/webapp/WEB-INF/application.properties` 中的配置信息：
   ```properties
   # 数据库配置
   db.url=jdbc:mysql://localhost:3306/smart_community?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8
   db.username=your_username
   db.password=your_password
   
   # 安全配置（生产环境建议修改）
   security.jwt.secret=your_jwt_secret_key
   security.aes.key=your_aes_encryption_key
   ```

3. **构建项目**
   ```bash
   mvn clean package
   ```

4. **部署到Tomcat**
   将生成的WAR包部署到Tomcat服务器

5. **启动服务**
   启动Tomcat服务器，访问 `http://localhost:8080/IntelligentCommunityServiceSystem`

## 前端页面

项目包含以下前端页面：

- **首页** (`index.html`)：展示平台主要功能和社区公告
- **登录页** (`login.html`)：用户登录界面
- **注册页** (`register.html`)：用户注册界面
- **用户中心** (`dashboard.html`)：用户仪表板，显示个人信息和快捷操作
- **个人资料** (`profile.html`)：用户个人信息管理和编辑
- **智能报修** (`repair.html`)：在线提交报修申请和查看报修记录
- **物业服务** (`property.html`)：物业费缴纳、停车位管理和访客登记
- **邻里圈** (`community.html`)：社区朋友圈，分享生活动态
- **二手市场** (`market.html`)：闲置物品交易平台
- **社区活动** (`events.html`)：社区活动列表和发布
- **活动详情** (`event-detail.html`)：活动详情和报名

所有页面均使用Bootstrap 5框架构建，具有响应式设计，支持PC端和移动端访问。

## 默认账户

系统初始化时会创建以下默认账户：

- **物业管理员**：
  - 用户名：admin
  - 密码：admin

- **服务商**：
  - 用户名：service
  - 密码：service

- **居民**：
  - 用户名：resident
  - 密码：resident

## API接口

### 用户相关接口
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `POST /api/user/logout` - 用户登出
- `GET /api/user/profile` - 获取用户信息

### 报修相关接口
- `POST /api/repair/create` - 创建报修工单
- `POST /api/repair/assign` - 分配服务商
- `POST /api/repair/complete` - 完成工单
- `GET /api/repair/list` - 获取工单列表
- `GET /api/repair/detail/{id}` - 获取工单详情

### 活动相关接口
- `POST /api/event/create` - 创建社区活动
- `POST /api/event/updateStatus` - 更新活动状态
- `GET /api/event/list` - 获取活动列表
- `GET /api/event/detail/{id}` - 获取活动详情

### 动态相关接口
- `POST /api/post/create` - 发布邻里圈动态
- `POST /api/post/like` - 点赞动态
- `POST /api/post/delete` - 删除动态
- `GET /api/post/list` - 获取动态列表
- `GET /api/post/detail/{id}` - 获取动态详情

## 安全机制

1. **JWT认证**：基于Token的身份验证
2. **角色权限控制**：细粒度权限控制
3. **请求日志**：记录操作审计日志
4. **数据加密**：敏感信息加密处理
5. **跨域支持**：支持前端跨域访问
6. **密码安全**：使用SHA256哈希算法加密存储用户密码
7. **安全工具类**：提供AES加密、哈希算法等安全功能

## 团队分工建议

### 成员A：核心架构与用户服务
- 系统架构设计和数据库设计
- 用户认证授权系统
- 个人中心模块
- 安全防护机制

### 成员B：业务逻辑与社区服务
- 报修系统核心业务
- 物业服务功能
- 支付集成（模拟）
- 业务逻辑层实现

### 成员C：社区互动与系统增强
- 邻里圈和活动系统
- 实时通知功能
- 数据统计和分析
- 系统监控和优化

## 开发规范

1. **代码规范**：遵循Java编码规范
2. **命名规范**：使用有意义的变量和方法名
3. **注释规范**：关键业务逻辑添加详细注释
4. **异常处理**：统一异常处理机制
5. **日志记录**：重要操作记录日志

## 扩展功能

1. **智能派单**：根据服务商位置、评分自动分配报修工单
2. **推荐算法**：基于用户行为推荐活动和邻居
3. **自动提醒**：物业费缴纳提醒、活动开始提醒
4. **实时通知**：WebSocket实现实时消息推送
5. **进度可视化**：报修进度可视化展示

## 注意事项

1. 请确保数据库连接信息正确配置
2. 部署前请先执行数据库初始化脚本
3. 生产环境中请注意修改默认账户密码
4. 建议定期备份数据库以防数据丢失

## 项目文档

项目包含以下技术文档：

- [系统架构设计文档](docs/系统架构设计文档.md)：详细描述系统架构设计
- [数据库设计文档](docs/数据库设计文档.md)：完整的数据库表结构和设计说明
- [API接口文档](docs/API接口文档.md)：所有API接口的详细说明
- [部署手册](docs/部署手册.md)：系统部署和运维指南