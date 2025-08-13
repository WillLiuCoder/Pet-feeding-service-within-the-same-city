-- 创建管理员用户表
CREATE TABLE IF NOT EXISTS `admin_user` (
  `admin_id` bigint NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `real_name` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar_url` varchar(255) DEFAULT NULL COMMENT '头像URL',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`admin_id`),
  UNIQUE KEY `uk_username` (`username`),
  UNIQUE KEY `uk_email` (`email`),
  UNIQUE KEY `uk_phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员用户表';

-- 创建管理员角色表
CREATE TABLE IF NOT EXISTS `admin_role` (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(50) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(200) DEFAULT NULL COMMENT '角色描述',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`role_id`),
  UNIQUE KEY `uk_role_name` (`role_name`),
  UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员角色表';

-- 创建管理员权限表
CREATE TABLE IF NOT EXISTS `admin_permission` (
  `permission_id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `permission_type` varchar(20) DEFAULT NULL COMMENT '权限类型',
  `resource_path` varchar(200) DEFAULT NULL COMMENT '资源路径',
  `description` varchar(200) DEFAULT NULL COMMENT '权限描述',
  `status` varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`permission_id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理员权限表';

-- 创建用户角色关联表
CREATE TABLE IF NOT EXISTS `admin_user_role` (
  `admin_id` bigint NOT NULL COMMENT '管理员ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`admin_id`,`role_id`),
  KEY `fk_user_role_role_id` (`role_id`),
  CONSTRAINT `fk_user_role_admin_id` FOREIGN KEY (`admin_id`) REFERENCES `admin_user` (`admin_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_role_role_id` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 创建角色权限关联表
CREATE TABLE IF NOT EXISTS `admin_role_permission` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  PRIMARY KEY (`role_id`,`permission_id`),
  KEY `fk_role_permission_permission_id` (`permission_id`),
  CONSTRAINT `fk_role_permission_role_id` FOREIGN KEY (`role_id`) REFERENCES `admin_role` (`role_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_role_permission_permission_id` FOREIGN KEY (`permission_id`) REFERENCES `admin_permission` (`permission_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 插入默认管理员用户 (密码: admin123)
INSERT INTO `admin_user` (`username`, `password`, `real_name`, `email`, `status`) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', '系统管理员', 'admin@example.com', 'ACTIVE');

-- 插入默认角色
INSERT INTO `admin_role` (`role_name`, `role_code`, `description`) VALUES 
('超级管理员', 'SUPER_ADMIN', '系统超级管理员，拥有所有权限'),
('普通管理员', 'ADMIN', '普通管理员，拥有基本管理权限');

-- 插入默认权限
INSERT INTO `admin_permission` (`permission_name`, `permission_code`, `permission_type`, `resource_path`, `description`) VALUES 
('用户管理', 'USER_MANAGE', 'MENU', '/user', '用户管理菜单'),
('角色管理', 'ROLE_MANAGE', 'MENU', '/role', '角色管理菜单'),
('权限管理', 'PERMISSION_MANAGE', 'MENU', '/permission', '权限管理菜单'),
('服务人员管理', 'CAREGIVER_MANAGE', 'MENU', '/caregiver', '服务人员管理菜单');

-- 关联超级管理员角色和权限
INSERT INTO `admin_user_role` (`admin_id`, `role_id`) VALUES (1, 1);
INSERT INTO `admin_role_permission` (`role_id`, `permission_id`) VALUES (1, 1), (1, 2), (1, 3), (1, 4);
