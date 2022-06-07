DROP TABLE IF EXISTS `financial_user`;
CREATE TABLE `financial_user`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `type`         VARCHAR(64)     NOT NULL COMMENT '类型',
    `name`         VARCHAR(64)     NOT NULL COMMENT '用户名',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '用户';

DROP TABLE IF EXISTS `financial_user_robot`;
CREATE TABLE `financial_user_robot`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `user_id`      BIGINT UNSIGNED NOT NULL COMMENT '用户 id',
    `symbol_id`    BIGINT UNSIGNED NOT NULL COMMENT '标的 id',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '机器人用户';

DROP TABLE IF EXISTS `financial_account`;
CREATE TABLE `financial_account`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `user_id`      BIGINT UNSIGNED NOT NULL COMMENT '交易员 id',
    `type`         VARCHAR(64)     NOT NULL COMMENT '类型',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '账号';

DROP TABLE IF EXISTS `financial_account_trade`;
CREATE TABLE `financial_account_trade`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `account_id`   BIGINT UNSIGNED COMMENT '账号 id',
    `balance`      DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '余额',
    `frozen_fund`  DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '冻结的资金',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '交易账户';

DROP TABLE IF EXISTS `financial_account_login`;
CREATE TABLE `financial_account_login`
(
    `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`       BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`       BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `account_id`    BIGINT UNSIGNED COMMENT '账号 id',
    `login_account` VARCHAR(64)     NOT NULL COMMENT '登陆账户',
    `password`      VARCHAR(64)     NOT NULL COMMENT '密码',
    `access_token`  VARCHAR(128) COMMENT '是否已登陆',
    `expire_time`   DATETIME COMMENT '登陆过期时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '交易账户';

DROP TABLE IF EXISTS `financial_order`;
CREATE TABLE `financial_order`
(
    `id`                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`         DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`            BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`            BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `account_trade_id`   BIGINT UNSIGNED COMMENT '交易账号 id',
    `symbol_id`          BIGINT UNSIGNED NOT NULL COMMENT '交易标的',
    `quantity`           BIGINT                   DEFAULT 0 COMMENT '委托数量',
    `price`              DECIMAL(20, 4)           DEFAULT 0 COMMENT '委托价格',
    `side`               VARCHAR(64) COMMENT '交易方向',
    `type`               VARCHAR(64) COMMENT '交易类型',
    `completed_quantity` BIGINT                   DEFAULT 0 COMMENT '成交数量',
    `canceled_quantity`  BIGINT                   DEFAULT 0 COMMENT '撤销数量',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '委托单';

DROP TABLE IF EXISTS `financial_hold_position`;
CREATE TABLE `financial_hold_position`
(
    `id`               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified`     DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`          BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`          BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',
    `delete_time`      DATETIME        NOT NULL DEFAULT '9999-12-31 23:59:59' COMMENT '删除时间',

    `account_trade_id` BIGINT UNSIGNED NOT NULL COMMENT '交易账号 id',
    `symbol_id`        BIGINT UNSIGNED NOT NULL COMMENT '持有标的',
    `quantity`         BIGINT          NOT NULL DEFAULT 0 COMMENT '持有头寸数量',
    `frozen_quantity`  BIGINT          NOT NULL DEFAULT 0 COMMENT '冻结的头寸数量',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_symbol` (`account_trade_id`, `symbol_id`, `deleted`, `delete_time`)
) ENGINE = InnoDB COMMENT '持有头寸';

DROP TABLE IF EXISTS `financial_symbol`;
CREATE TABLE `financial_symbol`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `symbol`       VARCHAR(64) COMMENT '持有标的',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '标的';

DROP TABLE IF EXISTS `financial_trade_voucher`;
CREATE TABLE `financial_trade_voucher`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `maker_id`     BIGINT UNSIGNED NOT NULL COMMENT '挂单',
    `take_id`      BIGINT UNSIGNED NOT NULL COMMENT '吃单',
    `symbol_id`    BIGINT UNSIGNED NOT NULL COMMENT '持有标的',
    `taker_side`   VARCHAR(64)     NOT NULL COMMENT '吃单的买卖方向',
    `quantity`     BIGINT          NOT NULL DEFAULT 0 COMMENT '成交数量',
    `price`        DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '成交价格',
    `trade_time`   DATETIME        NOT NULL COMMENT '成交时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '交易凭证';

DROP TABLE IF EXISTS `financial_market_k_line`;
CREATE TABLE `financial_market_k_line`
(
    `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
    `gmt_create`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modified` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `version`      BIGINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '乐观锁',
    `deleted`      BOOLEAN         NOT NULL DEFAULT false COMMENT '伪删除',

    `symbol_id`    BIGINT UNSIGNED NOT NULL COMMENT '持有标的',
    `level`        VARCHAR(64)     NOT NULL COMMENT '级别',
    `time`         DATETIME        NOT NULL COMMENT '成交时间',
    `open`         DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '开盘价',
    `close`        DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '收盘价',
    `high`         DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '最高价',
    `low`          DECIMAL(20, 4)  NOT NULL DEFAULT 0 COMMENT '最低价',

    `volume`       BIGINT          NOT NULL DEFAULT 0 COMMENT '成交量',

    PRIMARY KEY (`id`)
) ENGINE = InnoDB COMMENT '行情';

