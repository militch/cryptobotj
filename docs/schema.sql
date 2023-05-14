CREATE TABLE `market_kline` (
    `id` int not null AUTO_INCREMENT,
    `exchange` varchar(128) not null comment '交易所',
    `symbol` varchar(128) not null comment '交易对',
    `decimals` varchar(128) not null comment '交易精度',
    `open_time` datetime not null COMMENT '开盘时间',
    `close_time` datetime not null COMMENT '收盘时间',
    `open_price` varchar(128) not null default '0' COMMENT '开盘价',
    `close_price` varchar(128) not null default '0' COMMENT '收盘价',
    `high_price` varchar(128) not null default '0' COMMENT '最高价',
    `low_price` varchar(128) not null default '0' COMMENT '最低价',
    `volume` varchar(128) not null default '0' COMMENT '成交量',
    `quote_volume` varchar(128) not null default '0' COMMENT '交易额',
    `t_count` int not null default '0' COMMENT '交易笔数',
    `create_time` datetime not null default current_timestamp COMMENT '创建时间',
    PRIMARY KEY (`id`)
) COMMENT='市场K线';