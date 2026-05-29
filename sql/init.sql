-- WMS 仓储管理系统 - 数据库初始化脚本
-- 包含: 建表、初始数据、菜单权限配置

-- 创建数据库
CREATE DATABASE IF NOT EXISTS wms DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wms;

-- ==================== 建表语句 ====================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT NOT NULL COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT NOT NULL COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL COMMENT '角色编码',
    description VARCHAR(255) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 菜单表
CREATE TABLE IF NOT EXISTS sys_menu (
    id BIGINT NOT NULL COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    menu_name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    menu_type TINYINT DEFAULT 1 COMMENT '菜单类型（1：目录，2：菜单，3：按钮）',
    path VARCHAR(200) COMMENT '路由地址',
    component VARCHAR(200) COMMENT '组件路径',
    perms VARCHAR(100) COMMENT '权限标识',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单表';

-- 角色菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色菜单关联表';

-- 仓库表
CREATE TABLE IF NOT EXISTS wms_warehouse (
    id BIGINT NOT NULL COMMENT '主键ID',
    warehouse_code VARCHAR(50) NOT NULL COMMENT '仓库编码',
    warehouse_name VARCHAR(100) NOT NULL COMMENT '仓库名称',
    warehouse_type TINYINT DEFAULT 1 COMMENT '仓库类型（1：普通仓库，2：保税仓库，3：冷链仓库）',
    address VARCHAR(255) COMMENT '仓库地址',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    area DECIMAL(10,2) COMMENT '仓库面积（平方米）',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_warehouse_code (warehouse_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='仓库表';

-- 库位表
CREATE TABLE IF NOT EXISTS wms_location (
    id BIGINT NOT NULL COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_code VARCHAR(50) NOT NULL COMMENT '库位编码',
    location_name VARCHAR(100) COMMENT '库位名称',
    location_type TINYINT DEFAULT 1 COMMENT '库位类型（1：存储位，2：拣货位，3：暂存位）',
    area VARCHAR(50) COMMENT '区域',
    shelf VARCHAR(50) COMMENT '货架',
    layer INT COMMENT '层',
    position INT COMMENT '位置',
    capacity DECIMAL(10,2) COMMENT '容量',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_location_code (location_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库位表';

-- 商品分类表
CREATE TABLE IF NOT EXISTS wms_category (
    id BIGINT NOT NULL COMMENT '主键ID',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    category_code VARCHAR(50) NOT NULL COMMENT '分类编码',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_category_code (category_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- 商品表
CREATE TABLE IF NOT EXISTS wms_product (
    id BIGINT NOT NULL COMMENT '主键ID',
    product_code VARCHAR(50) NOT NULL COMMENT '商品编码',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT COMMENT '分类ID',
    brand VARCHAR(100) COMMENT '品牌',
    specification VARCHAR(200) COMMENT '规格',
    unit VARCHAR(20) COMMENT '单位',
    barcode VARCHAR(50) COMMENT '条码',
    weight DECIMAL(10,3) COMMENT '重量（kg）',
    volume DECIMAL(10,6) COMMENT '体积（立方米）',
    purchase_price DECIMAL(12,2) COMMENT '采购价',
    sale_price DECIMAL(12,2) COMMENT '销售价',
    safety_stock INT DEFAULT 0 COMMENT '安全库存',
    min_stock INT DEFAULT 0 COMMENT '最小库存',
    max_stock INT DEFAULT 0 COMMENT '最大库存',
    image VARCHAR(255) COMMENT '商品图片',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- 供应商表
CREATE TABLE IF NOT EXISTS wms_supplier (
    id BIGINT NOT NULL COMMENT '主键ID',
    supplier_code VARCHAR(50) NOT NULL COMMENT '供应商编码',
    supplier_name VARCHAR(200) NOT NULL COMMENT '供应商名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    bank_name VARCHAR(100) COMMENT '开户行',
    bank_account VARCHAR(50) COMMENT '银行账号',
    tax_number VARCHAR(50) COMMENT '税号',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_supplier_code (supplier_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商表';

-- 客户表
CREATE TABLE IF NOT EXISTS wms_customer (
    id BIGINT NOT NULL COMMENT '主键ID',
    customer_code VARCHAR(50) NOT NULL COMMENT '客户编码',
    customer_name VARCHAR(200) NOT NULL COMMENT '客户名称',
    contact_person VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    customer_type TINYINT DEFAULT 1 COMMENT '客户类型（1：普通客户，2：VIP客户，3：企业客户）',
    status TINYINT DEFAULT 1 COMMENT '状态（0：禁用，1：启用）',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_customer_code (customer_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户表';

-- 库存表
CREATE TABLE IF NOT EXISTS wms_inventory (
    id BIGINT NOT NULL COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    location_id BIGINT COMMENT '库位ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    batch_no VARCHAR(50) COMMENT '批次号',
    quantity INT DEFAULT 0 COMMENT '库存数量',
    locked_quantity INT DEFAULT 0 COMMENT '锁定数量',
    available_quantity INT DEFAULT 0 COMMENT '可用数量',
    cost_price DECIMAL(12,2) COMMENT '成本价',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_warehouse_product (warehouse_id, product_id),
    KEY idx_batch_no (batch_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存表';

-- 入库单表
CREATE TABLE IF NOT EXISTS wms_inbound_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '入库单号',
    order_type TINYINT NOT NULL COMMENT '入库类型（1：采购入库，2：退货入库，3：调拨入库）',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    supplier_id BIGINT COMMENT '供应商ID',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态（0：待审核，1：已审核，2：已入库，3：已取消）',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    inbound_time DATETIME COMMENT '入库时间',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单表';

-- 入库单明细表
CREATE TABLE IF NOT EXISTS wms_inbound_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '入库单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    location_id BIGINT COMMENT '库位ID',
    batch_no VARCHAR(50) COMMENT '批次号',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(12,2) COMMENT '金额',
    production_date DATE COMMENT '生产日期',
    expiry_date DATE COMMENT '过期日期',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入库单明细表';

-- 出库单表
CREATE TABLE IF NOT EXISTS wms_outbound_order (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '出库单号',
    order_type TINYINT NOT NULL COMMENT '出库类型（1：销售出库，2：退货出库，3：调拨出库）',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    customer_id BIGINT COMMENT '客户ID',
    order_status TINYINT DEFAULT 0 COMMENT '订单状态（0：待审核，1：已审核，2：已出库，3：已取消）',
    total_quantity INT DEFAULT 0 COMMENT '总数量',
    total_amount DECIMAL(12,2) DEFAULT 0 COMMENT '总金额',
    outbound_time DATETIME COMMENT '出库时间',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单表';

-- 出库单明细表
CREATE TABLE IF NOT EXISTS wms_outbound_order_item (
    id BIGINT NOT NULL COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '出库单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    location_id BIGINT COMMENT '库位ID',
    batch_no VARCHAR(50) COMMENT '批次号',
    quantity INT NOT NULL COMMENT '数量',
    price DECIMAL(12,2) COMMENT '单价',
    amount DECIMAL(12,2) COMMENT '金额',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    gmt_modified DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    is_deleted TINYINT DEFAULT 0 COMMENT '是否删除（0：未删除，1：已删除）',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='出库单明细表';

-- 库存变更日志表
CREATE TABLE IF NOT EXISTS wms_inventory_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    warehouse_id BIGINT NOT NULL COMMENT '仓库ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    batch_no VARCHAR(50) COMMENT '批次号',
    change_type TINYINT NOT NULL COMMENT '变更类型（1：入库，2：出库，3：盘点，4：调整）',
    change_quantity INT NOT NULL COMMENT '变更数量',
    before_quantity INT COMMENT '变更前数量',
    after_quantity INT COMMENT '变更后数量',
    order_no VARCHAR(50) COMMENT '关联单号',
    remark VARCHAR(500) COMMENT '备注',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    PRIMARY KEY (id),
    KEY idx_warehouse_product (warehouse_id, product_id),
    KEY idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存变更日志表';

-- 操作日志表
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    module VARCHAR(50) COMMENT '模块',
    operation VARCHAR(50) COMMENT '操作',
    method VARCHAR(200) COMMENT '方法',
    request_url VARCHAR(500) COMMENT '请求URL',
    request_method VARCHAR(10) COMMENT '请求方式',
    request_params TEXT COMMENT '请求参数',
    response_result TEXT COMMENT '响应结果',
    ip VARCHAR(50) COMMENT 'IP地址',
    user_agent VARCHAR(500) COMMENT '用户代理',
    user_id BIGINT COMMENT '操作人ID',
    username VARCHAR(50) COMMENT '操作人',
    duration BIGINT COMMENT '耗时（毫秒）',
    status TINYINT DEFAULT 1 COMMENT '状态（0：失败，1：成功）',
    error_msg TEXT COMMENT '错误信息',
    gmt_create DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by VARCHAR(50) COMMENT '创建人',
    update_by VARCHAR(50) COMMENT '更新人',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_gmt_create (gmt_create)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- ==================== 初始数据 ====================

-- 初始化管理员用户 (密码: 123456)
INSERT INTO sys_user (id, username, password, nickname, status) VALUES
(1, 'admin', '$2a$10$M2iAYXuQUJjScYJF6q232.T06jSxHKM4SvmmJ5UZNr0cW.K3Cjdsy', '系统管理员', 1);

-- 初始化角色
INSERT INTO sys_role (id, role_name, role_code, description, status) VALUES
(1, '超级管理员', 'SUPER_ADMIN', '超级管理员，拥有所有权限', 1),
(2, '仓库管理员', 'WAREHOUSE_ADMIN', '仓库管理员，负责仓库日常管理', 1),
(3, '普通用户', 'USER', '普通用户，基础操作权限', 1);

-- 初始化用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id) VALUES
(1, 1, 1);

-- ==================== 菜单权限初始化 ====================

-- 一级目录
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(1, 0, '仓库管理', 1, '/warehouse', NULL, NULL, 'House', 1, 1),
(2, 0, '商品管理', 1, '/product', NULL, NULL, 'Goods', 2, 1),
(3, 0, '基础数据', 1, '/base', NULL, NULL, 'User', 3, 1),
(4, 0, '入库管理', 1, '/inbound', NULL, NULL, 'Download', 4, 1),
(5, 0, '出库管理', 1, '/outbound', NULL, NULL, 'Upload', 5, 1),
(6, 0, '库存管理', 1, '/inventory', NULL, NULL, 'Box', 6, 1),
(7, 0, '报表分析', 1, '/report', NULL, NULL, 'DataAnalysis', 7, 1),
(8, 0, 'AI智能', 1, '/ai', NULL, NULL, 'MagicStick', 8, 1),
(9, 0, '系统管理', 1, '/system', NULL, NULL, 'Setting', 9, 1);

-- 仓库管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(101, 1, '仓库列表', 2, '/warehouse/list', 'warehouse/list', 'warehouse:view', NULL, 1, 1),
(102, 1, '库位管理', 2, '/location/list', 'location/list', 'location:view', NULL, 2, 1),
(111, 1, '查看仓库', 3, NULL, NULL, 'warehouse:view', NULL, 1, 1),
(112, 1, '创建仓库', 3, NULL, NULL, 'warehouse:create', NULL, 2, 1),
(113, 1, '编辑仓库', 3, NULL, NULL, 'warehouse:edit', NULL, 3, 1),
(114, 1, '删除仓库', 3, NULL, NULL, 'warehouse:delete', NULL, 4, 1),
(121, 1, '查看库位', 3, NULL, NULL, 'location:view', NULL, 5, 1),
(122, 1, '创建库位', 3, NULL, NULL, 'location:create', NULL, 6, 1),
(123, 1, '编辑库位', 3, NULL, NULL, 'location:edit', NULL, 7, 1),
(124, 1, '删除库位', 3, NULL, NULL, 'location:delete', NULL, 8, 1);

-- 商品管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(201, 2, '商品分类', 2, '/category/list', 'category/list', 'category:view', NULL, 1, 1),
(202, 2, '商品列表', 2, '/product/list', 'product/list', 'product:view', NULL, 2, 1),
(211, 2, '查看商品', 3, NULL, NULL, 'product:view', NULL, 1, 1),
(212, 2, '创建商品', 3, NULL, NULL, 'product:create', NULL, 2, 1),
(213, 2, '编辑商品', 3, NULL, NULL, 'product:edit', NULL, 3, 1),
(214, 2, '删除商品', 3, NULL, NULL, 'product:delete', NULL, 4, 1),
(221, 2, '查看分类', 3, NULL, NULL, 'category:view', NULL, 5, 1),
(222, 2, '创建分类', 3, NULL, NULL, 'category:create', NULL, 6, 1),
(223, 2, '编辑分类', 3, NULL, NULL, 'category:edit', NULL, 7, 1),
(224, 2, '删除分类', 3, NULL, NULL, 'category:delete', NULL, 8, 1);

-- 基础数据菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(301, 3, '供应商管理', 2, '/supplier/list', 'supplier/list', 'supplier:view', NULL, 1, 1),
(302, 3, '客户管理', 2, '/customer/list', 'customer/list', 'customer:view', NULL, 2, 1),
(311, 3, '查看供应商', 3, NULL, NULL, 'supplier:view', NULL, 1, 1),
(312, 3, '创建供应商', 3, NULL, NULL, 'supplier:create', NULL, 2, 1),
(313, 3, '编辑供应商', 3, NULL, NULL, 'supplier:edit', NULL, 3, 1),
(314, 3, '删除供应商', 3, NULL, NULL, 'supplier:delete', NULL, 4, 1),
(321, 3, '查看客户', 3, NULL, NULL, 'customer:view', NULL, 5, 1),
(322, 3, '创建客户', 3, NULL, NULL, 'customer:create', NULL, 6, 1),
(323, 3, '编辑客户', 3, NULL, NULL, 'customer:edit', NULL, 7, 1),
(324, 3, '删除客户', 3, NULL, NULL, 'customer:delete', NULL, 8, 1);

-- 入库管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(401, 4, '入库单列表', 2, '/inbound/list', 'inbound/list', 'inbound:list', NULL, 1, 1),
(402, 4, '创建入库单', 2, '/inbound/create', 'inbound/create', 'inbound:create', NULL, 2, 1),
(411, 4, '创建入库', 3, NULL, NULL, 'inbound:create', NULL, 1, 1),
(412, 4, '审核入库', 3, NULL, NULL, 'inbound:audit', NULL, 2, 1),
(413, 4, '执行入库', 3, NULL, NULL, 'inbound:execute', NULL, 3, 1),
(414, 4, '取消入库', 3, NULL, NULL, 'inbound:cancel', NULL, 4, 1),
(415, 4, '查看入库', 3, NULL, NULL, 'inbound:view', NULL, 5, 1),
(416, 4, '入库列表', 3, NULL, NULL, 'inbound:list', NULL, 6, 1);

-- 出库管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(501, 5, '出库单列表', 2, '/outbound/list', 'outbound/list', 'outbound:list', NULL, 1, 1),
(502, 5, '创建出库单', 2, '/outbound/create', 'outbound/create', 'outbound:create', NULL, 2, 1),
(511, 5, '创建出库', 3, NULL, NULL, 'outbound:create', NULL, 1, 1),
(512, 5, '审核出库', 3, NULL, NULL, 'outbound:audit', NULL, 2, 1),
(513, 5, '执行出库', 3, NULL, NULL, 'outbound:execute', NULL, 3, 1),
(514, 5, '取消出库', 3, NULL, NULL, 'outbound:cancel', NULL, 4, 1),
(515, 5, '查看出库', 3, NULL, NULL, 'outbound:view', NULL, 5, 1),
(516, 5, '出库列表', 3, NULL, NULL, 'outbound:list', NULL, 6, 1);

-- 库存管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(601, 6, '库存列表', 2, '/inventory/list', 'inventory/list', 'inventory:list', NULL, 1, 1),
(602, 6, '库存日志', 2, '/inventory/log', 'inventory/log', 'inventory:log', NULL, 2, 1),
(611, 6, '查看库存', 3, NULL, NULL, 'inventory:view', NULL, 1, 1),
(612, 6, '库存列表', 3, NULL, NULL, 'inventory:list', NULL, 2, 1),
(613, 6, '库存调整', 3, NULL, NULL, 'inventory:adjust', NULL, 3, 1),
(614, 6, '库存日志', 3, NULL, NULL, 'inventory:log', NULL, 4, 1);

-- 报表分析菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(701, 7, '订单统计', 2, '/report/order', 'report/order', 'report:order', NULL, 1, 1),
(702, 7, '库存报表', 2, '/report/inventory', 'report/inventory', 'report:inventory', NULL, 2, 1),
(711, 7, '订单统计', 3, NULL, NULL, 'report:order', NULL, 1, 1),
(712, 7, '库存报表', 3, NULL, NULL, 'report:inventory', NULL, 2, 1);

-- AI智能菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(801, 8, '需求预测', 2, '/ai/forecast', 'ai/forecast', 'ai:forecast', NULL, 1, 1),
(802, 8, '智能补货', 2, '/ai/replenishment', 'ai/replenishment', 'ai:replenishment', NULL, 2, 1),
(803, 8, '异常检测', 2, '/ai/anomaly', 'ai/anomaly', 'ai:anomaly', NULL, 3, 1),
(811, 8, '需求预测', 3, NULL, NULL, 'ai:forecast', NULL, 1, 1),
(812, 8, '智能补货', 3, NULL, NULL, 'ai:replenishment', NULL, 2, 1),
(813, 8, '异常检测', 3, NULL, NULL, 'ai:anomaly', NULL, 3, 1),
(814, 8, 'AI对话', 3, NULL, NULL, 'ai:chat', NULL, 4, 1);

-- 系统管理菜单 + 按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(901, 9, '员工管理', 2, '/system/employee', 'system/employee/list', 'system:user:view', NULL, 1, 1),
(911, 9, '查看员工', 3, NULL, NULL, 'system:user:view', NULL, 1, 1),
(912, 9, '创建员工', 3, NULL, NULL, 'system:user:create', NULL, 2, 1),
(913, 9, '编辑员工', 3, NULL, NULL, 'system:user:edit', NULL, 3, 1),
(914, 9, '删除员工', 3, NULL, NULL, 'system:user:delete', NULL, 4, 1);

-- ==================== 角色权限分配 ====================

-- 超级管理员：所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu WHERE is_deleted = 0 AND status = 1;

-- 仓库管理员：业务操作权限（不包括系统管理）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 2, id FROM sys_menu
WHERE is_deleted = 0 AND status = 1
AND parent_id NOT IN (9)
AND id NOT IN (SELECT id FROM sys_menu WHERE parent_id = 9);

-- 普通用户：只读权限（view/list）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 3, id FROM sys_menu
WHERE is_deleted = 0 AND status = 1
AND (
    perms LIKE '%:view'
    OR perms LIKE '%:list'
    OR parent_id IN (7, 8)
);

-- ==================== 初始化完成 ====================
SELECT 'WMS 数据库初始化完成！' AS message;
SELECT '默认账号: admin / 123456' AS account;
SELECT COUNT(*) AS total_menus FROM sys_menu WHERE is_deleted = 0;
SELECT COUNT(*) AS total_permissions FROM sys_menu WHERE is_deleted = 0 AND menu_type = 3;
