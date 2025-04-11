create database peacemall;
use peacemall;
-- 用户表
CREATE TABLE users (
    user_id BIGINT PRIMARY KEY COMMENT '用户唯一ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    nickname VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
    email VARCHAR(100) UNIQUE COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    phone_number VARCHAR(20) UNIQUE COMMENT '手机号',
    role ENUM('ADMIN', 'MERCHANT', 'USER') DEFAULT 'USER' COMMENT '用户角色',
    status ENUM('ACTIVE', 'LOCKED', 'PENDING','CLOSED') DEFAULT 'PENDING' COMMENT '账号状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login TIMESTAMP NULL COMMENT '最后登录时间',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    signature TEXT COMMENT '个性签名'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商店表
CREATE TABLE shops (
    shop_id BIGINT PRIMARY KEY COMMENT '店铺ID',
    user_id BIGINT NOT NULL COMMENT '所属用户ID',
    shop_name VARCHAR(50) NOT NULL COMMENT '店铺名称',
    shop_status ENUM('NORMAL','FROZEN','CLOSED') DEFAULT 'NORMAL' COMMENT '店铺状态',
    shop_description TEXT COMMENT '店铺简介',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    shop_avatar_url VARCHAR(255) COMMENT '店铺头像URL',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='店铺表';

-- 钱包表
CREATE TABLE wallet (
    wallet_id BIGINT PRIMARY KEY COMMENT '钱包ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_balance DECIMAL(15,2) NOT NULL COMMENT '总余额',
    available_balance DECIMAL(15,2) DEFAULT 0.00 COMMENT '可用余额',
    pending_balance DECIMAL(15,2) DEFAULT 0.00 COMMENT '待确认金额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包表';

-- 钱包流水表
CREATE TABLE flow_logs (
    wallet_logs_id BIGINT PRIMARY KEY COMMENT '流水ID',
    wallet_id BIGINT NOT NULL COMMENT '钱包ID',
    user_id BIGINT,
    related_order BIGINT COMMENT '关联订单ID',
    flow_type VARCHAR(50) NOT NULL COMMENT '流水类型',
    balance_change DECIMAL(15,2) NOT NULL COMMENT '变动金额',
    balance_after DECIMAL(15,2) NOT NULL COMMENT '变动后余额',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (wallet_id) REFERENCES wallet(wallet_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='钱包流水表';

-- 用户提现申请表
CREATE TABLE withdraw_request (
    request_id     BIGINT PRIMARY KEY COMMENT '提现申请ID',
    user_id        BIGINT NOT NULL COMMENT '用户ID，关联用户表',
    wallet_id      BIGINT NOT NULL COMMENT '钱包ID，关联钱包表',
    amount         DECIMAL(15,2) NOT NULL COMMENT '提现金额',
    status         ENUM('PENDING', 'APPROVED', 'REJECTED', 'COMPLETED','CANCELED') DEFAULT 'PENDING' COMMENT '提现状态',
    reason         VARCHAR(255) DEFAULT NULL COMMENT '审核拒绝理由',
    payment_method VARCHAR(50) NOT NULL COMMENT '提现方式（银行卡、微信、支付宝）',
    account_info   VARCHAR(255) NOT NULL COMMENT '提现账号信息',
    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    updated_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_withdraw_request_user FOREIGN KEY (user_id) REFERENCES user(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_withdraw_request_wallet FOREIGN KEY (wallet_id) REFERENCES wallet(wallet_id) ON DELETE CASCADE
) COMMENT='用户提现申请表';

-- 商家申请表
CREATE TABLE merchant_applications (
    application_id BIGINT PRIMARY KEY COMMENT '申请ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    shop_name VARCHAR(50) NOT NULL COMMENT '店铺名称',
    shop_avatar_url VARCHAR(255) COMMENT '店铺头像URL',
    shop_description TEXT COMMENT '店铺简介',
    status ENUM('PENDING', 'APPROVED', 'REJECTED','CANCELED') DEFAULT 'PENDING' COMMENT '审核状态',
    reason TEXT COMMENT '管理员审核拒绝理由',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家申请表';

-- 地址表
CREATE TABLE user_address (
    address_id BIGINT PRIMARY KEY COMMENT '地址ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    consignee VARCHAR(50) NOT NULL COMMENT '收件人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    country VARCHAR(50) NOT NULL COMMENT '国家',
    province VARCHAR(50) NOT NULL COMMENT '省份',
    city VARCHAR(50) NOT NULL COMMENT '城市',
    district VARCHAR(50) NOT NULL COMMENT '区县',
    street VARCHAR(200) NOT NULL COMMENT '详细地址',
    is_default TINYINT(1) DEFAULT 0 COMMENT '是否默认',
    address_tag VARCHAR(20) COMMENT '地址标签',
    status TINYINT(1) DEFAULT 1 COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户地址表';

-- 商品分类表
CREATE TABLE categories (
    category_id BIGINT PRIMARY KEY COMMENT '分类ID',
    category_name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT COMMENT '父分类ID',
    icon VARCHAR(255) COMMENT '分类图标',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES categories(category_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- 商品表
CREATE TABLE products (
    product_id BIGINT PRIMARY KEY COMMENT '商品ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    user_id BIGINT NOT NULL COMMENT'用户ID',
    brand VARCHAR(20) NOT NULL COMMENT '品牌',
    name VARCHAR(100) NOT NULL COMMENT '商品名称',
    description TEXT NOT NULL COMMENT '商品描述',
    stock_mode ENUM('NORMAL','PRE_SALE') NOT NULL COMMENT '库存模式',
    is_active BOOLEAN DEFAULT true COMMENT '是否上架',
    status ENUM('PENDING', 'APPROVED', 'REJECTED','CANCELED') DEFAULT 'PENDING' COMMENT '审核状态',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    sales INT UNSIGNED DEFAULT 0 COMMENT '销量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (shop_id) REFERENCES shops(shop_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE;
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 商品配置表
CREATE TABLE product_configurations (
    config_id BIGINT PRIMARY KEY COMMENT '配置ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    configuration VARCHAR(200) NOT NULL COMMENT '配置信息',
    price DECIMAL(15,2) NOT NULL COMMENT '价格',
    stock INT UNSIGNED DEFAULT 0 COMMENT '库存',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品配置表';


-- 订单表
CREATE TABLE orders (
    order_id BIGINT PRIMARY KEY COMMENT '订单ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    shop_id BIGINT NOT NULL COMMENT '店铺ID',
    address_id BIGINT NOT NULL COMMENT '地址ID',
    shipper_address_id BIGINT NOT NULL COMMENT '商家发货地址，引用地址表',
    logistics_number VARCHAR(18) COMMENT '物流单号',
    logistics_com VARCHAR(30) COMMENT '物流公司',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '商品实付金额',
    original_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '商品总金额',
    status ENUM('PENDING_PAYMENT', 'PENDING', 'SHIPPED','IN_TRANSIT', 'DELIVERED', 'RECEIVED', 'CANCELLED') NOT NULL COMMENT '订单状态',
    return_status ENUM('NOT_REQUESTED', 'REQUESTED', 'APPROVED', 'REJECTED', 'RETURNED','RECEIVED') DEFAULT 'NOT_REQUESTED' COMMENT '退货状态',
    payment_type TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '支付方式',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    consign_time TIMESTAMP NULL COMMENT '发货时间',
    end_time TIMESTAMP NULL COMMENT '完成时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';


-- 商品多图支持表
CREATE TABLE product_images (
    image_id BIGINT PRIMARY KEY COMMENT '图片ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    url VARCHAR(255) NOT NULL COMMENT '图片URL',
    is_main BOOLEAN DEFAULT false COMMENT '是否主图',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品图片表';

-- 库存变动日志表
CREATE TABLE stock_change_logs (
    log_id BIGINT PRIMARY KEY COMMENT '日志ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    config_id BIGINT NOT NULL COMMENT '配置ID',
    delta INT NOT NULL COMMENT '库存变动量',
    source_type ENUM('SALED','MUNAL_ADD','MUNAL_DECREASE') NOT NULL COMMENT '变动来源',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动日志';

-- 收藏表
CREATE TABLE favorites (
    favorites_id BIGINT PRIMARY KEY COMMENT '收藏ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品收藏表';

-- 评论表
CREATE TABLE reviews (
    review_id BIGINT PRIMARY KEY COMMENT '评论ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    parent_review_id BIGINT COMMENT '父评论ID',
    rating INT CHECK (rating BETWEEN 1 AND 5) COMMENT '评分',
    comment TEXT NOT NULL COMMENT '评论内容',
    report_count INT DEFAULT 0 COMMENT '被举报次数',
    status ENUM('PENDING', 'APPROVED', 'REJECTED') DEFAULT 'APPROVED' COMMENT '审核状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品评论表';


-- 购物车表
CREATE TABLE cart_Items (
    cart_item_id BIGINT PRIMARY KEY COMMENT '购物车项ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    config_id BIGINT NOT NULL COMMENT '配置ID',
    quantity INT UNSIGNED DEFAULT 1 COMMENT '数量',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 订单详情表
CREATE TABLE order_details (
    order_item_id BIGINT PRIMARY KEY COMMENT '订单项ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    config_id BIGINT NOT NULL COMMENT '配置ID',
    quantity INT UNSIGNED NOT NULL COMMENT '购买数量',
    price DECIMAL(15,2) NOT NULL COMMENT '成交单价',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单详情表';

-- 优惠券类型表
CREATE TABLE coupon_types (
    type_id BIGINT PRIMARY KEY COMMENT '类型ID',
    coupon_name VARCHAR(50) NOT NULL COMMENT '优惠券名称',
    coupon_scope ENUM('GLOBAL','SHOP','CATEGORY','PRODUCT') NOT NULL COMMENT '适用范围',
    coupon_type ENUM('FIXED','PERCENT','GIFT') NOT NULL COMMENT '券类型',
    min_purchase_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低消费',
    discount_value DECIMAL(10,2) NOT NULL COMMENT '优惠值',
    validity_type ENUM('FIXED','DYNAMIC') NOT NULL COMMENT '有效期类型',
    stackable BOOLEAN DEFAULT false COMMENT '是否可叠加',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券类型表';

-- 优惠券表
CREATE TABLE coupons (
    coupon_id BIGINT PRIMARY KEY COMMENT '优惠券ID',
    type_id BIGINT NOT NULL COMMENT '类型ID',
    shop_id BIGINT COMMENT '店铺ID',
    category_id BIGINT COMMENT '分类ID',
    product_id BIGINT COMMENT '商品ID',
    gift_product_id BIGINT COMMENT '赠品ID',
    validity_days INT COMMENT '有效天数',
    code VARCHAR(20) UNIQUE COMMENT '优惠码',
    total_quantity INT UNSIGNED NOT NULL COMMENT '总数量',
    remaining_quantity INT UNSIGNED NOT NULL COMMENT '剩余数量',
    limit_per_user INT UNSIGNED DEFAULT 1 COMMENT '每人限领',
    available_start TIMESTAMP NOT NULL COMMENT '领取开始时间',
    available_end TIMESTAMP NOT NULL COMMENT '领取结束时间',
    start_time TIMESTAMP COMMENT '有效期开始',
    end_time TIMESTAMP COMMENT '有效期结束',
    is_public BOOLEAN DEFAULT false COMMENT '是否公开',
    created_by BIGINT NOT NULL COMMENT '创建人',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券表';

-- 用户优惠券表
CREATE TABLE user_coupons (
    user_coupon_id BIGINT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    coupon_id BIGINT NOT NULL COMMENT '优惠券ID',
    status ENUM('UNUSED','USED','EXPIRED') DEFAULT 'UNUSED' COMMENT '使用状态',
    obtained_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    use_time TIMESTAMP NULL COMMENT '使用时间',
    valid_start TIMESTAMP NOT NULL COMMENT '有效期开始',
    valid_end TIMESTAMP NOT NULL COMMENT '有效期结束',
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

ALTER TABLE flow_logs
ADD COLUMN user_id BIGINT AFTER wallet_id,
ADD CONSTRAINT fk_flowLogs_users
FOREIGN KEY (user_id) REFERENCES users(user_id);

CREATE TABLE dead_letter_log (
    dead_letter_log_id BIGINT PRIMARY KEY COMMENT '主键ID',
    message TEXT NOT NULL COMMENT '日志内容',
    reason TEXT COMMENT '失败原因',
    status ENUM('PENDING', 'RESOLVED', 'FAILED') NOT NULL DEFAULT 'PENDING' COMMENT '处理状态: PENDING-待处理, RESOLVED-已处理, FAILED-永久失败',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='死信日志表，存储处理失败的日志';
