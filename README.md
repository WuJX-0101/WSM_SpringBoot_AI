# WMS 仓储管理系统

基于 Spring Boot + Vue 3 的现代化仓储管理系统，支持仓库管理、商品管理、库存管理、出入库管理、报表分析等功能。

## 技术栈

### 后端
- **框架**: Spring Boot 3.2.5
- **语言**: Java 17
- **ORM**: MyBatis-Plus 3.5.5
- **认证**: Sa-Token
- **数据库**: MySQL 8.0
- **缓存**: Redis
- **文档**: Knife4j (Swagger)

### 前端
- **框架**: Vue 3 + TypeScript
- **UI库**: Element Plus
- **图表**: ECharts
- **构建**: Vite
- **HTTP**: Axios

## 项目结构

```
springboot-ai-project/
├── wms-common/          # 公共模块（异常处理、工具类）
├── wms-model/           # 实体和DTO
├── wms-dao/             # 数据访问层（Mapper）
├── wms-service/         # 业务逻辑层
├── wms-web/             # Web启动模块（Controller）
├── wms-frontend/        # Vue前端
├── sql/                 # 数据库脚本
└── docker-compose.yml   # Docker配置
```

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.8+
- Node.js 18+
- MySQL 8.0
- Redis 6+

### 1. 克隆项目
```bash
git clone https://github.com/WuJX-0101/WSM_SpringBoot_AI.git
cd WSM_SpringBoot_AI
```

### 2. 数据库初始化
```bash
mysql -u root -p < sql/init.sql
```

### 3. 配置环境变量
复制 `.env.example` 为 `.env` 并修改配置：
```bash
cp .env.example .env
```

### 4. 启动后端
```bash
mvn clean package -DskipTests
java -jar wms-web/target/wms-web-1.0.0.jar
```

### 5. 启动前端
```bash
cd wms-frontend
npm install
npm run dev
```

### 6. 访问系统
- 前端地址: http://localhost:5173
- 后端地址: http://localhost:8080
- API文档: http://localhost:8080/doc.html

## 功能模块

| 模块 | 功能 |
|------|------|
| 首页仪表盘 | 库存概览、订单统计、待办事项 |
| 仓库管理 | 仓库增删改查、状态管理 |
| 库位管理 | 库位增删改查、按仓库筛选 |
| 商品管理 | 商品分类、商品信息维护 |
| 基础数据 | 供应商、客户管理 |
| 入库管理 | 入库单创建、审核、执行 |
| 出库管理 | 出库单创建、审核、执行 |
| 库存管理 | 库存列表、库存日志、库存调整 |
| 报表分析 | 订单统计、库存报表 |
| 系统管理 | 员工管理、权限配置 |

## 默认账号

| 用户名 | 密码 |
|--------|------|
| admin | 123456 |

## 许可证

MIT License
