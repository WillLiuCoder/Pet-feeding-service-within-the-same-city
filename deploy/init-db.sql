-- 客户信息表 [6,8](@ref)
CREATE TABLE IF NOT EXISTS customer_info (
                                             user_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                             name VARCHAR(50) NOT NULL,
    address VARCHAR(255) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 宠物档案表
CREATE TABLE IF NOT EXISTS pet_profile (
                                           pet_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                           user_id INT UNSIGNED NOT NULL,
                                           name VARCHAR(50) NOT NULL,
    type ENUM('cat', 'dog') NOT NULL,
    breed VARCHAR(50),
    age TINYINT UNSIGNED,
    health_notes TEXT,
    photo_url VARCHAR(255),
    FOREIGN KEY (user_id) REFERENCES customer_info(user_id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 服务人员表
CREATE TABLE IF NOT EXISTS caregiver_info (
                                              caregiver_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                              real_name VARCHAR(50) NOT NULL,
    id_card CHAR(18) UNIQUE NOT NULL,
    service_zone ENUM('Haidian', 'Chaoyang', 'Fengtai', 'Xicheng') NOT NULL,
    certification VARCHAR(100),
    avg_rating DECIMAL(3,2) DEFAULT 0.00
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 服务订单表 [7](@ref)
CREATE TABLE IF NOT EXISTS pet_service_order (
                                                 order_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                                 user_id INT UNSIGNED NOT NULL,
                                                 pet_id INT UNSIGNED NOT NULL,
                                                 service_type ENUM('喂养', '遛狗') NOT NULL,
    address VARCHAR(255) NOT NULL,
    scheduled_time DATETIME NOT NULL,
    status TINYINT UNSIGNED DEFAULT 0 COMMENT '0:待支付, 1:已派单, 2:服务中, 3:已完成, 4:已评价',
    special_notes TEXT,
    FOREIGN KEY (user_id) REFERENCES customer_info(user_id),
    FOREIGN KEY (pet_id) REFERENCES pet_profile(pet_id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 服务过程表
CREATE TABLE IF NOT EXISTS service_log (
                                           log_id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                           order_id INT UNSIGNED NOT NULL,
                                           check_in_time DATETIME,
                                           checkout_time DATETIME,
                                           feed_photo VARCHAR(255),
    walk_route_map TEXT COMMENT '存储遛狗GPS轨迹的JSON或路径',
    FOREIGN KEY (order_id) REFERENCES pet_service_order(order_id) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;