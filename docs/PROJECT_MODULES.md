# WMS 仓储管理系统 - 模块详解文档

## 目录

1. [项目概述](#一项目概述)
2. [模块结构总览](#二模块结构总览)
3. [wms-common 公共模块](#三wms-common-公共模块)
4. [wms-model 实体模块](#四wms-model-实体模块)
5. [wms-dao 数据访问模块](#五wms-dao-数据访问模块)
6. [wms-service 业务逻辑模块](#六wms-service-业务逻辑模块)
7. [wms-ai AI智能模块](#七wms-ai-ai智能模块)
8. [wms-integration 系统集成模块](#八wms-integration-系统集成模块)
9. [wms-web Web启动模块](#九wms-web-web启动模块)
10. [wms-frontend 前端模块](#十wms-frontend-前端模块)
11. [数据库设计](#十一数据库设计)
12. [配置与部署](#十二配置与部署)
13. [优化记录](#十三优化记录)

---

## 一、项目概述

### 1.1 项目简介

WMS (Warehouse Management System) 是一个基于 Spring Boot + Vue 3 的现代化仓储管理系统，专为小型企业内部仓储管理设计（< 1000 SKU, 1-2 仓库）。系统集成了 AI 智能分析能力，通过 DeepSeek 大模型提供需求预测、智能补货、异常检测和自然语言查询功能。

### 1.2 技术栈

| 层级 | 技术 | 版本 | 用途 |
|------|------|------|------|
| 后端框架 | Spring Boot | 3.2.5 | 应用框架 |
| 开发语言 | Java | 17 | 后端开发 |
| ORM框架 | MyBatis-Plus | 3.5.6 | 数据库操作 |
| 认证授权 | Sa-Token | 1.38.0 | 权限管理 |
| 数据库 | MySQL | 8.0 | 数据存储 |
| 缓存 | Redis | 7 | 缓存/会话 |
| 搜索引擎 | Elasticsearch | 8.13.0 | 全文搜索 |
| AI能力 | Spring AI | 1.0.0-M2 | AI集成 |
| 大模型 | DeepSeek | deepseek-v4-flash | AI推理 |
| 前端框架 | Vue | 3.5.32 | 前端开发 |
| UI组件库 | Element Plus | 2.14.0 | 界面组件 |
| 图表库 | ECharts | 6.1.0 | 数据可视化 |
| 构建工具 | Vite | 8.0.8 | 前端构建 |

### 1.3 业务定位

- **目标用户**: 小型企业仓储管理人员
- **核心场景**: 仓库管理、库存管控、出入库流程、报表分析
- **AI增强**: 智能预测、自动补货、异常预警、自然语言交互

---

## 二、模块结构总览

### 2.1 模块依赖关系

```
wms-web (启动模块, Controller层)
  ├── wms-service (业务逻辑层)
  │     ├── wms-dao (数据访问层)
  │     │     └── wms-model (实体/DTO/VO)
  │     ├── wms-common (公共工具)
  │     └── Redis/Sa-Token/Redisson
  ├── wms-ai (AI能力层)
  │     ├── wms-model
  │     ├── wms-common
  │     ├── wms-dao
  │     └── Spring AI OpenAI Starter
  └── wms-integration (系统集成层)
        ├── wms-model
        ├── wms-common
        └── WebFlux (响应式HTTP客户端)
```

### 2.2 目录结构

```
springboot-ai-project/
├── wms-common/                    # 公共模块（异常处理、工具类）
│   └── src/main/java/com/wms/common/
│       ├── core/                  # 核心类（R、PageResult）
│       └── exception/             # 异常处理
├── wms-model/                     # 实体和DTO
│   └── src/main/java/com/wms/model/
│       ├── entity/                # 数据库实体
│       ├── dto/                   # 数据传输对象
│       └── vo/                    # 视图对象
├── wms-dao/                       # 数据访问层（Mapper）
│   └── src/main/java/com/wms/dao/
│       ├── mapper/                # MyBatis Mapper接口
│       └── config/                # MyBatis配置
├── wms-service/                   # 业务逻辑层
│   └── src/main/java/com/wms/service/
│       ├── service/               # 服务接口
│       ├── impl/                  # 服务实现
│       ├── auth/                  # 认证授权
│       └── config/                # 配置类
├── wms-ai/                        # AI智能模块
│   └── src/main/java/com/wms/ai/
│       └── service/               # AI服务
├── wms-integration/               # 系统集成模块
│   └── src/main/java/com/wms/integration/
│       └── service/               # 集成服务
├── wms-web/                       # Web启动模块（Controller）
│   └── src/main/java/com/wms/web/
│       ├── controller/            # 控制器
│       └── config/                # 配置类
├── wms-frontend/                  # Vue前端
│   └── src/
│       ├── api/                   # API调用
│       ├── views/                 # 页面视图
│       ├── components/            # 公共组件
│       └── router/                # 路由配置
├── sql/                           # 数据库脚本
└── docker-compose.yml             # Docker配置
```

---

## 三、wms-common 公共模块

### 3.1 模块职责

wms-common 是系统的基础公共模块，提供统一的响应封装、分页处理、异常处理等基础设施，被所有其他模块依赖。

### 3.2 核心类详解

#### 3.2.1 R - 统一响应封装

**路径**: `com.wms.common.core.R`

```java
@Data
public class R<T> implements Serializable {
    private Integer code;    // 状态码：200成功，500失败
    private String message;  // 提示信息
    private T data;          // 响应数据
}
```

**设计思想**:
- 统一所有API的响应格式，前端可统一处理
- 提供 `R.ok()` 和 `R.fail()` 工厂方法，简化使用
- 泛型设计支持任意类型的数据封装

**使用示例**:
```java
// 成功响应
return R.ok(data);

// 带消息的成功响应
return R.ok("操作成功", data);

// 失败响应
return R.fail("参数错误");
```

#### 3.2.2 PageResult - 分页结果封装

**路径**: `com.wms.common.core.PageResult`

```java
@Data
public class PageResult<T> implements Serializable {
    private List<T> records;  // 数据列表
    private Long total;       // 总记录数
    private Long size;        // 每页大小
    private Long current;     // 当前页码
    private Long pages;       // 总页数
}
```

**设计思想**:
- 封装 MyBatis-Plus 的分页结果
- 提供标准化的分页响应格式
- 前端可直接使用进行分页展示

#### 3.2.3 BusinessException - 自定义业务异常

**路径**: `com.wms.common.exception.BusinessException`

```java
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
```

**设计思想**:
- 区分业务异常和系统异常
- 携带业务错误码，便于前端处理
- 用于业务校验失败、权限不足等场景

#### 3.2.4 GlobalExceptionHandler - 全局异常处理器

**路径**: `com.wms.common.exception.GlobalExceptionHandler`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public R<?> handleBusinessException(BusinessException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<?> handleValidationException(MethodArgumentNotValidException e) {
        // 参数校验异常处理
    }

    @ExceptionHandler(Exception.class)
    public R<?> handleException(Exception e) {
        // 兜底异常处理
    }
}
```

**设计思想**:
- 统一捕获和处理所有异常
- 避免异常信息泄露给前端
- 提供友好的错误提示

### 3.3 依赖说明

| 依赖 | 版本 | 用途 |
|------|------|------|
| spring-boot-starter-web | 3.2.5 | Web支持 |
| spring-boot-starter-validation | 3.2.5 | 参数校验 |
| hutool-all | 5.8.25 | 工具类库 |
| easyexcel | 3.3.4 | Excel导入导出 |
| zxing | 3.5.3 | 二维码生成 |

---

## 四、wms-model 实体模块

### 4.1 模块职责

wms-model 定义了系统的所有数据模型，包括数据库实体（Entity）、数据传输对象（DTO）和视图对象（VO），是各层之间数据传递的载体。

### 4.2 实体类详解

#### 4.2.1 BaseEntity - 实体基类

**路径**: `com.wms.model.entity.BaseEntity`

```java
@Data
public class BaseEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime gmtCreate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime gmtModified;

    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
}
```

**设计要点**:
- **雪花算法ID**: 使用 `IdType.ASSIGN_ID` 生成分布式唯一ID，解决数据库自增ID的扩展问题
- **Long转String**: 使用 `@JsonSerialize(using = ToStringSerializer.class)` 解决前端JavaScript Long精度丢失问题
- **逻辑删除**: 使用 `@TableLogic` 注解实现软删除，数据可恢复
- **自动填充**: 通过 `FieldFill` 实现公共字段自动填充，减少手动维护

#### 4.2.2 SysUser - 用户实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| username | String | 用户名（唯一索引） |
| password | String | 密码（BCrypt加密） |
| nickname | String | 昵称 |
| email | String | 邮箱 |
| phone | String | 手机号 |
| avatar | String | 头像URL |
| status | Integer | 状态（0禁用/1启用） |

#### 4.2.3 WmsWarehouse - 仓库实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| warehouseCode | String | 仓库编码（唯一索引） |
| warehouseName | String | 仓库名称 |
| warehouseType | String | 仓库类型 |
| address | String | 仓库地址 |
| contactPerson | String | 联系人 |
| contactPhone | String | 联系电话 |
| area | BigDecimal | 仓库面积 |
| status | Integer | 状态（0禁用/1启用） |

#### 4.2.4 WmsLocation - 库位实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| warehouseId | Long | 所属仓库ID |
| locationCode | String | 库位编码（唯一索引） |
| locationName | String | 库位名称 |
| locationType | String | 库位类型 |
| area | BigDecimal | 库位面积 |
| shelf | String | 货架编号 |
| layer | String | 层号 |
| position | String | 位置编号 |
| capacity | Integer | 库位容量 |
| status | Integer | 状态（0禁用/1启用） |

#### 4.2.5 WmsProduct - 商品实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| productCode | String | 商品编码（唯一索引） |
| productName | String | 商品名称 |
| categoryId | Long | 分类ID |
| brand | String | 品牌 |
| specification | String | 规格 |
| unit | String | 单位 |
| barcode | String | 条形码 |
| weight | BigDecimal | 重量 |
| volume | BigDecimal | 体积 |
| purchasePrice | BigDecimal | 采购价 |
| salePrice | BigDecimal | 销售价 |
| safetyStock | Integer | 安全库存 |
| minStock | Integer | 最小库存 |
| maxStock | Integer | 最大库存 |
| image | String | 商品图片 |
| status | Integer | 状态（0禁用/1启用） |

#### 4.2.6 WmsInboundOrder - 入库单实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| orderNo | String | 入库单号（唯一索引） |
| orderType | Integer | 单据类型（1采购入库/2退货入库/3调拨入库） |
| warehouseId | Long | 入库仓库ID |
| supplierId | Long | 供应商ID |
| orderStatus | Integer | 单据状态（0待审核/1已审核/2已入库/3已取消） |
| totalQuantity | Integer | 总数量 |
| totalAmount | BigDecimal | 总金额 |
| inboundTime | LocalDateTime | 入库时间 |

#### 4.2.7 WmsOutboundOrder - 出库单实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| orderNo | String | 出库单号（唯一索引） |
| orderType | Integer | 单据类型（1销售出库/2退货出库/3调拨出库） |
| warehouseId | Long | 出库仓库ID |
| customerId | Long | 客户ID |
| orderStatus | Integer | 单据状态（0待审核/1已审核/2已出库/3已取消） |
| totalQuantity | Integer | 总数量 |
| totalAmount | BigDecimal | 总金额 |
| outboundTime | LocalDateTime | 出库时间 |

#### 4.2.8 WmsInventory - 库存实体

**字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| warehouseId | Long | 仓库ID |
| locationId | Long | 库位ID |
| productId | Long | 商品ID |
| batchNo | String | 批次号 |
| quantity | Integer | 库存数量 |
| lockedQuantity | Integer | 锁定数量 |
| availableQuantity | Integer | 可用数量 |
| costPrice | BigDecimal | 成本价 |
| productionDate | LocalDate | 生产日期 |
| expiryDate | LocalDate | 过期日期 |

### 4.3 DTO 详解

#### 4.3.1 LoginDTO - 登录请求

```java
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}
```

#### 4.3.2 InboundOrderDTO - 入库单请求

```java
@Data
public class InboundOrderDTO {
    @NotNull(message = "单据类型不能为空")
    private Integer orderType;

    @NotNull(message = "仓库ID不能为空")
    private Long warehouseId;

    private Long supplierId;

    @Valid
    @NotEmpty(message = "入库明细不能为空")
    private List<InboundOrderItemDTO> items;
}
```

### 4.4 VO 详解

#### 4.4.1 LoginVO - 登录响应

```java
@Data
@Schema(description = "登录响应")
public class LoginVO {
    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserVO user;
}
```

#### 4.4.2 DemandForecastVO - 需求预测响应

```java
@Data
@Schema(description = "需求预测结果")
public class DemandForecastVO {
    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "预测数量")
    private Integer forecastQuantity;

    @Schema(description = "置信度")
    private Double confidence;

    @Schema(description = "预测说明")
    private String explanation;

    @Schema(description = "建议")
    private String suggestion;

    @Schema(description = "建议补货时间")
    private String suggestedReorderTime;
}
```

---

## 五、wms-dao 数据访问模块

### 5.1 模块职责

wms-dao 是数据访问层，封装了所有数据库操作，提供 MyBatis Mapper 接口和 MyBatis-Plus 配置。

### 5.2 Mapper 接口详解

#### 5.2.1 SysUserMapper - 用户Mapper

**核心方法**:

```java
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
    
    /**
     * 查询用户角色编码列表
     * 多表关联: sys_user -> sys_user_role -> sys_role
     */
    @Select("""
        SELECT r.role_code 
        FROM sys_role r 
        INNER JOIN sys_user_role ur ON r.id = ur.role_id 
        WHERE ur.user_id = #{userId} AND r.is_deleted = 0
        """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户权限标识列表
     * 多表关联: sys_user -> sys_user_role -> sys_role -> sys_role_menu -> sys_menu
     */
    @Select("""
        SELECT DISTINCT m.perms 
        FROM sys_menu m 
        INNER JOIN sys_role_menu rm ON m.id = rm.menu_id 
        INNER JOIN sys_user_role ur ON rm.role_id = ur.role_id 
        WHERE ur.user_id = #{userId} AND m.is_deleted = 0 AND m.perms IS NOT NULL
        """)
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);
}
```

**设计要点**:
- 使用注解SQL而非XML，简化多表关联查询
- 查询角色和权限时进行多表关联，避免N+1问题

#### 5.2.2 其他Mapper接口

所有业务Mapper继承 `BaseMapper<T>`，获得CRUD能力：

```java
@Mapper
public interface WmsWarehouseMapper extends BaseMapper<WmsWarehouse> {}

@Mapper
public interface WmsProductMapper extends BaseMapper<WmsProduct> {}

@Mapper
public interface WmsInventoryMapper extends BaseMapper<WmsInventory> {}
```

### 5.3 MyBatis-Plus 配置

#### 5.3.1 MybatisPlusConfig - 分页配置

**路径**: `com.wms.dao.config.MybatisPlusConfig`

```java
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        interceptor.addInnerInterceptor(
            new PaginationInnerInterceptor(DbType.MYSQL)
        );
        return interceptor;
    }
}
```

**作用**: 启用 MyBatis-Plus 分页功能，支持 `Page<T>` 分页查询。

#### 5.3.2 MybatisPlusMetaObjectHandler - 自动填充处理器

**路径**: `com.wms.dao.config.MybatisPlusMetaObjectHandler`

```java
@Component
public class MybatisPlusMetaObjectHandler implements MetaObjectHandler {
    
    @Override
    public void insertFill(MetaObject metaObject) {
        // 插入时自动填充
        this.strictInsertFill(metaObject, "gmtCreate", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "isDeleted", Integer.class, 0);
        this.strictInsertFill(metaObject, "createBy", String.class, getCurrentUser());
        this.strictInsertFill(metaObject, "updateBy", String.class, getCurrentUser());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 更新时自动填充
        this.strictUpdateFill(metaObject, "gmtModified", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, getCurrentUser());
    }
}
```

**作用**:
- 插入记录时自动填充创建时间、修改时间、创建人、修改人、逻辑删除字段
- 更新记录时自动填充修改时间、修改人
- 避免手动维护公共字段，减少代码冗余

---

## 六、wms-service 业务逻辑模块

### 6.1 模块职责

wms-service 是系统的核心业务逻辑层，包含所有业务服务的接口定义和实现，处理认证授权、缓存、业务校验等核心逻辑。

### 6.2 认证授权服务

#### 6.2.1 AuthService - 认证服务

**接口定义**:

```java
public interface AuthService {
    LoginVO login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserVO getCurrentUserInfo();
    void logout();
    PageResult<UserVO> getEmployeeList(String keyword, Integer page, Integer size);
    void createEmployee(EmployeeDTO dto);
    void updateEmployee(Long id, EmployeeUpdateDTO dto);
    void deleteEmployee(Long id);
}
```

**核心实现 - login方法**:

```java
@Override
public LoginVO login(LoginDTO dto) {
    // 1. 查询用户
    SysUser user = sysUserMapper.selectOne(
        new LambdaQueryWrapper<SysUser>()
            .eq(SysUser::getUsername, dto.getUsername())
    );
    
    // 2. 校验用户状态
    if (user == null || user.getStatus() == 0) {
        throw new BusinessException("用户名或密码错误");
    }
    
    // 3. 校验密码 (BCrypt)
    if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
        throw new BusinessException("用户名或密码错误");
    }
    
    // 4. Sa-Token 登录
    StpUtil.login(user.getId());
    
    // 5. 查询角色和权限
    List<String> roles = sysUserMapper.selectRoleCodesByUserId(user.getId());
    List<String> permissions = sysUserMapper.selectPermissionsByUserId(user.getId());
    
    // 6. 构建响应
    LoginVO vo = new LoginVO();
    vo.setToken(StpUtil.getTokenValue());
    vo.setUser(buildUserVO(user, roles, permissions));
    
    return vo;
}
```

**设计要点**:
- BCrypt密码加密，安全性高
- Sa-Token生成Token，支持分布式会话
- 一次查询获取角色和权限，避免N+1问题

#### 6.2.2 StpInterfaceImpl - 权限认证实现

**路径**: `com.wms.service.auth.StpInterfaceImpl`

```java
@Component
public class StpInterfaceImpl implements StpInterface {
    
    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return sysUserMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.parseLong(loginId.toString());
        return sysUserMapper.selectRoleCodesByUserId(userId);
    }
}
```

**作用**: 实现 Sa-Token 的权限认证接口，用于 `@SaCheckPermission` 和 `@SaCheckRole` 注解的权限校验。

### 6.3 业务服务详解

#### 6.3.1 InboundOrderService - 入库服务

**核心功能**:

```java
public interface InboundOrderService {
    void create(InboundOrderDTO dto);
    void audit(Long id);
    void execute(Long id);
    void cancel(Long id);
    InboundOrderVO getDetail(Long id);
    PageResult<InboundOrderVO> list(String keyword, Integer page, Integer size);
}
```

**创建入库单流程**:

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void create(InboundOrderDTO dto) {
    // 1. 生成入库单号: IN + 日期 + 4位序号
    String orderNo = generateOrderNo("IN");
    
    // 2. 计算总数量和总金额
    int totalQuantity = dto.getItems().stream()
        .mapToInt(InboundOrderItemDTO::getQuantity)
        .sum();
    BigDecimal totalAmount = dto.getItems().stream()
        .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    
    // 3. 保存入库单主表
    WmsInboundOrder order = new WmsInboundOrder();
    order.setOrderNo(orderNo);
    order.setOrderType(dto.getOrderType());
    order.setWarehouseId(dto.getWarehouseId());
    order.setSupplierId(dto.getSupplierId());
    order.setOrderStatus(0); // 待审核
    order.setTotalQuantity(totalQuantity);
    order.setTotalAmount(totalAmount);
    inboundOrderMapper.insert(order);
    
    // 4. 保存入库单明细
    for (InboundOrderItemDTO item : dto.getItems()) {
        WmsInboundOrderItem orderItem = new WmsInboundOrderItem();
        orderItem.setOrderId(order.getId());
        // ... 设置其他字段
        inboundOrderItemMapper.insert(orderItem);
    }
}
```

**执行入库流程**:

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void execute(Long id) {
    // 1. 查询入库单
    WmsInboundOrder order = inboundOrderMapper.selectById(id);
    if (order.getOrderStatus() != 1) {
        throw new BusinessException("只有已审核的入库单才能执行");
    }
    
    // 2. 查询入库单明细
    List<WmsInboundOrderItem> items = inboundOrderItemMapper.selectList(
        new LambdaQueryWrapper<WmsInboundOrderItem>()
            .eq(WmsInboundOrderItem::getOrderId, id)
    );
    
    // 3. 更新库存
    for (WmsInboundOrderItem item : items) {
        // 查询或创建库存记录
        WmsInventory inventory = inventoryMapper.selectOne(
            new LambdaQueryWrapper<WmsInventory>()
                .eq(WmsInventory::getWarehouseId, order.getWarehouseId())
                .eq(WmsInventory::getProductId, item.getProductId())
                .eq(WmsInventory::getBatchNo, item.getBatchNo())
        );
        
        if (inventory == null) {
            // 新建库存记录
            inventory = new WmsInventory();
            inventory.setWarehouseId(order.getWarehouseId());
            inventory.setProductId(item.getProductId());
            inventory.setBatchNo(item.getBatchNo());
            inventory.setQuantity(item.getQuantity());
            inventory.setAvailableQuantity(item.getQuantity());
            inventoryMapper.insert(inventory);
        } else {
            // 更新库存数量
            inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() + item.getQuantity());
            inventoryMapper.updateById(inventory);
        }
        
        // 4. 记录库存日志
        WmsInventoryLog log = new WmsInventoryLog();
        log.setWarehouseId(order.getWarehouseId());
        log.setProductId(item.getProductId());
        log.setChangeType(1); // 入库
        log.setChangeQuantity(item.getQuantity());
        log.setBeforeQuantity(inventory.getQuantity() - item.getQuantity());
        log.setAfterQuantity(inventory.getQuantity());
        log.setOrderNo(order.getOrderNo());
        inventoryLogMapper.insert(log);
    }
    
    // 5. 更新入库单状态
    order.setOrderStatus(2); // 已入库
    order.setInboundTime(LocalDateTime.now());
    inboundOrderMapper.updateById(order);
}
```

#### 6.3.2 OutboundOrderService - 出库服务

**出库与入库的主要区别**:

1. **库存校验**: 出库前需检查可用库存是否充足
2. **库存扣减**: 出库时扣减库存，入库时增加库存
3. **单号前缀**: 出库单号以 "OUT" 开头

**执行出库流程**:

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void execute(Long id) {
    // 1. 查询出库单
    WmsOutboundOrder order = outboundOrderMapper.selectById(id);
    if (order.getOrderStatus() != 1) {
        throw new BusinessException("只有已审核的出库单才能执行");
    }
    
    // 2. 查询出库单明细
    List<WmsOutboundOrderItem> items = outboundOrderItemMapper.selectList(
        new LambdaQueryWrapper<WmsOutboundOrderItem>()
            .eq(WmsOutboundOrderItem::getOrderId, id)
    );
    
    // 3. 校验库存并扣减
    for (WmsOutboundOrderItem item : items) {
        WmsInventory inventory = inventoryMapper.selectOne(
            new LambdaQueryWrapper<WmsInventory>()
                .eq(WmsInventory::getWarehouseId, order.getWarehouseId())
                .eq(WmsInventory::getProductId, item.getProductId())
                .eq(WmsInventory::getBatchNo, item.getBatchNo())
        );
        
        // 校验库存
        if (inventory == null || inventory.getAvailableQuantity() < item.getQuantity()) {
            throw new BusinessException("库存不足");
        }
        
        // 扣减库存
        inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - item.getQuantity());
        inventoryMapper.updateById(inventory);
        
        // 记录库存日志
        // ...
    }
    
    // 4. 更新出库单状态
    order.setOrderStatus(2); // 已出库
    order.setOutboundTime(LocalDateTime.now());
    outboundOrderMapper.updateById(order);
}
```

#### 6.3.3 InventoryService - 库存服务

**核心功能**:

```java
public interface InventoryService {
    InventoryVO getDetail(Long id);
    PageResult<InventoryVO> list(Long warehouseId, Long productId, Integer page, Integer size);
    List<InventoryVO> getByWarehouse(Long warehouseId);
    List<InventoryVO> getByProduct(Long productId);
    Long getTotalInventory();
    void adjust(Long id, InventoryAdjustDTO dto);
    PageResult<InventoryLogVO> getLogList(Long warehouseId, Long productId, Integer page, Integer size);
}
```

**库存调整流程**:

```java
@Override
@Transactional(rollbackFor = Exception.class)
public void adjust(Long id, InventoryAdjustDTO dto) {
    // 1. 查询库存
    WmsInventory inventory = inventoryMapper.selectById(id);
    
    // 2. 计算调整后数量
    int beforeQuantity = inventory.getQuantity();
    int afterQuantity = beforeQuantity + dto.getAdjustQuantity();
    
    if (afterQuantity < 0) {
        throw new BusinessException("调整后库存不能为负数");
    }
    
    // 3. 更新库存
    inventory.setQuantity(afterQuantity);
    inventory.setAvailableQuantity(afterQuantity - inventory.getLockedQuantity());
    inventoryMapper.updateById(inventory);
    
    // 4. 记录库存日志
    WmsInventoryLog log = new WmsInventoryLog();
    log.setWarehouseId(inventory.getWarehouseId());
    log.setProductId(inventory.getProductId());
    log.setChangeType(4); // 调整
    log.setChangeQuantity(dto.getAdjustQuantity());
    log.setBeforeQuantity(beforeQuantity);
    log.setAfterQuantity(afterQuantity);
    log.setRemark(dto.getRemark());
    inventoryLogMapper.insert(log);
}
```

### 6.4 配置类

#### 6.4.1 SaTokenConfig - 认证拦截配置

**路径**: `com.wms.service.config.SaTokenConfig`

```java
@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> {
            // 拦截所有请求
            SaRouter.match("/**")
                // 排除登录注册接口
                .notMatch("/api/v1/auth/login", "/api/v1/auth/register")
                // 排除Swagger文档
                .notMatch("/doc.html", "/webjars/**", "/swagger-resources/**")
                // 校验登录
                .check(r -> StpUtil.checkLogin());
        }));
    }
}
```

#### 6.4.2 RedisConfig - Redis序列化配置

**路径**: `com.wms.service.config.RedisConfig`

```java
@Configuration
public class RedisConfig {
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        
        // Key使用String序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value使用JSON序列化
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        
        template.afterPropertiesSet();
        return template;
    }
}
```

---

## 七、wms-ai AI智能模块

### 7.1 模块职责

wms-ai 是系统的AI智能模块，通过集成 DeepSeek 大模型，提供需求预测、智能补货、异常检测和自然语言查询功能。

### 7.2 AI服务详解

#### 7.2.1 AiService - AI服务接口

```java
public interface AiService {
    DemandForecastVO forecastDemand(Long productId);
    List<ReplenishmentVO> getReplenishmentSuggestion();
    AnomalyDetectionVO detectAnomaly();
    String chat(String message);
}
```

#### 7.2.2 AiServiceImpl - AI服务实现

**核心实现**:

```java
@Service
@Slf4j
public class AiServiceImpl implements AiService {
    
    @Autowired
    private ChatClient.Builder chatClientBuilder;
    
    @Autowired
    private WmsProductMapper productMapper;
    
    @Autowired
    private WmsInventoryMapper inventoryMapper;
    
    // ... 其他依赖
    
    private ChatClient chatClient;
    
    @PostConstruct
    public void init() {
        this.chatClient = chatClientBuilder.build();
    }
}
```

### 7.3 AI功能详解

#### 7.3.1 需求预测 (forecastDemand)

**功能描述**: 基于历史销售数据预测未来需求量，给出补货建议。

**实现流程**:

```java
@Override
public DemandForecastVO forecastDemand(Long productId) {
    // 1. 获取商品信息
    WmsProduct product = productMapper.selectById(productId);
    
    // 2. 获取最近30天出库数据
    LocalDateTime startDate = LocalDateTime.now().minusDays(30);
    List<WmsOutboundOrderItem> outboundItems = outboundOrderItemMapper.selectList(
        new LambdaQueryWrapper<WmsOutboundOrderItem>()
            .eq(WmsOutboundOrderItem::getProductId, productId)
    );
    
    // 3. 按日期统计销量
    Map<LocalDate, Integer> dailySales = outboundItems.stream()
        .collect(Collectors.groupingBy(
            item -> item.getCreateDate().toLocalDate(),
            Collectors.summingInt(WmsOutboundOrderItem::getQuantity)
        ));
    
    // 4. 计算日均销量
    double avgDailySales = dailySales.values().stream()
        .mapToInt(Integer::intValue)
        .average()
        .orElse(0);
    
    // 5. 构建Prompt
    String prompt = buildForecastPrompt(product, dailySales, avgDailySales);
    
    // 6. 调用AI
    String response = chatClient.prompt(prompt).call().content();
    
    // 7. 解析JSON响应
    DemandForecastVO result = parseForecastResponse(response, productId);
    
    // 8. 计算建议补货时间
    WmsInventory inventory = inventoryMapper.selectOne(
        new LambdaQueryWrapper<WmsInventory>()
            .eq(WmsInventory::getProductId, productId)
    );
    
    if (inventory != null && avgDailySales > 0) {
        int daysUntilReorder = (int) ((inventory.getQuantity() - product.getSafetyStock()) / avgDailySales);
        result.setSuggestedReorderTime("预计 " + daysUntilReorder + " 天后需要补货");
    }
    
    return result;
}
```

**Prompt设计**:

```java
private String buildForecastPrompt(WmsProduct product, Map<LocalDate, Integer> dailySales, double avgDailySales) {
    StringBuilder prompt = new StringBuilder();
    prompt.append("你是一个专业的仓储管理AI助手。\n\n");
    prompt.append("## 商品信息\n");
    prompt.append("- 商品名称: ").append(product.getProductName()).append("\n");
    prompt.append("- 安全库存: ").append(product.getSafetyStock()).append("\n");
    prompt.append("- 最小库存: ").append(product.getMinStock()).append("\n");
    prompt.append("- 最大库存: ").append(product.getMaxStock()).append("\n\n");
    
    prompt.append("## 历史销售数据（最近30天）\n");
    dailySales.forEach((date, quantity) -> 
        prompt.append("- ").append(date).append(": ").append(quantity).append("件\n")
    );
    prompt.append("\n日均销量: ").append(String.format("%.2f", avgDailySales)).append("件\n\n");
    
    prompt.append("## 任务\n");
    prompt.append("请分析以上数据，预测未来7天的需求量，并给出补货建议。\n");
    prompt.append("请返回JSON格式：\n");
    prompt.append("{\n");
    prompt.append("  \"forecastQuantity\": 预测数量,\n");
    prompt.append("  \"confidence\": 置信度(0-1),\n");
    prompt.append("  \"explanation\": 预测说明,\n");
    prompt.append("  \"suggestion\": 补货建议\n");
    prompt.append("}\n");
    
    return prompt.toString();
}
```

#### 7.3.2 智能补货 (getReplenishmentSuggestion)

**功能描述**: 分析库存水平，自动生成补货建议。

**实现流程**:

```java
@Override
public List<ReplenishmentVO> getReplenishmentSuggestion() {
    // 1. 获取所有库存信息
    List<WmsInventory> inventories = inventoryMapper.selectList(null);
    
    // 2. 按商品汇总库存
    Map<Long, Integer> productInventoryMap = inventories.stream()
        .collect(Collectors.groupingBy(
            WmsInventory::getProductId,
            Collectors.summingInt(WmsInventory::getQuantity)
        ));
    
    // 3. 获取所有商品信息
    List<WmsProduct> products = productMapper.selectList(null);
    
    // 4. 筛选低于安全库存的商品
    List<WmsProduct> lowStockProducts = products.stream()
        .filter(p -> {
            int currentStock = productInventoryMap.getOrDefault(p.getId(), 0);
            return currentStock < p.getSafetyStock();
        })
        .collect(Collectors.toList());
    
    // 5. 构建Prompt
    String prompt = buildReplenishmentPrompt(lowStockProducts, productInventoryMap);
    
    // 6. 调用AI
    String response = chatClient.prompt(prompt).call().content();
    
    // 7. 解析响应并返回
    return parseReplenishmentResponse(response, lowStockProducts, productInventoryMap);
}
```

#### 7.3.3 异常检测 (detectAnomaly)

**功能描述**: 检测库存异常情况，如库存不足、库存过期等。

**实现流程**:

```java
@Override
public AnomalyDetectionVO detectAnomaly() {
    // 1. 获取所有库存
    List<WmsInventory> inventories = inventoryMapper.selectList(null);
    
    // 2. 获取所有商品
    Map<Long, WmsProduct> productMap = products.stream()
        .collect(Collectors.toMap(WmsProduct::getId, Function.identity()));
    
    // 3. 检测库存异常
    List<AnomalyItem> anomalies = new ArrayList<>();
    
    for (WmsInventory inventory : inventories) {
        WmsProduct product = productMap.get(inventory.getProductId());
        
        // 库存为零
        if (inventory.getQuantity() == 0) {
            anomalies.add(new AnomalyItem("库存为零", "HIGH", product.getProductName()));
        }
        
        // 低于安全库存
        if (inventory.getQuantity() < product.getSafetyStock()) {
            String level = inventory.getQuantity() < product.getMinStock() ? "HIGH" : "MEDIUM";
            anomalies.add(new AnomalyItem("低于安全库存", level, product.getProductName()));
        }
        
        // 超过最大库存
        if (inventory.getQuantity() > product.getMaxStock()) {
            anomalies.add(new AnomalyItem("超过最大库存", "LOW", product.getProductName()));
        }
        
        // 检查过期
        if (inventory.getExpiryDate() != null && 
            inventory.getExpiryDate().isBefore(LocalDate.now())) {
            anomalies.add(new AnomalyItem("已过期", "HIGH", product.getProductName()));
        }
    }
    
    // 4. 汇总统计
    AnomalyDetectionVO result = new AnomalyDetectionVO();
    result.setTotalAnomalies(anomalies.size());
    result.setHighPriority((int) anomalies.stream()
        .filter(a -> "HIGH".equals(a.getLevel()))
        .count());
    result.setAnomalies(anomalies);
    
    return result;
}
```

#### 7.3.4 自然语言查询 (chat)

**功能描述**: 通过自然语言与系统交互，查询库存、订单等信息。

**实现流程**:

```java
@Override
public String chat(String message) {
    // 1. 获取系统数据概览
    long totalProducts = productMapper.selectCount(null);
    long totalWarehouses = warehouseMapper.selectCount(null);
    
    // 2. 获取商品库存详情
    List<WmsInventory> inventories = inventoryMapper.selectList(null);
    Map<Long, WmsProduct> productMap = products.stream()
        .collect(Collectors.toMap(WmsProduct::getId, Function.identity()));
    
    // 3. 构建上下文
    StringBuilder context = new StringBuilder();
    context.append("## 系统数据概览\n");
    context.append("- 商品种类: ").append(totalProducts).append("\n");
    context.append("- 仓库数量: ").append(totalWarehouses).append("\n\n");
    
    context.append("## 商品库存详情\n");
    for (WmsInventory inventory : inventories) {
        WmsProduct product = productMap.get(inventory.getProductId());
        context.append("- ").append(product.getProductName())
               .append(": 当前库存 ").append(inventory.getQuantity())
               .append(", 安全库存 ").append(product.getSafetyStock())
               .append(", 销售价 ").append(product.getSalePrice())
               .append("\n");
    }
    
    // 4. 构建Prompt
    String prompt = "你是一个专业的仓储管理AI助手。\n\n" +
                    context.toString() + "\n" +
                    "## 用户问题\n" + message + "\n\n" +
                    "请根据以上数据回答用户问题。";
    
    // 5. 调用AI
    return chatClient.prompt(prompt).call().content();
}
```

---

## 八、wms-integration 系统集成模块

### 8.1 模块职责

wms-integration 是系统的外部系统集成模块，负责与 ERP、电商平台、财务系统等外部系统对接。当前为桩实现，预留了完整的接口定义。

### 8.2 集成服务详解

#### 8.2.1 ErpIntegrationService - ERP集成

**接口定义**:

```java
public interface ErpIntegrationService {
    SyncResultVO testConnection();
    SyncResultVO syncProducts();
    SyncResultVO syncInventory();
    SyncResultVO syncInboundOrders();
    SyncResultVO syncOutboundOrders();
    SyncResultVO pullPurchaseOrders();
}
```

**当前实现**:

```java
@Service
@Slf4j
public class ErpIntegrationServiceImpl implements ErpIntegrationService {
    
    @Override
    public SyncResultVO testConnection() {
        // TODO: 实现ERP连接测试
        SyncResultVO result = new SyncResultVO();
        result.setSuccess(false);
        result.setMessage("ERP接口未配置");
        return result;
    }
    
    @Override
    public SyncResultVO syncProducts() {
        // TODO: 实现商品同步到ERP
        SyncResultVO result = new SyncResultVO();
        result.setSuccess(false);
        result.setMessage("ERP接口未配置");
        return result;
    }
    
    // ... 其他方法类似
}
```

#### 8.2.2 EcommerceIntegrationService - 电商平台集成

**接口定义**:

```java
public interface EcommerceIntegrationService {
    SyncResultVO testConnection();
    SyncResultVO syncProducts();
    SyncResultVO syncInventory();
    SyncResultVO pullOrders();
    SyncResultVO pushShippingInfo(String orderNo);
}
```

#### 8.2.3 FinanceIntegrationService - 财务系统集成

**接口定义**:

```java
public interface FinanceIntegrationService {
    SyncResultVO testConnection();
    SyncResultVO syncInboundOrders();
    SyncResultVO syncOutboundOrders();
    SyncResultVO syncInventoryCost();
    SyncResultVO pushReceivables();
    SyncResultVO pushPayables();
}
```

### 8.3 技术特点

- **响应式HTTP客户端**: 使用 WebFlux 的 WebClient 进行HTTP调用
- **异步处理**: 支持异步同步数据
- **错误处理**: 统一的错误处理和重试机制（预留）

---

## 九、wms-web Web启动模块

### 9.1 模块职责

wms-web 是系统的启动模块，包含 Spring Boot 启动类、Controller 层、配置类，负责接收HTTP请求并返回响应。

### 9.2 启动类

**路径**: `com.wms.web.WmsApplication`

```java
@SpringBootApplication
@ComponentScan("com.wms")
@MapperScan("com.wms.dao.mapper")
public class WmsApplication {
    public static void main(String[] args) {
        // 加载 .env 文件
        Dotenv dotenv = Dotenv.configure().load();
        
        // 设置环境变量
        System.setProperty("DEEPSEEK_API_KEY", dotenv.get("DEEPSEEK_API_KEY"));
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        // ... 其他环境变量
        
        SpringApplication.run(WmsApplication.class, args);
    }
}
```

**设计要点**:
- 使用 java-dotenv 加载 .env 文件，便于环境配置管理
- `@ComponentScan("com.wms")` 扫描所有模块的组件
- `@MapperScan("com.wms.dao.mapper")` 扫描所有Mapper接口

### 9.3 Controller 详解

#### 9.3.1 AuthController - 认证控制器

**路径**: `com.wms.web.controller.AuthController`

```java
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证管理", description = "用户登录、注册、员工管理")
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public R<LoginVO> login(@RequestBody @Validated LoginDTO dto) {
        return R.ok(authService.login(dto));
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public R<Void> register(@RequestBody @Validated RegisterDTO dto) {
        authService.register(dto);
        return R.ok();
    }
    
    @GetMapping("/info")
    @Operation(summary = "获取当前用户信息")
    @SaCheckLogin
    public R<UserVO> info() {
        return R.ok(authService.getCurrentUserInfo());
    }
    
    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    @SaCheckLogin
    public R<Void> logout() {
        authService.logout();
        return R.ok();
    }
    
    @GetMapping("/employee/list")
    @Operation(summary = "员工列表")
    @SaCheckPermission("system:employee:list")
    public R<PageResult<UserVO>> employeeList(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(authService.getEmployeeList(keyword, page, size));
    }
    
    // ... 其他员工CRUD接口
}
```

#### 9.3.2 InboundOrderController - 入库控制器

**路径**: `com.wms.web.controller.InboundOrderController`

```java
@RestController
@RequestMapping("/api/v1/inbound")
@Tag(name = "入库管理", description = "入库单的创建、审核、执行")
public class InboundOrderController {
    
    @Autowired
    private InboundOrderService inboundOrderService;
    
    @PostMapping
    @Operation(summary = "创建入库单")
    @SaCheckPermission("inbound:create")
    public R<Void> create(@RequestBody @Validated InboundOrderDTO dto) {
        inboundOrderService.create(dto);
        return R.ok();
    }
    
    @PutMapping("/{id}/audit")
    @Operation(summary = "审核入库单")
    @SaCheckPermission("inbound:audit")
    public R<Void> audit(@PathVariable Long id) {
        inboundOrderService.audit(id);
        return R.ok();
    }
    
    @PutMapping("/{id}/execute")
    @Operation(summary = "执行入库")
    @SaCheckPermission("inbound:execute")
    public R<Void> execute(@PathVariable Long id) {
        inboundOrderService.execute(id);
        return R.ok();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "入库单详情")
    @SaCheckPermission("inbound:view")
    public R<InboundOrderVO> detail(@PathVariable Long id) {
        return R.ok(inboundOrderService.getDetail(id));
    }
    
    @GetMapping("/list")
    @Operation(summary = "入库单列表")
    @SaCheckPermission("inbound:list")
    public R<PageResult<InboundOrderVO>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return R.ok(inboundOrderService.list(keyword, page, size));
    }
}
```

#### 9.3.3 AiController - AI控制器

**路径**: `com.wms.web.controller.AiController`

```java
@RestController
@RequestMapping("/api/v1/ai")
@Tag(name = "AI智能", description = "AI预测、补货、异常检测、自然语言查询")
public class AiController {
    
    @Autowired
    private AiService aiService;
    
    @GetMapping("/forecast/{productId}")
    @Operation(summary = "需求预测")
    @SaCheckPermission("ai:forecast")
    public R<DemandForecastVO> forecast(@PathVariable Long productId) {
        return R.ok(aiService.forecastDemand(productId));
    }
    
    @GetMapping("/replenishment")
    @Operation(summary = "智能补货建议")
    @SaCheckPermission("ai:replenishment")
    public R<List<ReplenishmentVO>> replenishment() {
        return R.ok(aiService.getReplenishmentSuggestion());
    }
    
    @GetMapping("/anomaly")
    @Operation(summary = "异常检测")
    @SaCheckPermission("ai:anomaly")
    public R<AnomalyDetectionVO> anomaly() {
        return R.ok(aiService.detectAnomaly());
    }
    
    @PostMapping("/chat")
    @Operation(summary = "自然语言查询")
    @SaCheckPermission("ai:chat")
    public R<String> chat(@RequestBody Map<String, String> request) {
        return R.ok(aiService.chat(request.get("message")));
    }
}
```

### 9.4 配置类

#### 9.4.1 Knife4jConfig - API文档配置

**路径**: `com.wms.web.config.Knife4jConfig`

```java
@Configuration
public class Knife4jConfig {
    
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("WMS 仓储管理系统 API")
                .description("基于 Spring Boot + Vue 3 的现代化仓储管理系统")
                .version("1.0.0"))
            .components(new Components()
                .addSecuritySchemes("Bearer",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

#### 9.4.2 CorsConfig - 跨域配置

**路径**: `com.wms.web.config.CorsConfig`

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns("*")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## 十、wms-frontend 前端模块

### 10.1 模块职责

wms-frontend 是系统的前端模块，基于 Vue 3 + TypeScript 构建，提供用户界面和交互功能。

### 10.2 技术栈详解

| 技术 | 版本 | 用途 |
|------|------|------|
| Vue | 3.5.32 | 前端框架 |
| TypeScript | 6.0 | 类型系统 |
| Vite | 8.0.8 | 构建工具 |
| Element Plus | 2.14.0 | UI组件库 |
| ECharts | 6.1.0 | 数据可视化 |
| Pinia | 3.0.4 | 状态管理 |
| Vue Router | 5.0.4 | 路由管理 |
| Axios | 1.16.1 | HTTP客户端 |

### 10.3 目录结构

```
wms-frontend/
├── src/
│   ├── main.ts                    # 应用入口
│   ├── App.vue                    # 根组件
│   ├── api/                       # API调用模块
│   │   ├── auth.ts                # 认证API
│   │   ├── warehouse.ts           # 仓库API
│   │   ├── product.ts             # 商品API
│   │   ├── inbound.ts             # 入库API
│   │   ├── outbound.ts            # 出库API
│   │   ├── inventory.ts           # 库存API
│   │   └── ai.ts                  # AI功能API
│   ├── views/                     # 页面视图
│   │   ├── login/                 # 登录页
│   │   ├── dashboard/             # 仪表盘
│   │   ├── warehouse/             # 仓库管理
│   │   ├── product/               # 商品管理
│   │   ├── inbound/               # 入库管理
│   │   ├── outbound/              # 出库管理
│   │   ├── inventory/             # 库存管理
│   │   ├── report/                # 报表分析
│   │   ├── ai/                    # AI智能
│   │   └── system/                # 系统管理
│   ├── components/                # 公共组件
│   │   ├── PageHeader.vue         # 页面头部
│   │   ├── StatusTag.vue          # 状态标签
│   │   └── SearchForm.vue         # 搜索表单
│   ├── layout/                    # 布局组件
│   │   └── index.vue              # 主布局
│   ├── router/                    # 路由配置
│   │   └── index.ts               # 路由定义
│   └── utils/                     # 工具函数
│       └── request.ts             # Axios封装
├── public/                        # 静态资源
└── vite.config.ts                 # Vite配置
```

### 10.4 核心组件详解

#### 10.4.1 request.ts - Axios封装

**路径**: `src/utils/request.ts`

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const service = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 30000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      
      // 401: Token过期
      if (res.code === 401) {
        localStorage.removeItem('token')
        router.push('/login')
      }
      
      return Promise.reject(new Error(res.message))
    }
    return res
  },
  error => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default service
```

#### 10.4.2 router/index.ts - 路由配置

**路径**: `src/router/index.ts`

```typescript
import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/index.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页仪表盘' }
      },
      {
        path: 'warehouse',
        name: 'Warehouse',
        component: () => import('@/views/warehouse/list.vue'),
        meta: { title: '仓库管理' }
      },
      // ... 其他路由
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
```

#### 10.4.3 layout/index.vue - 主布局

**路径**: `src/layout/index.vue`

```vue
<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'">
      <div class="logo">
        <span v-if="!isCollapse">WMS 仓储管理</span>
        <span v-else>WMS</span>
      </div>
      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>首页仪表盘</span>
        </el-menu-item>
        <!-- ... 其他菜单 -->
      </el-menu>
    </el-aside>
    
    <!-- 主内容区 -->
    <el-container>
      <el-header>
        <div class="header-left">
          <el-icon @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb>
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item>{{ route.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown>
            <span class="user-info">
              {{ userStore.userInfo?.nickname || '管理员' }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
```

### 10.5 页面视图详解

#### 10.5.1 登录页 (views/login/index.vue)

**功能**:
- 用户名/密码输入
- 表单校验
- 登录请求
- Token存储
- 路由跳转

#### 10.5.2 仪表盘 (views/dashboard/index.vue)

**功能**:
- 统计卡片（商品总数、仓库数量、今日入库、今日出库）
- 最近入库单列表
- 最近出库单列表
- 库存趋势图（ECharts）

#### 10.5.3 入库管理 (views/inbound/)

**list.vue - 入库单列表**:
- 搜索过滤
- 分页展示
- 状态标签（待审核/已审核/已入库/已取消）
- 操作按钮（审核/执行/取消）

**create.vue - 创建入库单**:
- 选择仓库
- 选择供应商
- 添加入库明细（商品、数量、价格、批次号）
- 提交创建

#### 10.5.4 AI智能 (views/ai/)

**forecast.vue - 需求预测**:
- 选择商品
- 展示预测结果（预测数量、置信度、说明、建议）
- 历史数据图表
- 预测趋势图表

**replenishment.vue - 智能补货**:
- 展示补货建议列表
- 显示紧急程度（高/中/低）
- 预计补货金额

**anomaly.vue - 异常检测**:
- 展示异常列表
- 异常等级（高/中/低）
- 异常类型（库存不足、已过期等）

**chat.vue - AI助手**:
- 对话式界面
- 自然语言查询
- 上下文理解

---

## 十一、数据库设计

### 11.1 数据表总览

系统共包含15张数据表，分为系统表和业务表两大类。

#### 系统表 (6张)

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| sys_user | 用户表 | id, username, password, nickname, status |
| sys_role | 角色表 | id, role_code, role_name |
| sys_user_role | 用户角色关联 | id, user_id, role_id |
| sys_menu | 菜单表 | id, menu_name, parent_id, perms, menu_type |
| sys_role_menu | 角色菜单关联 | id, role_id, menu_id |
| sys_operation_log | 操作日志 | id, user_id, operation, method, ip |

#### 业务表 (9张)

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| wms_warehouse | 仓库表 | id, warehouse_code, warehouse_name, status |
| wms_location | 库位表 | id, warehouse_id, location_code, capacity |
| wms_category | 商品分类表 | id, category_code, category_name, parent_id |
| wms_product | 商品表 | id, product_code, product_name, safety_stock |
| wms_supplier | 供应商表 | id, supplier_code, supplier_name |
| wms_customer | 客户表 | id, customer_code, customer_name |
| wms_inventory | 库存表 | id, warehouse_id, product_id, quantity |
| wms_inbound_order | 入库单表 | id, order_no, order_type, order_status |
| wms_inbound_order_item | 入库单明细 | id, order_id, product_id, quantity |
| wms_outbound_order | 出库单表 | id, order_no, order_type, order_status |
| wms_outbound_order_item | 出库单明细 | id, order_id, product_id, quantity |
| wms_inventory_log | 库存日志 | id, warehouse_id, product_id, change_type |

### 11.2 ER关系图

```
sys_user ─┬─ sys_user_role ─── sys_role
           │                      │
           │                sys_role_menu
           │                      │
           │                   sys_menu
                    
wms_warehouse ── wms_location
      │
      ├── wms_inventory ── wms_product ── wms_category
      │        │
      │   wms_inventory_log
      │
      ├── wms_inbound_order ── wms_inbound_order_item
      │                              │
      │                         wms_product
      │
      └── wms_outbound_order ── wms_outbound_order_item
                                     │
                                wms_product
```

### 11.3 索引设计

#### 唯一索引

| 表名 | 索引名 | 字段 | 说明 |
|------|--------|------|------|
| sys_user | uk_username | username | 用户名唯一 |
| sys_role | uk_role_code | role_code | 角色编码唯一 |
| wms_warehouse | uk_warehouse_code | warehouse_code | 仓库编码唯一 |
| wms_location | uk_location_code | location_code | 库位编码唯一 |
| wms_category | uk_category_code | category_code | 分类编码唯一 |
| wms_product | uk_product_code | product_code | 商品编码唯一 |
| wms_supplier | uk_supplier_code | supplier_code | 供应商编码唯一 |
| wms_customer | uk_customer_code | customer_code | 客户编码唯一 |
| wms_inbound_order | uk_order_no | order_no | 入库单号唯一 |
| wms_outbound_order | uk_order_no | order_no | 出库单号唯一 |

#### 普通索引

| 表名 | 索引名 | 字段 | 说明 |
|------|--------|------|------|
| wms_inventory | idx_warehouse_product | warehouse_id, product_id | 库仓商品联合索引 |
| wms_inventory | idx_batch_no | batch_no | 批次号索引 |
| wms_inbound_order_item | idx_order_id | order_id | 入库单ID索引 |
| wms_outbound_order_item | idx_order_id | order_id | 出库单ID索引 |
| wms_inventory_log | idx_warehouse_product | warehouse_id, product_id | 库仓商品联合索引 |
| wms_inventory_log | idx_order_no | order_no | 单号索引 |

### 11.4 初始数据

#### 用户数据

```sql
-- 管理员用户 (密码: 123456, BCrypt加密)
INSERT INTO sys_user (username, password, nickname, status) VALUES
('admin', '$2a$10$EqKcp1WFKVQISheBxnFOheYMKMeFSmVPfaAJMRPIlVtMVaKMr8yLq', '超级管理员', 1);

-- 角色
INSERT INTO sys_role (role_code, role_name, sort_order) VALUES
('SUPER_ADMIN', '超级管理员', 1),
('WAREHOUSE_ADMIN', '仓库管理员', 2),
('USER', '普通用户', 3);
```

#### 业务数据

```sql
-- 仓库
INSERT INTO wms_warehouse (warehouse_code, warehouse_name, warehouse_type, address, status) VALUES
('WH001', '主仓库', '常温仓', '北京市朝阳区xxx', 1),
('WH002', '保税仓库', '保税仓', '上海市浦东新区xxx', 1),
('WH003', '冷链仓库', '冷链仓', '广州市天河区xxx', 1);

-- 库位
INSERT INTO wms_location (warehouse_id, location_code, location_name, capacity, status) VALUES
(1, 'A-01-01', 'A区1层1号', 1000, 1),
(1, 'A-01-02', 'A区1层2号', 1000, 1),
(1, 'A-02-01', 'A区2层1号', 800, 1);

-- 商品分类
INSERT INTO wms_category (category_code, category_name, parent_id, sort_order) VALUES
('CG001', '电子产品', 0, 1),
('CG002', '食品饮料', 0, 2),
('CG001001', '手机', 1, 1),
('CG001002', '电脑', 1, 2),
('CG002001', '零食', 2, 1),
('CG002002', '饮料', 2, 2);

-- 商品
INSERT INTO wms_product (product_code, product_name, category_id, brand, unit, sale_price, safety_stock, min_stock, max_stock, status) VALUES
('P001', 'iPhone 15', 3, 'Apple', '台', 7999.00, 10, 5, 100, 1),
('P002', 'MacBook Pro', 4, 'Apple', '台', 14999.00, 5, 2, 50, 1),
('P003', '薯片', 5, '乐事', '包', 8.50, 100, 50, 1000, 1),
('P004', '可乐', 6, '可口可乐', '瓶', 3.00, 200, 100, 2000, 1);

-- 库存
INSERT INTO wms_inventory (warehouse_id, location_id, product_id, quantity, available_quantity, cost_price, expiry_date) VALUES
(1, 1, 1, 5, 5, 6999.00, NULL),
(1, 1, 2, 3, 3, 12999.00, NULL),
(1, 2, 3, 200, 200, 5.00, '2026-12-31'),
(1, 2, 4, 500, 500, 2.00, '2026-06-30'),
(2, 4, 1, 0, 0, 6999.00, NULL);  -- 库存为零的异常场景
```

---

## 十二、配置与部署

### 12.1 环境变量配置

**.env 文件**:

```bash
# 应用配置
APP_PORT=8080

# MySQL 数据库配置
DB_HOST=localhost
DB_PORT=3306
DB_NAME=wms
DB_USERNAME=root
DB_PASSWORD=your_password_here

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379

# Elasticsearch 配置
ES_HOST=localhost
ES_PORT=9200

# DeepSeek AI 配置
DEEPSEEK_API_KEY=your_api_key_here
```

### 12.2 application.yml 配置详解

```yaml
server:
  port: ${APP_PORT:8080}

spring:
  application:
    name: wms-system
  
  # 数据库配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:wms}?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
  
  # Redis配置
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      database: 0
      lettuce:
        pool:
          max-active: 8
          max-wait: -1ms
          max-idle: 8
          min-idle: 0
  
  # Elasticsearch配置
  elasticsearch:
    uris: http://${ES_HOST:localhost}:${ES_PORT:9200}
  
  # 禁用favicon解析
  web:
    resources:
      add-mappings: false
  
  # Jackson配置
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    time-zone: Asia/Shanghai
    default-property-inclusion: non_null
  
  # AI配置
  ai:
    openai:
      api-key: ${DEEPSEEK_API_KEY:your-api-key}
      base-url: https://api.deepseek.com
      chat:
        options:
          model: deepseek-v4-flash
          temperature: 0.7

# MyBatis-Plus配置
mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.wms.model.entity
  global-config:
    db-config:
      id-type: ASSIGN_ID
      logic-delete-field: isDeleted
      logic-delete-value: 1
      logic-not-delete-value: 0
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# Sa-Token配置
sa-token:
  token-name: Authorization
  token-prefix: Bearer
  timeout: 86400
  active-timeout: -1
  is-concurrent: true
  is-share: false
  token-style: uuid
  is-log: false

# Knife4j配置
knife4j:
  enable: true
  setting:
    language: zh_cn

# 日志配置
logging:
  level:
    root: INFO
    com.wms: DEBUG
    org.springframework.ai: DEBUG
  pattern:
    dateformat: yyyy-MM-dd HH:mm:ss.SSS
  file:
    name: ./logs/wms.log
```

### 12.3 Docker Compose 部署

**docker-compose.yml**:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: wms-mysql
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD:-root}
      MYSQL_DATABASE: ${DB_NAME:-wms}
      MYSQL_CHARSET: utf8mb4
      MYSQL_COLLATION: utf8mb4_unicode_ci
    ports:
      - "${DB_PORT:-3306}:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./sql:/docker-entrypoint-initdb.d
    command: --default-authentication-plugin=caching_sha2_password --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
    networks:
      - wms-network

  redis:
    image: redis:7-alpine
    container_name: wms-redis
    ports:
      - "${REDIS_PORT:-6379}:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes
    networks:
      - wms-network

  elasticsearch:
    image: elasticsearch:8.13.0
    container_name: wms-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
      - ES_JAVA_OPTS=-Xms512m -Xmx512m
    ports:
      - "${ES_PORT:-9200}:9200"
      - "9300:9300"
    volumes:
      - elasticsearch_data:/usr/share/elasticsearch/data
    networks:
      - wms-network

volumes:
  mysql_data:
  redis_data:
  elasticsearch_data:

networks:
  wms-network:
    driver: bridge
```

### 12.4 启动步骤

#### 12.4.1 本地开发环境

```bash
# 1. 克隆项目
git clone https://github.com/WuJX-0101/WSM_SpringBoot_AI.git
cd WSM_SpringBoot_AI

# 2. 配置环境变量
cp .env.example .env
# 编辑 .env 文件，填写实际配置

# 3. 启动基础设施
docker-compose up -d

# 4. 初始化数据库
mysql -u root -p < sql/init.sql

# 5. 启动后端
mvn clean package -DskipTests
java -jar wms-web/target/wms-web-1.0.0.jar

# 6. 启动前端
cd wms-frontend
npm install
npm run dev
```

#### 12.4.2 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost:5173 | Vue开发服务器 |
| 后端 | http://localhost:8080 | Spring Boot应用 |
| API文档 | http://localhost:8080/doc.html | Knife4j文档 |
| MySQL | localhost:3306 | 数据库 |
| Redis | localhost:6379 | 缓存 |
| Elasticsearch | localhost:9200 | 搜索引擎 |

### 12.5 常见问题

#### Q1: 启动报错 "Unknown database 'wms'"

**原因**: 数据库未创建

**解决**:
```bash
mysql -u root -p -e "CREATE DATABASE wms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p wms < sql/init.sql
```

#### Q2: AI功能报错 "Invalid API Key"

**原因**: DEEPSEEK_API_KEY 未配置或配置错误

**解决**:
```bash
# 检查 .env 文件
cat .env | grep DEEPSEEK_API_KEY

# 确保API Key正确
DEEPSEEK_API_KEY=sk-xxxxxxxxxxxxxxxx
```

#### Q3: 前端请求401错误

**原因**: Token过期或未携带Token

**解决**:
- 检查 localStorage 中是否存在 token
- 检查请求头是否正确携带 `Authorization: Bearer xxx`

---

## 附录

### A. API接口清单

详见 [API接口汇总表](#六api-接口汇总)

### B. 数据库表结构

详见 [数据库设计](#十一数据库设计)

### C. 配置参数说明

详见 [application.yml 配置详解](#122-applicationyml-配置详解)

### D. 常用命令

```bash
# 后端打包
mvn clean package -DskipTests

# 后端运行
java -jar wms-web/target/wms-web-1.0.0.jar

# 前端开发
cd wms-frontend && npm run dev

# 前端打包
cd wms-frontend && npm run build

# Docker启动
docker-compose up -d

# Docker停止
docker-compose down

# 查看日志
tail -f logs/wms.log
```

---

## 十三、优化记录

> 更新时间：2026-05-29

### 13.1 安全加固

| 优化项 | 说明 | 修改文件 |
|--------|------|----------|
| 登录限流 | Redis实现，5次失败锁定15分钟 | AuthServiceImpl.java |
| 注册防刷 | 同一用户名每小时最多3次 | AuthServiceImpl.java |
| Swagger禁用 | Profile控制，生产环境禁用 | Knife4jConfig.java |
| 环境变量 | API地址改为环境变量配置 | request.ts, .env.* |

### 13.2 单元测试

| 测试类 | 测试数量 | 覆盖功能 |
|--------|----------|----------|
| AuthServiceImplTest | 10个 | 登录/注册/权限 |
| InboundOrderServiceImplTest | 13个 | 入库单CRUD |
| OutboundOrderServiceImplTest | 11个 | 出库单CRUD |

**测试结果**：34个测试全部通过，通过率100%

### 13.3 前端优化

| 优化项 | 优化前 | 优化后 | 效果 |
|--------|--------|--------|------|
| ECharts打包 | 1,118 KB | 587 KB | 减少47% |
| 图标注册 | 全量300+ | 按需引入 | 减少加载 |
| 内存泄漏 | resize监听未移除 | 统一移除 | 修复泄漏 |
| 类型定义 | 无 | types/api.ts | 类型安全 |

### 13.4 权限控制

| 角色 | 权限数量 | 权限范围 |
|------|----------|----------|
| 超级管理员 | 77 | 所有权限 |
| 仓库管理员 | 72 | 业务权限 |
| 普通用户 | 35 | 只读权限 |

**权限标识**：50个按钮级别权限（inbound:create、inventory:adjust等）

### 13.5 性能优化

| 优化项 | 优化前 | 优化后 |
|--------|--------|--------|
| 统计接口 | 全量加载+Java Stream | SQL聚合查询 |
| 仪表盘 | 7次数据库查询 | 1次SQL聚合 |
| 排行榜 | 多次查询+Java分组 | SQL JOIN+GROUP BY |
| Redis缓存 | 无 | Cache Aside模式 |

**新增文件**：
- `OrderStatsMapper.java` - 统计查询Mapper
- `OrderStatsMapper.xml` - SQL聚合查询实现
- `CacheConfig.java` - 缓存配置类

**Cache Aside 缓存模式**：

| 操作 | 逻辑 | 实现 |
|------|------|------|
| 读取 | 先读缓存，未命中则查数据库并写入缓存 | `@Cacheable` |
| 写入 | 先更新数据库，再删除缓存 | `@CacheEvict` |

**缓存配置**：

| 缓存项 | TTL | 说明 |
|--------|-----|------|
| 仪表盘统计 | 5分钟 | `dashboard:stats` |
| 订单统计 | 5分钟 | `orderStats:*` |
| 仓库列表 | 10分钟 | `warehouse:all` |
| 供应商列表 | 10分钟 | `supplier:all` |
| 客户列表 | 10分钟 | `customer:all` |
| 分类树 | 10分钟 | `category:tree` |

**缓存清除时机**：
- 执行入库后 → 清除仪表盘缓存
- 执行出库后 → 清除仪表盘缓存
- 创建/更新/删除仓库 → 清除仓库缓存
- 创建/更新/删除供应商 → 清除供应商缓存
- 创建/更新/删除客户 → 清除客户缓存
- 创建/更新/删除分类 → 清除分类缓存

**不缓存的数据**：
- 库存数据（变化频繁，需实时准确）
- 库存报表（数据量小，直接查数据库）
- 分页查询（参数组合多，缓存key难设计）

### 13.6 SQL脚本整合

- 将 `permission_init.sql` 整合到 `init.sql`
- 删除独立的 `permission_init.sql` 文件
- `init.sql` 现包含完整权限体系（77个菜单+50个按钮权限）

---

**文档版本**: 1.3.0
**更新日期**: 2026-05-29
**维护者**: WuJX
