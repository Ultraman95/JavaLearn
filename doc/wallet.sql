# Host: 192.168.100.163  (Version: 5.6.37-log)
# Date: 2019-07-22 16:02:32
# Generator: MySQL-Front 5.3  (Build 4.234)

/*!40101 SET NAMES utf8 */;

#
# Structure for table "t_address"
#

DROP TABLE IF EXISTS `t_address`;
CREATE TABLE `t_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `coinid` int(11) DEFAULT NULL COMMENT '币种代码',
  `address` varchar(200) DEFAULT NULL COMMENT '充值地址',
  `createdate` datetime DEFAULT NULL COMMENT '创建日期',
  `isavailable` tinyint(1) DEFAULT NULL COMMENT '是否有效',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "t_address"
#


#
# Structure for table "t_coininfo"
#

DROP TABLE IF EXISTS `t_coininfo`;
CREATE TABLE `t_coininfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '唯一ID',
  `coinname` varchar(10) DEFAULT NULL COMMENT '币种符号',
  `coinprecision` int(11) DEFAULT NULL COMMENT '币种精度',
  `confirmations` int(11) DEFAULT NULL COMMENT '安全块确认数',
  `maxlittlewithdrawamount` decimal(30,18) DEFAULT NULL COMMENT '小额提现数额的最大阈值',
  `contractasset` tinyint(1) DEFAULT NULL COMMENT '是否是合约资产,0-否,1-是',
  `contractmainchainasset` varchar(10) DEFAULT NULL COMMENT '合约资产的主链资产标识（比如ERC20代币的主链资产是ETH，没有就返回null）',
  `contractaddress` varchar(150) DEFAULT NULL COMMENT '合约地址（没有返回null）',
  `coinid` int(11) DEFAULT NULL COMMENT '币种ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

#
# Data for table "t_coininfo"
#

INSERT INTO `t_coininfo` VALUES (1,'BTC',8,6,1.000000000000000000,0,NULL,NULL,1),(2,'ETH',8,6,1.000000000000000000,0,NULL,NULL,2),(3,'BITC',18,30,1.000000000000000000,1,'ETH','0x0f565ee8dcfeb0016d3c92158a5223378916e5a0',202),(4,'LTC',8,6,1.000000000000000000,0,NULL,NULL,3),(5,'BCH',8,6,1.000000000000000000,0,NULL,NULL,5),(6,'USDT',8,6,1.000000000000000000,0,NULL,NULL,31),(7,'ETC',8,6,1.000000000000000000,0,NULL,NULL,11),(8,'XRP',8,6,1.000000000000000000,0,NULL,NULL,8);

#
# Structure for table "t_params"
#

DROP TABLE IF EXISTS `t_params`;
CREATE TABLE `t_params` (
  `paramid` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `paramcode` varchar(50) DEFAULT NULL COMMENT '参数代码',
  `paramdesc` varchar(255) DEFAULT NULL COMMENT '参数描述',
  `paramvalue` varchar(200) DEFAULT NULL COMMENT '参数值',
  PRIMARY KEY (`paramid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

#
# Data for table "t_params"
#

INSERT INTO `t_params` VALUES (1,'1','BTC最后一次扫描的区块高度','1438995'),(2,'2','ETH最后一次扫描的区块高度','-1'),(3,'11','ETC最后一次扫描的区块高度','82'),(4,'8','XRP最后一次扫描的区块高度','13019932');

#
# Structure for table "t_recharge"
#

DROP TABLE IF EXISTS `t_recharge`;
CREATE TABLE `t_recharge` (
  `rechargeid` int(11) NOT NULL AUTO_INCREMENT COMMENT '充值ID',
  `coinid` int(2) DEFAULT NULL COMMENT '币种',
  `txid` varchar(200) DEFAULT NULL COMMENT '交易ID',
  `fromaddr` varchar(150) DEFAULT NULL COMMENT '付款地址',
  `toaddr` varchar(150) DEFAULT NULL COMMENT '收款地址',
  `value` decimal(19,10) DEFAULT NULL COMMENT '充值数量',
  `fee` decimal(19,10) DEFAULT NULL COMMENT '充值手续费',
  `receivetime` datetime DEFAULT NULL COMMENT '接收时间',
  `status` int(1) DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rechargeid`),
  UNIQUE KEY `NDX_TXID` (`txid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "t_recharge"
#


#
# Structure for table "t_withdraw"
#

DROP TABLE IF EXISTS `t_withdraw`;
CREATE TABLE `t_withdraw` (
  `withdrawid` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `orderid` varchar(30) DEFAULT NULL COMMENT '对应交易所提现订单ID',
  `toaddr` varchar(100) DEFAULT NULL COMMENT '提币地址',
  `value` decimal(19,9) DEFAULT NULL COMMENT '提币数量',
  `coinid` int(11) DEFAULT NULL COMMENT '代币类型',
  `passwd` varchar(50) DEFAULT NULL COMMENT '热钱包密码',
  `requesttime` datetime DEFAULT NULL COMMENT '申请提币时间',
  `processtime` datetime DEFAULT NULL COMMENT '完成处理时间',
  `status` int(11) DEFAULT NULL COMMENT '状态,0-init,1-online,2-pending,3-done,4-failed',
  `auditing` int(11) DEFAULT NULL COMMENT '是否需要审核',
  `auditor` varchar(30) DEFAULT NULL COMMENT '审核人',
  `txid` varchar(100) DEFAULT NULL COMMENT '区块链交易唯一ID',
  `startheight` bigint(11) DEFAULT NULL COMMENT '交易时当前区块高度',
  `txheight` bigint(11) DEFAULT NULL COMMENT '当前交易实际所在区块高度',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注信息',
  PRIMARY KEY (`withdrawid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

#
# Data for table "t_withdraw"
#

INSERT INTO `t_withdraw` VALUES (35,'T181015151312503','2MsV7nifu4m2yktYZwDQmWwoith7Bmr63KW',0.001000000,1,'wallet1234','2018-10-15 17:51:21','2018-10-15 00:00:00',3,0,NULL,'ac48e56d0b1ed364047a6050ee1cd955adc94cd40daea037a60257073a9ab1ad',1438198,1438198,'收到');

#
# Structure for table "t_xrpaccount"
#

DROP TABLE IF EXISTS `t_xrpaccount`;
CREATE TABLE `t_xrpaccount` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `accountid` varchar(200) DEFAULT NULL COMMENT '地址',
  `masterseed` varchar(150) DEFAULT NULL COMMENT '私钥',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

#
# Data for table "t_xrpaccount"
#

INSERT INTO `t_xrpaccount` VALUES (1,'rHaFV7CDzUvzS5AtFtn48GBSHAJoyy7due','shP8aXQj9nzDGVXjMw8XQR8Ck22Qd'),(4,'rHcg5iq9vBV3L85MR54tYoYbPtAfL6KgqJ','snyqLd8sw2svj3VxqmxNgZMWA9c6v');

#
# Structure for table "test"
#

DROP TABLE IF EXISTS `test`;
CREATE TABLE `test` (
  `name` varchar(255) NOT NULL,
  `desc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

#
# Data for table "test"
#

INSERT INTO `test` VALUES ('1','2');
