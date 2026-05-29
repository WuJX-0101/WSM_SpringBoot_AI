-- ==================== 权限控制完善脚本 ====================
-- 此脚本用于添加按钮级别的权限标识

USE wms;

-- ==================== 1. 添加按钮级别权限菜单 ====================

-- 入库管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(411, 4, '创建入库', 3, NULL, NULL, 'inbound:create', NULL, 1, 1),
(412, 4, '审核入库', 3, NULL, NULL, 'inbound:audit', NULL, 2, 1),
(413, 4, '执行入库', 3, NULL, NULL, 'inbound:execute', NULL, 3, 1),
(414, 4, '取消入库', 3, NULL, NULL, 'inbound:cancel', NULL, 4, 1),
(415, 4, '查看入库', 3, NULL, NULL, 'inbound:view', NULL, 5, 1),
(416, 4, '入库列表', 3, NULL, NULL, 'inbound:list', NULL, 6, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 出库管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(511, 5, '创建出库', 3, NULL, NULL, 'outbound:create', NULL, 1, 1),
(512, 5, '审核出库', 3, NULL, NULL, 'outbound:audit', NULL, 2, 1),
(513, 5, '执行出库', 3, NULL, NULL, 'outbound:execute', NULL, 3, 1),
(514, 5, '取消出库', 3, NULL, NULL, 'outbound:cancel', NULL, 4, 1),
(515, 5, '查看出库', 3, NULL, NULL, 'outbound:view', NULL, 5, 1),
(516, 5, '出库列表', 3, NULL, NULL, 'outbound:list', NULL, 6, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 库存管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(611, 6, '查看库存', 3, NULL, NULL, 'inventory:view', NULL, 1, 1),
(612, 6, '库存列表', 3, NULL, NULL, 'inventory:list', NULL, 2, 1),
(613, 6, '库存调整', 3, NULL, NULL, 'inventory:adjust', NULL, 3, 1),
(614, 6, '库存日志', 3, NULL, NULL, 'inventory:log', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 仓库管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(111, 1, '查看仓库', 3, NULL, NULL, 'warehouse:view', NULL, 1, 1),
(112, 1, '创建仓库', 3, NULL, NULL, 'warehouse:create', NULL, 2, 1),
(113, 1, '编辑仓库', 3, NULL, NULL, 'warehouse:edit', NULL, 3, 1),
(114, 1, '删除仓库', 3, NULL, NULL, 'warehouse:delete', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 库位管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(121, 1, '查看库位', 3, NULL, NULL, 'location:view', NULL, 1, 1),
(122, 1, '创建库位', 3, NULL, NULL, 'location:create', NULL, 2, 1),
(123, 1, '编辑库位', 3, NULL, NULL, 'location:edit', NULL, 3, 1),
(124, 1, '删除库位', 3, NULL, NULL, 'location:delete', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 商品管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(211, 2, '查看商品', 3, NULL, NULL, 'product:view', NULL, 1, 1),
(212, 2, '创建商品', 3, NULL, NULL, 'product:create', NULL, 2, 1),
(213, 2, '编辑商品', 3, NULL, NULL, 'product:edit', NULL, 3, 1),
(214, 2, '删除商品', 3, NULL, NULL, 'product:delete', NULL, 4, 1),
(221, 2, '查看分类', 3, NULL, NULL, 'category:view', NULL, 5, 1),
(222, 2, '创建分类', 3, NULL, NULL, 'category:create', NULL, 6, 1),
(223, 2, '编辑分类', 3, NULL, NULL, 'category:edit', NULL, 7, 1),
(224, 2, '删除分类', 3, NULL, NULL, 'category:delete', NULL, 8, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 供应商管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(311, 3, '查看供应商', 3, NULL, NULL, 'supplier:view', NULL, 1, 1),
(312, 3, '创建供应商', 3, NULL, NULL, 'supplier:create', NULL, 2, 1),
(313, 3, '编辑供应商', 3, NULL, NULL, 'supplier:edit', NULL, 3, 1),
(314, 3, '删除供应商', 3, NULL, NULL, 'supplier:delete', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 客户管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(321, 3, '查看客户', 3, NULL, NULL, 'customer:view', NULL, 1, 1),
(322, 3, '创建客户', 3, NULL, NULL, 'customer:create', NULL, 2, 1),
(323, 3, '编辑客户', 3, NULL, NULL, 'customer:edit', NULL, 3, 1),
(324, 3, '删除客户', 3, NULL, NULL, 'customer:delete', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 系统管理按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(911, 9, '查看员工', 3, NULL, NULL, 'system:user:view', NULL, 1, 1),
(912, 9, '创建员工', 3, NULL, NULL, 'system:user:create', NULL, 2, 1),
(913, 9, '编辑员工', 3, NULL, NULL, 'system:user:edit', NULL, 3, 1),
(914, 9, '删除员工', 3, NULL, NULL, 'system:user:delete', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- AI功能按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(811, 8, '需求预测', 3, NULL, NULL, 'ai:forecast', NULL, 1, 1),
(812, 8, '智能补货', 3, NULL, NULL, 'ai:replenishment', NULL, 2, 1),
(813, 8, '异常检测', 3, NULL, NULL, 'ai:anomaly', NULL, 3, 1),
(814, 8, 'AI对话', 3, NULL, NULL, 'ai:chat', NULL, 4, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- 报表分析按钮权限
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order, status) VALUES
(711, 7, '订单统计', 3, NULL, NULL, 'report:order', NULL, 1, 1),
(712, 7, '库存报表', 3, NULL, NULL, 'report:inventory', NULL, 2, 1)
ON DUPLICATE KEY UPDATE perms = VALUES(perms);

-- ==================== 2. 分配权限给超级管理员角色 ====================

-- 获取超级管理员角色ID (role_code = 'SUPER_ADMIN')
-- 删除原有权限分配，重新分配所有权限
DELETE FROM sys_role_menu WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN' LIMIT 1);

-- 为超级管理员分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
    (SELECT id FROM sys_role WHERE role_code = 'SUPER_ADMIN' LIMIT 1),
    id
FROM sys_menu
WHERE is_deleted = 0 AND status = 1;

-- ==================== 3. 分配权限给仓库管理员角色 ====================

-- 获取仓库管理员角色ID (role_code = 'WAREHOUSE_ADMIN')
DELETE FROM sys_role_menu WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'WAREHOUSE_ADMIN' LIMIT 1);

-- 为仓库管理员分配业务权限（不包括系统管理）
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
    (SELECT id FROM sys_role WHERE role_code = 'WAREHOUSE_ADMIN' LIMIT 1),
    id
FROM sys_menu
WHERE is_deleted = 0 AND status = 1
AND parent_id NOT IN (9)  -- 排除系统管理
AND id NOT IN (SELECT id FROM sys_menu WHERE parent_id = 9);  -- 排除系统管理子菜单

-- ==================== 4. 分配权限给普通用户角色 ====================

-- 获取普通用户角色ID (role_code = 'USER')
DELETE FROM sys_role_menu WHERE role_id = (SELECT id FROM sys_role WHERE role_code = 'USER' LIMIT 1);

-- 为普通用户分配查看权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT
    (SELECT id FROM sys_role WHERE role_code = 'USER' LIMIT 1),
    id
FROM sys_menu
WHERE is_deleted = 0 AND status = 1
AND (
    perms LIKE '%:view'
    OR perms LIKE '%:list'
    OR parent_id IN (7, 8)  -- 报表和AI只读
);

-- ==================== 完成 ====================
SELECT '权限配置完成！' AS message;
SELECT COUNT(*) AS total_permissions FROM sys_menu WHERE is_deleted = 0 AND status = 1 AND menu_type = 3;
