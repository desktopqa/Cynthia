/*
Navicat MySQL Data Transfer

Source Server         : 10.11.202.226 --cythia test机
Source Server Version : 50077
Source Host           : 10.11.202.226:3306
Source Database       : cynthia_open

Target Server Type    : MYSQL
Target Server Version : 50077
File Encoding         : 65001

Date: 2014-05-05 17:54:29
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for attachment
-- ----------------------------
DROP TABLE IF EXISTS `attachment`;
CREATE TABLE `attachment` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `create_user` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `size` int(10) unsigned NOT NULL,
  `data` longblob,
  `file_id` varchar(128) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of attachment
-- ----------------------------

-- ----------------------------
-- Table structure for data
-- ----------------------------
DROP TABLE IF EXISTS `data`;
CREATE TABLE `data` (
  `id` int(10) NOT NULL,
  `templateId` int(50) default NULL,
  `createUser` varchar(50) default NULL,
  `templateTypeId` int(1) default NULL,
  `title` varchar(1024) default NULL,
  `description` longtext,
  `createTime` varchar(50) default NULL,
  `lastModifyTime` varchar(50) default NULL,
  `assignUser` varchar(50) default NULL,
  `statusId` int(50) default NULL,
  `fieldInt_1` int(50) default NULL,
  `fieldInt_2` int(50) default NULL,
  `fieldInt_3` int(50) default NULL,
  `fieldInt_4` int(50) default NULL,
  `fieldInt_5` int(50) default NULL,
  `fieldInt_6` int(50) default NULL,
  `fieldInt_7` int(50) default NULL,
  `fieldInt_8` int(50) default NULL,
  `fieldInt_9` int(50) default NULL,
  `fieldInt_10` int(50) default NULL,
  `fieldInt_11` int(50) default NULL,
  `fieldInt_12` int(50) default NULL,
  `fieldInt_13` int(50) default NULL,
  `fieldInt_14` int(50) default NULL,
  `fieldInt_15` int(50) default NULL,
  `fieldInt_16` int(50) default NULL,
  `fieldInt_17` int(50) default NULL,
  `fieldInt_18` int(50) default NULL,
  `fieldInt_19` int(50) default NULL,
  `fieldInt_20` int(50) default NULL,
  `fieldInt_21` int(50) default NULL,
  `fieldInt_22` int(50) default NULL,
  `fieldInt_23` int(50) default NULL,
  `fieldInt_24` int(50) default NULL,
  `fieldInt_25` int(50) default NULL,
  `fieldInt_26` int(50) default NULL,
  `fieldInt_27` int(50) default NULL,
  `fieldInt_28` int(50) default NULL,
  `fieldInt_29` int(50) default NULL,
  `fieldInt_30` int(50) default NULL,
  `fieldInt_31` int(50) default NULL,
  `fieldInt_32` int(50) default NULL,
  `fieldInt_33` int(50) default NULL,
  `fieldInt_34` int(50) default NULL,
  `fieldInt_35` int(50) default NULL,
  `fieldInt_36` int(50) default NULL,
  `fieldInt_37` int(50) default NULL,
  `fieldInt_38` int(50) default NULL,
  `fieldInt_39` int(50) default NULL,
  `fieldInt_40` int(50) default NULL,
  `fieldIntM_1` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_2` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_3` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_4` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_5` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_6` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_7` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_8` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_9` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_10` varchar(300) default '' COMMENT '对应多选类型',
  `fieldStr_1` varchar(300) default NULL,
  `fieldStr_2` varchar(300) default NULL,
  `fieldStr_3` varchar(300) default NULL,
  `fieldStr_4` varchar(300) default NULL,
  `fieldStr_5` varchar(300) default NULL,
  `fieldStr_6` varchar(300) default NULL,
  `fieldStr_7` varchar(300) default NULL,
  `fieldStr_8` varchar(300) default NULL,
  `fieldStr_9` varchar(300) default NULL,
  `fieldStr_10` varchar(300) default NULL,
  `fieldStr_11` varchar(300) default NULL,
  `fieldStr_12` varchar(300) default NULL,
  `fieldStr_13` varchar(300) default NULL,
  `fieldStr_14` varchar(300) default NULL,
  `fieldStr_15` varchar(300) default NULL,
  `fieldStr_16` varchar(300) default NULL,
  `fieldStr_17` varchar(300) default NULL,
  `fieldStr_18` varchar(300) default NULL,
  `fieldStr_19` varchar(300) default NULL,
  `fieldStr_20` varchar(300) default NULL,
  `fieldStr_21` varchar(300) default NULL,
  `fieldStr_22` varchar(300) default NULL,
  `fieldStr_23` varchar(300) default NULL,
  `fieldStr_24` varchar(300) default NULL,
  `fieldStr_25` varchar(300) default NULL,
  `fieldStr_26` varchar(300) default NULL,
  `fieldStr_27` varchar(300) default NULL,
  `fieldStr_28` varchar(300) default NULL,
  `fieldStr_29` varchar(300) default NULL,
  `fieldStr_30` varchar(300) default NULL,
  `fieldStr_31` varchar(300) default NULL,
  `fieldStr_32` varchar(300) default NULL,
  `fieldStr_33` varchar(300) default NULL,
  `fieldStr_34` varchar(300) default NULL,
  `fieldStr_35` varchar(300) default NULL,
  `fieldStrM_1` text,
  `fieldStrM_2` text,
  `fieldStrM_3` text,
  `fieldStrM_4` text,
  `fieldStrM_5` text,
  `fieldStrM_6` text,
  `fieldStrM_7` text,
  `fieldStrM_8` text,
  `fieldStrM_9` text,
  `fieldStrM_10` text,
  `fieldStrM_11` text,
  `fieldStrM_12` text,
  `fieldStrM_13` text,
  `fieldStrM_14` text,
  `fieldStrM_15` text,
  `fieldStrM_16` text,
  `fieldStrM_17` text,
  `fieldStrM_18` text,
  `fieldStrM_19` text,
  `fieldStrM_20` text,
  `fieldStrM_21` text,
  `fieldStrM_22` text,
  `fieldStrM_23` text,
  `fieldStrM_24` text,
  `fieldStrM_25` text,
  `fieldStrM_26` text,
  `fieldStrM_27` text,
  `fieldStrM_28` text,
  `fieldStrM_29` text,
  `fieldStrM_30` text,
  `fieldEditor_1` text,
  `fieldEditor_2` text,
  `fieldEditor_3` text,
  `fieldEditor_4` text,
  `fieldEditor_5` text,
  `fieldCom_1` varchar(1024) default NULL,
  `fieldCom_2` varchar(1024) default NULL,
  `fieldCom_3` varchar(1024) default NULL,
  `fieldCom_4` varchar(1024) default NULL,
  `fieldCom_5` varchar(1024) default NULL,
  `is_valid` varchar(1) default '1',
  PRIMARY KEY  (`id`),
  KEY `createUser` USING BTREE (`createUser`),
  KEY `assignUser` USING BTREE (`assignUser`),
  KEY `templateId` USING BTREE (`templateId`),
  KEY `createTime` USING BTREE (`createTime`),
  KEY `lastModifyTime` USING BTREE (`lastModifyTime`),
  KEY `statsId` USING BTREE (`statusId`),
  KEY `status_index` USING BTREE (`statusId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of data
-- ----------------------------

-- ----------------------------
-- Table structure for data_log
-- ----------------------------
DROP TABLE IF EXISTS `data_log`;
CREATE TABLE `data_log` (
  `id` int(10) NOT NULL auto_increment,
  `dataId` int(50) NOT NULL,
  `logcreateUser` varchar(50) default NULL,
  `logActionIndex` int(100) NOT NULL default '1',
  `logcreateTime` varchar(50) default NULL,
  `logActionId` int(50) default NULL,
  `logActionComment` text,
  `templateId` int(50) default NULL,
  `createUser` varchar(50) default NULL,
  `templateTypeId` int(1) default NULL,
  `title` varchar(1024) default NULL,
  `description` longtext,
  `createTime` varchar(50) default NULL,
  `lastModifyTime` varchar(50) default NULL,
  `assignUser` varchar(50) default NULL,
  `statusId` int(10) default NULL,
  `fieldInt_1` int(50) default NULL,
  `fieldInt_2` int(50) default NULL,
  `fieldInt_3` int(50) default NULL,
  `fieldInt_4` int(50) default NULL,
  `fieldInt_5` int(50) default NULL,
  `fieldInt_6` int(50) default NULL,
  `fieldInt_7` int(50) default NULL,
  `fieldInt_8` int(50) default NULL,
  `fieldInt_9` int(50) default NULL,
  `fieldInt_10` int(50) default NULL,
  `fieldInt_11` int(50) default NULL,
  `fieldInt_12` int(50) default NULL,
  `fieldInt_13` int(50) default NULL,
  `fieldInt_14` int(50) default NULL,
  `fieldInt_15` int(50) default NULL,
  `fieldInt_16` int(50) default NULL,
  `fieldInt_17` int(50) default NULL,
  `fieldInt_18` int(50) default NULL,
  `fieldInt_19` int(50) default NULL,
  `fieldInt_20` int(50) default NULL,
  `fieldInt_21` int(50) default NULL,
  `fieldInt_22` int(50) default NULL,
  `fieldInt_23` int(50) default NULL,
  `fieldInt_24` int(50) default NULL,
  `fieldInt_25` int(50) default NULL,
  `fieldInt_26` int(50) default NULL,
  `fieldInt_27` int(50) default NULL,
  `fieldInt_28` int(50) default NULL,
  `fieldInt_29` int(50) default NULL,
  `fieldInt_30` int(50) default NULL,
  `fieldInt_31` int(50) default NULL,
  `fieldInt_32` int(50) default NULL,
  `fieldInt_33` int(50) default NULL,
  `fieldInt_34` int(50) default NULL,
  `fieldInt_35` int(50) default NULL,
  `fieldInt_36` int(50) default NULL,
  `fieldInt_37` int(50) default NULL,
  `fieldInt_38` int(50) default NULL,
  `fieldInt_39` int(50) default NULL,
  `fieldInt_40` int(50) default NULL,
  `fieldIntM_1` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_2` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_3` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_4` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_5` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_6` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_7` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_8` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_9` varchar(300) default '' COMMENT '对应多选类型',
  `fieldIntM_10` varchar(300) default '' COMMENT '对应多选类型',
  `fieldStr_1` varchar(300) default NULL,
  `fieldStr_2` varchar(300) default NULL,
  `fieldStr_3` varchar(300) default NULL,
  `fieldStr_4` varchar(300) default NULL,
  `fieldStr_5` varchar(300) default NULL,
  `fieldStr_6` varchar(300) default NULL,
  `fieldStr_7` varchar(300) default NULL,
  `fieldStr_8` varchar(300) default NULL,
  `fieldStr_9` varchar(300) default NULL,
  `fieldStr_10` varchar(300) default NULL,
  `fieldStr_11` varchar(300) default NULL,
  `fieldStr_12` varchar(300) default NULL,
  `fieldStr_13` varchar(300) default NULL,
  `fieldStr_14` varchar(300) default NULL,
  `fieldStr_15` varchar(300) default NULL,
  `fieldStr_16` varchar(300) default NULL,
  `fieldStr_17` varchar(300) default NULL,
  `fieldStr_18` varchar(300) default NULL,
  `fieldStr_19` varchar(300) default NULL,
  `fieldStr_20` varchar(300) default NULL,
  `fieldStr_21` varchar(300) default NULL,
  `fieldStr_22` varchar(300) default NULL,
  `fieldStr_23` varchar(300) default NULL,
  `fieldStr_24` varchar(300) default NULL,
  `fieldStr_25` varchar(300) default NULL,
  `fieldStr_26` varchar(300) default NULL,
  `fieldStr_27` varchar(300) default NULL,
  `fieldStr_28` varchar(300) default NULL,
  `fieldStr_29` varchar(300) default NULL,
  `fieldStr_30` varchar(300) default NULL,
  `fieldStr_31` varchar(300) default NULL,
  `fieldStr_32` varchar(300) default NULL,
  `fieldStr_33` varchar(300) default NULL,
  `fieldStr_34` varchar(300) default NULL,
  `fieldStr_35` varchar(300) default NULL,
  `fieldStrM_1` text,
  `fieldStrM_2` text,
  `fieldStrM_3` text,
  `fieldStrM_4` text,
  `fieldStrM_5` text,
  `fieldStrM_6` text,
  `fieldStrM_7` text,
  `fieldStrM_8` text,
  `fieldStrM_9` text,
  `fieldStrM_10` text,
  `fieldStrM_11` text,
  `fieldStrM_12` text,
  `fieldStrM_13` text,
  `fieldStrM_14` text,
  `fieldStrM_15` text,
  `fieldStrM_16` text,
  `fieldStrM_17` text,
  `fieldStrM_18` text,
  `fieldStrM_19` text,
  `fieldStrM_20` text,
  `fieldStrM_21` text,
  `fieldStrM_22` text,
  `fieldStrM_23` text,
  `fieldStrM_24` text,
  `fieldStrM_25` text,
  `fieldStrM_26` text,
  `fieldStrM_27` text,
  `fieldStrM_28` text,
  `fieldStrM_29` text,
  `fieldStrM_30` text,
  `fieldEditor_1` text,
  `fieldEditor_2` text,
  `fieldEditor_3` text,
  `fieldEditor_4` text,
  `fieldEditor_5` text,
  `fieldCom_1` varchar(1024) default NULL,
  `fieldCom_2` varchar(1024) default NULL,
  `fieldCom_3` varchar(1024) default NULL,
  `fieldCom_4` varchar(1024) default NULL,
  `fieldCom_5` varchar(1024) default NULL,
  `is_valid` varchar(1) default '1',
  PRIMARY KEY  (`id`),
  KEY `FK1` USING BTREE (`dataId`),
  KEY `log_action_index` USING BTREE (`logActionIndex`),
  KEY `log_create_user_index` USING BTREE (`logcreateUser`)
) ENGINE=MyISAM AUTO_INCREMENT=385061 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of data_log
-- ----------------------------

-- ----------------------------
-- Table structure for default_filters
-- ----------------------------
DROP TABLE IF EXISTS `default_filters`;
CREATE TABLE `default_filters` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(255) NOT NULL,
  `filters` varchar(1024) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=97 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of default_filters
-- ----------------------------

-- ----------------------------
-- Table structure for event
-- ----------------------------
DROP TABLE IF EXISTS `event`;
CREATE TABLE `event` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(256) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of event
-- ----------------------------
INSERT INTO `event` VALUES ('1', 'Cynthia访问');
INSERT INTO `event` VALUES ('2', '后台管理');
INSERT INTO `event` VALUES ('3', '脚本管理');

-- ----------------------------
-- Table structure for event_user
-- ----------------------------
DROP TABLE IF EXISTS `event_user`;
CREATE TABLE `event_user` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(255) NOT NULL,
  `event_id` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=81 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of event_user
-- ----------------------------
INSERT INTO `event_user` VALUES ('80', 'admin', '2');

-- ----------------------------
-- Table structure for favorite_filters
-- ----------------------------
DROP TABLE IF EXISTS `favorite_filters`;
CREATE TABLE `favorite_filters` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(255) NOT NULL,
  `filters` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1250 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of favorite_filters
-- ----------------------------
INSERT INTO `favorite_filters` VALUES ('1234', 'admin', '119695,119891,119892,119893');

-- ----------------------------
-- Table structure for field_name_map
-- ----------------------------
DROP TABLE IF EXISTS `field_name_map`;
CREATE TABLE `field_name_map` (
  `id` int(10) NOT NULL auto_increment,
  `templateId` int(10) NOT NULL COMMENT '表单Id',
  `fieldColName` varchar(15) NOT NULL COMMENT '对应data_new中的字段列名',
  `fieldId` varchar(10) NOT NULL COMMENT '对应表单中字段Id',
  `fieldType` varchar(15) NOT NULL COMMENT '类型分为Int单选,Int多选,Str单行,Str多行,Editor',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=80811 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of field_name_map
-- ----------------------------
INSERT INTO `field_name_map` VALUES ('80681', '744313', 'fieldInt_1', '744314', 'Int');
INSERT INTO `field_name_map` VALUES ('80682', '744313', 'fieldInt_2', '744315', 'Int');
INSERT INTO `field_name_map` VALUES ('80683', '744313', 'fieldIntM_1', '744316', 'IntM');
INSERT INTO `field_name_map` VALUES ('80684', '744313', 'fieldStr_1', '744317', 'Str');
INSERT INTO `field_name_map` VALUES ('80685', '744313', 'fieldInt_3', '744318', 'Int');
INSERT INTO `field_name_map` VALUES ('80686', '744313', 'fieldInt_4', '744319', 'Int');
INSERT INTO `field_name_map` VALUES ('80687', '744313', 'fieldInt_5', '744320', 'Int');
INSERT INTO `field_name_map` VALUES ('80688', '744313', 'fieldInt_6', '744321', 'Int');
INSERT INTO `field_name_map` VALUES ('80689', '744313', 'fieldInt_7', '744337', 'Int');
INSERT INTO `field_name_map` VALUES ('80690', '744313', 'fieldStrM_1', '744338', 'StrM');

-- ----------------------------
-- Table structure for filter
-- ----------------------------
DROP TABLE IF EXISTS `filter`;
CREATE TABLE `filter` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `xml` longtext NOT NULL,
  `create_user` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `is_and` tinyint(1) NOT NULL,
  `is_public` tinyint(1) NOT NULL,
  `is_visible` tinyint(1) NOT NULL,
  `father_id` int(10) unsigned default NULL,
  `is_valid` tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (`id`),
  KEY `FK_filter_2` USING BTREE (`father_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of filter
-- ----------------------------
INSERT INTO `filter` VALUES ('119695', '待处理', '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<query>\r\n<env>\r\n<current_user/>\r\n<timerange>current</timerange>\r\n<current_template_type/>\r\n</env>\r\n<templateType id=\"$current_template_type$\">\r\n<display>\r\n<field id=\"title\" name=\"标题\"/>\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field id=\"status_id\" name=\"状态\"/>\r\n<field id=\"create_user\" name=\"创建人\"/>\r\n<field id=\"create_time\" name=\"创建时间\"/>\r\n<field id=\"last_modify_time\" name=\"修改时间\"/>\r\n</display>\r\n<order indent=\"1\">\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field desc=\"true\" id=\"last_modify_time\" name=\"修改时间\"/>\r\n</order>\r\n<where>\r\n<field id=\"assign_user\" method=\"=\" name=\"指派人\">$current_user$</field>\r\n<condition>and</condition>\r\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\r\n</where>\r\n</templateType>\r\n</query>', 'admin@sohu-rd.com', '2014-04-03 17:13:15', '1', '1', '1', null, '1');
INSERT INTO `filter` VALUES ('119891', '待跟踪', '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\r\n<query>\r\n<env>\r\n<current_user/>\r\n<timerange>current</timerange>\r\n<current_template_type/>\r\n</env>\r\n<templateType id=\"$current_template_type$\">\r\n<display>\r\n<field id=\"title\" name=\"标题\"/>\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field id=\"status_id\" name=\"状态\"/>\r\n<field id=\"create_user\" name=\"创建人\"/>\r\n<field id=\"create_time\" name=\"创建时间\"/>\r\n<field id=\"assign_user\" name=\"指派人\"/>\r\n<field id=\"last_modify_time\" name=\"修改时间\"/>\r\n</display>\r\n<order indent=\"1\">\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field desc=\"true\" id=\"last_modify_time\" name=\"修改时间\"/>\r\n</order>\r\n<where>\r\n<field id=\"create_user\" method=\"=\" name=\"创建人\">$current_user$</field>\r\n<condition>and</condition>\r\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\r\n</where>\r\n</templateType>\r\n</query>', 'admin@sohu-rd.com', '2014-04-03 17:13:43', '1', '1', '1', null, '1');
INSERT INTO `filter` VALUES ('119892', '已处理[未关闭]', '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<query>		\r\n<env>\r\n<current_user/>\r\n<timerange>history</timerange>\r\n<current_template_type/>\r\n</env>		\r\n<templateType id=\"$current_template_type$\">\r\n<display>\r\n<field id=\"title\" name=\"标题\"/>\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field id=\"status_id\" name=\"状态\"/>\r\n<field id=\"create_user\" name=\"创建人\"/>\r\n<field id=\"create_time\" name=\"创建时间\"/>\r\n<field id=\"assign_user\" name=\"指派人\"/>\r\n<field id=\"last_modify_time\" name=\"修改时间\"/>\r\n</display>\r\n<order indent=\"1\">\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field desc=\"true\" id=\"last_modify_time\" name=\"修改时间\"/>\r\n</order>\r\n<where>\r\n<field id=\"action_index\" method=\"&gt;\" name=\"执行序号\">1</field>\r\n<condition>and</condition>\r\n<field id=\"action_user\" method=\"=\" name=\"执行人\">$current_user$</field>\r\n<condition>and</condition>\r\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\r\n</where>\r\n</templateType>\r\n</query>\r\n\r\n', 'admin@sohu-rd.com', '2014-04-03 17:14:17', '1', '1', '1', null, '1');
INSERT INTO `filter` VALUES ('119893', '已处理[已关闭]', '<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<query>		\r\n<env>\r\n<current_user/>\r\n<timerange>history</timerange>\r\n<current_template_type/>\r\n</env>		\r\n<templateType id=\"$current_template_type$\">\r\n<display>\r\n<field id=\"title\" name=\"标题\"/>\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field id=\"status_id\" name=\"状态\"/>\r\n<field id=\"create_user\" name=\"创建人\"/>\r\n<field id=\"create_time\" name=\"创建时间\"/>\r\n<field id=\"assign_user\" name=\"指派人\"/>\r\n<field id=\"last_modify_time\" name=\"修改时间\"/>\r\n</display>\r\n<order indent=\"1\">\r\n<field id=\"node_id\" name=\"项目\"/>\r\n<field desc=\"true\" id=\"last_modify_time\" name=\"修改时间\"/>\r\n</order>\r\n<where>\r\n<field id=\"status_id\" method=\"=\" name=\"状态\" timerange=\"current\">[逻辑关闭]</field>\r\n<condition>and</condition>\r\n<field id=\"action_user\" method=\"=\" name=\"执行人\">$current_user$</field>\r\n<condition>and</condition>\r\n<field id=\"action_index\" method=\"&gt;\" name=\"执行序号\">1</field>\r\n</where>\r\n</templateType>\r\n</query>\r\n', 'admin@sohu-rd.com', '2014-04-03 17:14:41', '1', '1', '1', null, '1');
INSERT INTO `filter` VALUES ('626813', '线上重要--勿删！', '', 'liming@sogou-inc.com', '2013-12-19 15:21:56', '0', '0', '0', null, '1');

-- ----------------------------
-- Table structure for flow
-- ----------------------------
DROP TABLE IF EXISTS `flow`;
CREATE TABLE `flow` (
  `id` int(10) unsigned NOT NULL,
  `name` text NOT NULL,
  `xml` longtext NOT NULL,
  `is_valid` int(1) default '1' COMMENT '流程是否有效 1有效 0无效',
  `svg_code` longtext,
  `create_user` varchar(100) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of flow
-- ----------------------------
INSERT INTO `flow` VALUES ('744289', '缺陷流程模版', '<?xml version=\"1.0\" encoding=\"UTF-8\"?><flow><id>744289</id><name>缺陷流程模版</name><stats><stat><id>744302</id><name>己关闭</name></stat><stat><id>744301</id><name>己拒绝</name></stat><stat><id>744300</id><name>重新打开</name></stat><stat><id>744299</id><name>己解决</name></stat><stat><id>744298</id><name>接受/处理</name></stat><stat><id>744290</id><name>待处理</name></stat></stats><actions><action><id>744303</id><name>接受处理</name><startStatId>744290</startStatId><endStatId>744298</endStatId></action><action><id>744312</id><name>处理完毕，关闭</name><startStatId>744299</startStatId><endStatId>744302</endStatId></action><action><id>744310</id><name>重新打开</name><startStatId>744301</startStatId><endStatId>744300</endStatId></action><action><id>744311</id><name>直接关闭</name><startStatId>744301</startStatId><endStatId>744302</endStatId></action><action><id>744308</id><name>拒绝处理</name><startStatId>744290</startStatId><endStatId>744301</endStatId></action><action><id>744309</id><name>重新拒绝处理</name><startStatId>744300</startStatId><endStatId>744301</endStatId></action><action><id>744306</id><name>重新处理</name><startStatId>744300</startStatId><endStatId>744298</endStatId></action><action><id>744291</id><name>新建缺陷</name><startStatId/><endStatId>744290</endStatId></action><action><id>744307</id><name>置为拒绝处理</name><startStatId>744298</startStatId><endStatId>744301</endStatId></action><action><id>744304</id><name>置为解决</name><startStatId>744298</startStatId><endStatId>744299</endStatId></action><action><id>744305</id><name>验证不通过</name><startStatId>744299</startStatId><endStatId>744300</endStatId></action></actions><roles><role><id>744297</id><name>产品人员</name></role><role><id>744296</id><name>设计</name></role><role><id>744295</id><name>开发Leader</name></role><role><id>744294</id><name>测试Leader</name></role><role><id>744293</id><name>开发工程师</name></role><role><id>744292</id><name>测试工程师</name></role></roles><actionRoles><actionRole><actionId>744308</actionId><roleId>744293</roleId></actionRole><actionRole><actionId>744308</actionId><roleId>744295</roleId></actionRole><actionRole><actionId>48</actionId><roleId>82</roleId></actionRole><actionRole><actionId>744303</actionId><roleId>744295</roleId></actionRole><actionRole><actionId>744303</actionId><roleId>744293</roleId></actionRole><actionRole><actionId>744291</actionId><roleId>82</roleId></actionRole></actionRoles><rights></rights></flow>', '1', '{states:{rect_start:{type:\'start\',text:{text:\'开始\'}, attr:{ x:539, y:-17, width:80, height:39}},rect_744290:{type:\'state\',text:{text:\'待处理\'}, attr:{ x:531, y:124, width:100, height:50}},rect_744298:{type:\'state\',text:{text:\'接受/处理\'}, attr:{ x:316, y:213, width:100, height:50}},rect_744299:{type:\'state\',text:{text:\'己解决\'}, attr:{ x:320, y:377, width:100, height:50}},rect_744300:{type:\'state\',text:{text:\'重新打开\'}, attr:{ x:563, y:377, width:100, height:50}},rect_744301:{type:\'state\',text:{text:\'己拒绝\'}, attr:{ x:794, y:203, width:100, height:50}},rect_744302:{type:\'state\',text:{text:\'己关闭\'}, attr:{ x:619, y:556, width:100, height:50}}},paths:{path_744291:{from:\'rect_start\',to:\'rect_744290\', dots:[],text:{text:\'新建缺陷\'},textPos:{x:0,y:-10}},path_744303:{from:\'rect_744290\',to:\'rect_744298\', dots:[],text:{text:\'接受处理\'},textPos:{x:0,y:-10}},path_744304:{from:\'rect_744298\',to:\'rect_744299\', dots:[],text:{text:\'置为解决\'},textPos:{x:0,y:-10}},path_744305:{from:\'rect_744299\',to:\'rect_744300\', dots:[],text:{text:\'验证不通过\'},textPos:{x:0,y:-10}},path_744306:{from:\'rect_744300\',to:\'rect_744298\', dots:[],text:{text:\'重新处理\'},textPos:{x:0,y:-10}},path_744307:{from:\'rect_744298\',to:\'rect_744301\', dots:[],text:{text:\'置为拒绝处理\'},textPos:{x:0,y:-10}},path_744308:{from:\'rect_744290\',to:\'rect_744301\', dots:[],text:{text:\'拒绝处理\'},textPos:{x:0,y:-10}},path_744309:{from:\'rect_744300\',to:\'rect_744301\', dots:[{x:705,y:300}],text:{text:\'重新拒绝处理\'},textPos:{x:0,y:-10}},path_744310:{from:\'rect_744301\',to:\'rect_744300\', dots:[{x:761,y:365}],text:{text:\'重新打开\'},textPos:{x:0,y:-10}},path_744311:{from:\'rect_744301\',to:\'rect_744302\', dots:[{x:855,y:581}],text:{text:\'直接关闭\'},textPos:{x:0,y:-10}},path_744312:{from:\'rect_744299\',to:\'rect_744302\', dots:[{x:375,y:586}],text:{text:\'处理完毕，关闭\'},textPos:{x:0,y:-10}}},panZoom:-3,viewBox_x:-121.6,viewBox_y:-81.80000000000001,viewBox_width:1560,viewBox_height:832}', 'admin');

-- ----------------------------
-- Table structure for home_filter
-- ----------------------------
DROP TABLE IF EXISTS `home_filter`;
CREATE TABLE `home_filter` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(255) NOT NULL,
  `filter_id` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of home_filter
-- ----------------------------

-- ----------------------------
-- Table structure for script_import
-- ----------------------------
DROP TABLE IF EXISTS `script_import`;
CREATE TABLE `script_import` (
  `import_str` longtext
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of script_import
-- ----------------------------
INSERT INTO `script_import` VALUES ('import java.io.FileNotFoundException;\r\nimport java.net.URLEncoder;\r\nimport java.sql.Connection;\r\nimport java.sql.PreparedStatement;\r\nimport java.sql.ResultSet;\r\nimport java.sql.Timestamp;\r\nimport java.text.SimpleDateFormat;\r\nimport java.util.ArrayList;\r\nimport java.util.Arrays;\r\nimport java.util.Collections;\r\nimport java.util.Comparator;\r\nimport java.util.HashMap;\r\nimport java.util.HashSet;\r\nimport java.util.Iterator;\r\nimport java.util.LinkedHashMap;\r\nimport java.util.LinkedHashSet;\r\nimport java.util.List;\r\nimport java.util.Map;\r\nimport java.util.Set;\r\n\r\nimport com.sogou.qadev.cache.impl.FieldNameCache;\r\nimport com.sogou.qadev.service.cynthia.bean.Action;\r\nimport com.sogou.qadev.service.cynthia.bean.Attachment;\r\nimport com.sogou.qadev.service.cynthia.bean.ChangeLog;\r\nimport com.sogou.qadev.service.cynthia.bean.Data;\r\nimport com.sogou.qadev.service.cynthia.bean.DataAccessAction;\r\nimport com.sogou.qadev.service.cynthia.bean.Field;\r\nimport com.sogou.qadev.service.cynthia.bean.Field.DataType;\r\nimport com.sogou.qadev.service.cynthia.bean.Field.Type;\r\nimport com.sogou.qadev.service.cynthia.bean.Filter;\r\nimport com.sogou.qadev.service.cynthia.bean.Flow;\r\nimport com.sogou.qadev.service.cynthia.bean.IMEBugSolveStaticBean;\r\nimport com.sogou.qadev.service.cynthia.bean.Option;\r\nimport com.sogou.qadev.service.cynthia.bean.Option.Forbidden;\r\nimport com.sogou.qadev.service.cynthia.bean.SmtAssignUserBug;\r\nimport com.sogou.qadev.service.cynthia.bean.Stat;\r\nimport com.sogou.qadev.service.cynthia.bean.TechActualProcessStatistic;\r\nimport com.sogou.qadev.service.cynthia.bean.TechAssignStatistic;\r\nimport com.sogou.qadev.service.cynthia.bean.TechEfficiencyNotWorkTime;\r\nimport com.sogou.qadev.service.cynthia.bean.TechEfficiencyStatic;\r\nimport com.sogou.qadev.service.cynthia.bean.TechEfficiencyWorkTime;\r\nimport com.sogou.qadev.service.cynthia.bean.Template;\r\nimport com.sogou.qadev.service.cynthia.bean.UUID;\r\nimport com.sogou.qadev.service.cynthia.bean.UserInfo;\r\nimport com.sogou.qadev.service.cynthia.dao.DataAccessSessionMySQL;\r\nimport com.sogou.qadev.service.cynthia.dao.UserInfoAccessSessionMySQL;\r\nimport com.sogou.qadev.service.cynthia.factory.DataAccessFactory;\r\nimport com.sogou.qadev.service.cynthia.service.DataAccessSession;\r\nimport com.sogou.qadev.service.cynthia.service.DataAccessSession.ErrorCode;\r\nimport com.sogou.qadev.service.cynthia.service.DbPoolConnection;\r\nimport com.sogou.qadev.service.cynthia.service.MailSender;\r\nimport com.sogou.qadev.service.cynthia.service.TableRuleManager;\r\nimport com.sogou.qadev.service.cynthia.service.TechStatisticManager;\r\nimport com.sogou.qadev.service.cynthia.statistic.TechStatistic;\r\nimport com.sogou.qadev.service.cynthia.util.ConfigUtil;\r\nimport com.sogou.qadev.service.cynthia.util.CynthiaUtil;\r\nimport com.sogou.qadev.service.cynthia.util.Date;\r\nimport com.sohu.rd.td.util.reference.Pair;\r\nimport com.sohu.rd.td.util.xml.XMLUtil;');

-- ----------------------------
-- Table structure for script_new
-- ----------------------------
DROP TABLE IF EXISTS `script_new`;
CREATE TABLE `script_new` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `create_user` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `template_type_ids` text,
  `template_ids` text,
  `flow_ids` text,
  `node_ids` text,
  `begin_stat_ids` text,
  `end_stat_ids` text,
  `action_ids` text,
  `is_async` tinyint(1) NOT NULL,
  `is_before_commit` tinyint(1) NOT NULL,
  `is_after_success` tinyint(1) NOT NULL,
  `is_after_fail` tinyint(1) NOT NULL,
  `is_after_query` tinyint(1) NOT NULL,
  `xml` longtext NOT NULL,
  `is_valid` tinyint(1) NOT NULL default '1',
  `is_stat_edit` tinyint(1) NOT NULL,
  `is_action_edit` tinyint(1) NOT NULL,
  `allowed_template_ids` varchar(256) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of script_new
-- ----------------------------
INSERT INTO `script_new` VALUES ('722793', '邮件通知指派人', 'liming@sogou-inc.com', '2014-02-25 20:34:13', null, '191663,633090,659696,667291,653829,675815,676752,672821,2436,699968,3422,3143,129275,177132,1753,157271,5278,1555,4186,3242,5507,2655,4573,269804,4022,5153,273668,5762,193456,4364,721432,724570,733522,708559,727341,744218,744313', null, null, null, '191543,191542,191541,451062,451063,191540,191539,326239,191538,451149,191537,191536,451068,451069,451070,451064,191546,451065,191545,451066,191544,451067,191526,191527,191534,191535,191532,191533,191530,191531,268398,268397,191528,191529,201904,201905,326241,326240,633106,667651,667652,667653,667654,667655,653167,653166,653165,653164,653163,653129,653162,653161,653130,653176,653174,653175,653172,653173,653170,653168,653169,675670,675664,675667,675677,675663,675679,675675,673757,673756,673759,673758,673752,673751,673783,673782,673781,673780,673779,673778,673777,673776,673775,673772,673773,673770,673768,673769,673766,673767,673765,673762,673763,673760,673761,175290,550,551,175291,175288,548,175289,549,546,175294,547,175295,175292,544,545,175293,209768,554,555,552,553,175275,175274,175273,175272,134522,175277,175276,134479,212134,212133,134466,134467,533,165625,165626,535,534,136072,212149,541,540,136070,543,542,537,536,539,538,142688,134459,134463,165632,134462,165633,134461,134460,135271,135270,135272,175308,142686,176121,176124,175305,175304,175307,175306,175301,142679,175300,175303,175302,175297,175296,176118,175299,176119,175298,699992,699933,699928,699929,699930,699931,175248,829,831,830,175252,201978,834,179751,835,129446,832,833,838,839,836,837,842,843,175265,840,841,176117,175208,175268,129249,129248,129251,129250,129253,129252,129254,129262,130633,145507,145504,129246,129247,177123,177119,177118,177117,746,745,744,739,738,743,742,741,740,157260,158779,157256,157257,157258,157259,157255,157931,157450,157930,157929,157935,157604,157934,157235,157234,157233,157301,157300,157232,125341,125340,383,125342,382,395,149168,394,393,392,396,387,386,385,384,391,390,389,388,463,462,130903,461,460,703629,466,464,465,269809,269808,269811,269813,269812,269899,269814,269825,269824,269816,269819,269826,269818,269821,269820,269823,269822,353631,273664,353634,353633,273663,353632,1045,721427,721426,724577,724578,724579,724580,724581,724574,724572,734740,730362,730363,746366,659645,659647,727320,727321,722544,721425,659644,748996,286497,659643,744211,744214,744302,744301,744300,744299,744298,744290', null, '1', '0', '1', '0', '0', 'String downLoadUrl = ConfigUtil.getCynthiaWebRoot()+ \"attachment/download.jsp?method=download&id=\";\nString referUrl = ConfigUtil.getCynthiaWebRoot()+\"taskManagement.html?operation=read&taskid=\";\n\nTemplate template = das.queryTemplate(data.getTemplateId());\nif(template == null)\n	return;\n\nFlow flow = das.queryFlow(template.getFlowId());\nif (flow == null) {\n	return;\n}\n\nStat stat = flow.getStat(data.getStatusId());\nif(stat == null)\n	return;\n\n//判断指派人是否变化	\nboolean isAssignUserChange = false;\nChangeLog[] allChangeLogs = data.getChangeLogs();\nif (allChangeLogs == null || allChangeLogs.length ==0 ) {\n	return;\n}\n\nboolean isNewTask = true; //是否是新建任务\nif (flow.getAction(data.getActionId()) == null || flow.getAction(data.getActionId()).getBeginStatId() != null) {  //编辑时动作为null\n	isNewTask = false;\n}\n\nChangeLog lastChangeLog = allChangeLogs[allChangeLogs.length-1];\njava.util.Map map = lastChangeLog.getBaseValueMap();\nif (map.get(\"assignUser\") != null) {\n	isAssignUserChange = true;\n}\n                              \nif (!isAssignUserChange) {\n	return;\n}\n\nAction action = flow.getAction(data.getActionId());\nMailSender sender = new MailSender();\n\nsender.setHtml( true );\nsender.setSmtp( \"transport.mail.sogou-inc.com\" );\nsender.setEncode( \"GBK\" );\n\nsender.setFromUser(data.getString(\"logCreateUser\"));\n\nSet toUserSet = new HashSet();\nif (data.getAssignUsername() != null && data.getAssignUsername().length() > 0) {\n	toUserSet.add(data.getAssignUsername());\n}\n\nString[] toUser = (String[]) toUserSet.toArray(new String[toUserSet.size()]);\nif(toUser == null || toUser.length == 0)\n	return;\nsender.setToUsers(toUser);\n\n\n\nif (isAssignUserChange) {\n	sender.setSubject(\"[Cynthia]-[\"+data.getId().toString()+\"]有需求指派给您，请及时处理\");\n}else {\n	sender.setSubject(\"[Cynthia]-[\"+data.getId().toString()+\"]您名下的需求有变更，请关注\");\n}\n\nStringBuffer html = new StringBuffer();\nhtml.append(\"<html>\");\nhtml.append(\"<head>\");\nhtml.append(\"<meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=GBK\\\"/>\");\nhtml.append(\"<style type=\\\"text/css\\\">\");\nhtml.append(\"table{border:1px #E1E1E1 solid;}\");\nhtml.append(\"td{border:1px #E1E1E1 solid;padding:10px;}\");\nhtml.append(\".tdcolor{background-color:#fedcbd}\");\nhtml.append(\".tdcolor2{background-color:#84bf96}\");\nhtml.append(\"h3{color:red;margin-top:40px}\");\nhtml.append(\"h4{color:blue;}\");\nhtml.append(\"h5{margin-left:80px}\");\nhtml.append(\"th{border:1px #E1E1E1 solid;padding:10px;}\");\nhtml.append(\"tr {font-size: 15px; COLOR:#000000; background-color:#FFFFFF; font-family: Tahoma; text-align:left;}\");\nhtml.append(\"</style>\");\nhtml.append(\"</head>\");\nhtml.append(\"<body>\");\nhtml.append(\"<h4>基本信息</h4>\");\n\nString logActionUserName = data.getActionUser();\nUserInfo userInfo = das.queryUserInfoByUserName(logActionUserName);\nif (userInfo != null ) {\n	logActionUserName = userInfo.getNickName();\n}\nhtml.append(\"<table>\");\nhtml.append(\"<tr><td class=\\\"tdcolor\\\">\").append(\"标题\").append(data.getId().toString()).append(\"</td><td colspan=\\\"5\\\">\").append(\"<a href=\\\"\" + ConfigUtil.getCynthiaWebRoot() + \"taskManagement.html?operation=read&taskid=\" + data.getId().toString() + \"\\\">\" + com.sohu.rd.td.util.xml.XMLUtil.toSafeXMLString(data.getTitle()) +\"</a>\").append(\"</td></tr>\");\nhtml.append(\"<tr><td class=\\\"tdcolor\\\">\").append(\"执行动作\").append(\"</td><td>\").append((action == null ? \"编辑\" : com.sohu.rd.td.util.xml.XMLUtil.toSafeXMLString(action.getName()))).append(\"</td>\");\nhtml.append(\"<td class=\\\"tdcolor\\\">\").append(\"执行人\").append(\"</td><td>\").append(com.sohu.rd.td.util.xml.XMLUtil.toSafeXMLString(logActionUserName)).append(\"</td>\");\nhtml.append(\"<td class=\\\"tdcolor\\\">\").append(\"状态\").append(\"</td><td>\").append(com.sohu.rd.td.util.xml.XMLUtil.toSafeXMLString(stat.getName())).append(\"</td></tr>\");\nhtml.append(\"</table>\");\n\nhtml.append(\"<h4>字段变更信息</h4>\");\n\nhtml.append(\"<table>\");\n\nif (isNewTask) {\n	html.append(\"<tr>\").append(\"<th class=\\\"tdcolor2\\\">\").append(\"字段\").append(\"</th>\")\n.append(\"<th class=\\\"tdcolor2\\\">\").append(\"新建内容\").append(\"</th></tr>\");\n}else {\n	html.append(\"<tr>\").append(\"<th class=\\\"tdcolor2\\\">\").append(\"字段\").append(\"</th>\")\n.append(\"<th class=\\\"tdcolor2\\\">\").append(\"修改之后\").append(\"</th>\")\n.append(\"<th class=\\\"tdcolor2\\\">\").append(\"修改之前\").append(\"</th></tr>\");\n}\n\nMap baseValueMap = lastChangeLog.getBaseValueMap();\nIterator baseIterator = baseValueMap.entrySet().iterator();\nwhile(baseIterator.hasNext()){\n	Map.Entry entry = (Map.Entry)baseIterator.next();\n	\n	String fieldName = \"\";\nStringBuffer before = new StringBuffer();\nStringBuffer after = new StringBuffer();\n\nString key = entry.getKey().toString();\nPair pair = (Pair)entry.getValue();\nif (pair == null) \n	continue;\n\nif (key.equals(\"title\")) {\n	fieldName = \"标题\";\n	before.append(pair.getFirst()==null?\"\":pair.getFirst());\n	after.append(pair.getSecond()==null?\"\":pair.getSecond());\n}else if (key.equals(\"description\")) {\n	\n	fieldName = \"描述\";\n	\n	if (pair.getFirst() == null) {\n		before.append(\"\");\n	}else {\n		String content = pair.getFirst().toString();\n		before.append(content);\n	}\n	\n	if (pair.getSecond() == null) {\n		after.append(\"\");\n	}else {\n		String content = pair.getSecond().toString();\n		after.append(content);\n	}\n\n}else if (key.equals(\"assignUser\")) {\n	fieldName = \"指派人\";\n	if (pair.getFirst()==\"\" || pair.getFirst() == null) {\n		before.append(\"\");\n	}else {				\n		userInfo = das.queryUserInfoByUserName(pair.getFirst().toString());\n		before.append(userInfo == null ? pair.getFirst().toString() : userInfo.getNickName());	\n	}\n	\n	if (pair.getSecond()==\"\" || pair.getSecond() == null) {\n		after.append(\"\");\n	}else {\n		userInfo = das.queryUserInfoByUserName(pair.getSecond().toString());\n		after.append(userInfo == null ? pair.getSecond().toString() : userInfo.getNickName());	\n	}\n\n}else if (key.equals(\"statusId\")) {\n	fieldName = \"状态\";\n	if (pair.getFirst() == \"\" || pair.getFirst() == null) {\n		before.append(\"\");	\n	}else {\n		before.append(flow.getStat(DataAccessFactory.getInstance().createUUID(pair.getFirst().toString())).getName());	\n	}\n	if (pair.getSecond() == \"\" || pair.getSecond() == null) {\n		after.append(\"\");	\n	}else {\n		after.append(flow.getStat(DataAccessFactory.getInstance().createUUID(pair.getSecond().toString())).getName());	\n	}\n}\nif (isNewTask) \n	html.append(\"<tr><td class=\\\"tdcolor\\\">\").append(fieldName).append(\"</td><td>\").append(after.toString()).append(\"</td></tr>\");\nelse \n	html.append(\"<tr><td class=\\\"tdcolor\\\">\").append(fieldName).append(\"</td><td>\").append(after.toString()).append(\"</td><td>\").append(before.toString()).append(\"</td></tr>\");\n}\n\n\nMap extValueMap = lastChangeLog.getExtValueMap();\nIterator extIterator = extValueMap.entrySet().iterator();\nwhile(extIterator.hasNext()){\n	Map.Entry entry = (Map.Entry)extIterator.next();\n	UUID keyUUID = (UUID)entry.getKey();\n	Pair pair = (Pair)entry.getValue();\n	if (pair == null || keyUUID == null) \n		continue;\n	\n	Field tmpField = das.queryField(keyUUID);\n	String fieldName = \"\";\nStringBuffer before = new StringBuffer();\nStringBuffer after = new StringBuffer();\n\nif (tmpField == null) {\n	continue;\n}\nfieldName = tmpField.getName();\n\nif (pair.getFirst() != null) {\n\n		if (tmpField.getType() == Type.t_selection) {\n			String[] optionIdStrArray = pair.getFirst().toString().split(\"\\\\,\");\n			for(String optionIdStr : optionIdStrArray){\n				UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);\n				Option option = tmpField.getOption(optionId);\n				if(option == null){\n					continue;\n				}\n				if(before.length() > 0){\n					before.append(\",\");\n				}\n				before.append(option.getName());\n			}\n		}else if (tmpField.getType() == Type.t_reference) {\n			String[] referIdArray = pair.getFirst().toString().split(\"\\\\,\");\n			for(String referId : referIdArray){\n				Data refer = das.queryData(DataAccessFactory.getInstance().createUUID(referId));\n				if (refer != null) {\n					before.append(before.length() > 0 ? \",\" :\"\").append(\"<a href=\\\"\").append(referUrl+refer.getId().toString()).append(\"\\\">\").append(refer.getTitle()).append(\"</a>\");\n				}\n			}\n			\n		}else if (tmpField.getType() == Type.t_attachment) {\n\n			UUID[] attachIdArray = (UUID[])pair.getFirst();\n			Attachment[] attachArray = das.queryAttachments(attachIdArray, false);\n			for(Attachment attach : attachArray){\n				if(before.length() > 0){\n					before.append(\"<br />\");\n				}\n				before.append(\"<a href=\\\"\").append(downLoadUrl+attach.getId().toString()).append(\"\\\">\").append(attach.getName()).append(\"</a>\");\n			}\n		}else {\n			String content = pair.getFirst().toString();\n			before.append(content);\n		}\n} // end for if (pair.getFirst() != null) {\n\nif (pair.getSecond() != null) {\n		if (tmpField.getType() == Type.t_selection) {\n			String[] optionIdStrArray = pair.getSecond().toString().split(\"\\\\,\");\n			for(String optionIdStr : optionIdStrArray){\n				UUID optionId = DataAccessFactory.getInstance().createUUID(optionIdStr);\n				Option option = tmpField.getOption(optionId);\n				if(option == null){\n					continue;\n				}\n				if(after.length() > 0){\n					after.append(\",\");\n				}\n				after.append(option.getName());\n			}\n		}else if (tmpField.getType() == Type.t_reference) {\n			\n			String[] referIdArray = pair.getSecond().toString().split(\"\\\\,\");\n			for(String referId : referIdArray){\n				Data refer = das.queryData(DataAccessFactory.getInstance().createUUID(referId));\n				if (refer != null) {\n					after.append(after.length() > 0 ? \",\" :\"\").append(\"<a href=\\\"\").append(referUrl+refer.getId().toString()).append(\"\\\">\").append(refer.getTitle()).append(\"</a>\");\n				}\n			}\n			\n		}else if (tmpField.getType() == Type.t_attachment) {\n			UUID[] attachIdArray = (UUID[])pair.getSecond();\n			\n			Attachment[] attachArray = das.queryAttachments(attachIdArray, false);\n			\n			for(Attachment attach : attachArray){\n				if(after.length() > 0){\n					after.append(\"<br />\");\n				}\n				after.append(\"<a href=\\\"\").append(downLoadUrl+attach.getId().toString()).append(\"\\\">\").append(attach.getName()).append(\"</a>\");\n			}\n		}else {\n			String content = pair.getSecond().toString();\n			after.append(content);\n		}\n} // end for if (pair.getSecond() != null) {\n\nif (isNewTask) \n	html.append(\"<tr><td class=\\\"tdcolor\\\">\").append(fieldName).append(\"</td><td>\").append(after.toString()).append(\"</td></tr>\");\nelse \n	html.append(\"<tr><td class=\\\"tdcolor\\\">\").append(fieldName).append(\"</td><td>\").append(after.toString()).append(\"</td><td>\").append(before.toString()).append(\"</td></tr>\");\n}\n\nhtml.append(\"</table>\");\nhtml.append(\"</body>\");\nhtml.append(\"</html>\");\n\nString sendHtml =  html.toString();\nsendHtml = sendHtml.replace(\"</tr>\", \"</tr>\\n\");\nsender.setContent(sendHtml);\nsender.sendHtmlEx(\"GBK\");', '1', '1', '0', '*');

-- ----------------------------
-- Table structure for system_set
-- ----------------------------
DROP TABLE IF EXISTS `system_set`;
CREATE TABLE `system_set` (
  `set_name` varchar(255) NOT NULL,
  `value` longtext,
  PRIMARY KEY  (`set_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of system_set
-- ----------------------------
INSERT INTO `system_set` VALUES ('system', '{\"openRight\":\"true\"}');

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` int(10) NOT NULL auto_increment,
  `name` varchar(100) default NULL,
  `user_name` varchar(50) default NULL,
  `color` text,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=109 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tag
-- ----------------------------

-- ----------------------------
-- Table structure for tag_data
-- ----------------------------
DROP TABLE IF EXISTS `tag_data`;
CREATE TABLE `tag_data` (
  `tag_id` int(10) NOT NULL,
  `tag_data` int(10) NOT NULL default '0',
  `filter_id` int(15) default NULL,
  PRIMARY KEY  (`tag_id`,`tag_data`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of tag_data
-- ----------------------------

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` int(10) unsigned NOT NULL,
  `name` text NOT NULL,
  `xml` longtext,
  `is_valid` int(1) default '1' COMMENT '表单是否有效 1有效 0无效',
  `is_new` int(1) NOT NULL default '1',
  `layout_xml` longtext,
  `create_user` varchar(100) default NULL COMMENT '表单负责人',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of template
-- ----------------------------
INSERT INTO `template` VALUES ('744313', '缺陷表单模版', null, '1', '1', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?><template><id>744313</id><templateTypeId>1</templateTypeId><name>缺陷表单模版</name><description></description><flowId>744289</flowId><layout><rows><row><column><field><id>744314</id><name>优先级</name><description>缺陷优先级程度</description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744340</id><name>低</name><description>低</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744341</id><name>中</name><description>中</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744342</id><name>高</name><description>高</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744303</actionId><actionId>744291</actionId></actionIds></field><field><id>744315</id><name>严重程度</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744343</id><name>普通</name><description>普通</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744344</id><name>严重</name><description>严重</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744317</id><name>附件信息</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_attachment</type><dataType></dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field></column><column><field><id>744316</id><name>发现版本</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_multiple</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744318</id><name>缺陷类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744321</id><name>测试方式</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744322</id><name>手工测试</name><description>手工测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744323</id><name>自动化测试</name><description>自动化测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744319</id><name>测试类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744324</id><name>功能测试</name><description>功能测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744325</id><name>需求检查</name><description>需求检查</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744326</id><name>代码检查</name><description>代码检查</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option><option><id>744327</id><name>单元测试</name><description>单元测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>4</indexOrder></option><option><id>744328</id><name>冒烟测试</name><description>冒烟测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>5</indexOrder></option><option><id>744329</id><name>系统测试</name><description>系统测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>6</indexOrder></option><option><id>744330</id><name>随机测试</name><description>随机测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>7</indexOrder></option><option><id>744331</id><name>回归测试</name><description>回归测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>8</indexOrder></option><option><id>744332</id><name>客户反馈</name><description>客户反馈</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>9</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field></column><column><field><id>744320</id><name>测试阶段</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744333</id><name>单元测试</name><description>单元测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744334</id><name>集成测试</name><description>集成测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744335</id><name>系统测试</name><description>系统测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option><option><id>744336</id><name>运营测试</name><description>运营测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>4</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744337</id><name>关联bug</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field><field><id>744338</id><name>其它信息</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_input</type><dataType>dt_text</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds/><actionIds><actionId>744291</actionId></actionIds></field></column></row></rows></layout></template>', null);

-- ----------------------------
-- Table structure for template_admin_user
-- ----------------------------
DROP TABLE IF EXISTS `template_admin_user`;
CREATE TABLE `template_admin_user` (
  `template_id` int(20) NOT NULL,
  `admin_user` varchar(300) NOT NULL COMMENT '表单负责人',
  PRIMARY KEY  (`template_id`,`admin_user`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of template_admin_user
-- ----------------------------
INSERT INTO `template_admin_user` VALUES ('744313', 'admin');

-- ----------------------------
-- Table structure for template_operate_log
-- ----------------------------
DROP TABLE IF EXISTS `template_operate_log`;
CREATE TABLE `template_operate_log` (
  `id` int(20) NOT NULL auto_increment,
  `template_id` varchar(20) NOT NULL COMMENT '表单id',
  `field_id` varchar(20) default NULL COMMENT '修改的字段id',
  `field_name` varchar(50) default NULL COMMENT '字段名字',
  `operate_type` varchar(10) default NULL,
  `create_time` varchar(50) default NULL,
  `create_user` varchar(50) default NULL COMMENT '修改人邮箱',
  `before_xml` longtext,
  `after_xml` longtext,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=1174 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of template_operate_log
-- ----------------------------
INSERT INTO `template_operate_log` VALUES ('1172', '744313', '744314', '优先级', 'modify', '2014-05-04 11:58:17.0', 'admin', '<field><id>744314</id><name>优先级</name><description>缺陷优先级程度</description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744303</actionId><actionId>744291</actionId></actionIds></field>', '<field><id>744314</id><name>优先级</name><description>缺陷优先级程度</description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744340</id><name>低</name><description>低</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744341</id><name>中</name><description>中</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744342</id><name>高</name><description>高</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744303</actionId><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1173', '744313', '744315', '严重程度', 'modify', '2014-05-04 11:58:52.0', 'admin', '<field><id>744315</id><name>严重程度</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>', '<field><id>744315</id><name>严重程度</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744343</id><name>普通</name><description>普通</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744344</id><name>严重</name><description>严重</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1159', '744313', '744314', '优先级', 'add', '2014-05-04 11:05:13.0', 'admin', '', '<field><id>744314</id><name>优先级</name><description>缺陷优先级程度</description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744303</actionId><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1160', '744313', '744315', '严重程度', 'add', '2014-05-04 11:05:50.0', 'admin', '', '<field><id>744315</id><name>严重程度</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_1</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1161', '744313', '744316', '发现版本', 'add', '2014-05-04 11:06:41.0', 'admin', '', '<field><id>744316</id><name>发现版本</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_multiple</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1162', '744313', '744317', '附件信息', 'add', '2014-05-04 11:07:19.0', 'admin', '', '<field><id>744317</id><name>附件信息</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_attachment</type><dataType></dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1163', '744313', '744318', '缺陷类型', 'add', '2014-05-04 11:08:02.0', 'admin', '', '<field><id>744318</id><name>缺陷类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1164', '744313', '744319', '测试类型', 'add', '2014-05-04 11:08:29.0', 'admin', '', '<field><id>744319</id><name>测试类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1165', '744313', '744320', '测试阶段', 'add', '2014-05-04 11:08:51.0', 'admin', '', '<field><id>744320</id><name>测试阶段</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1166', '744313', '744321', '测试方式', 'add', '2014-05-04 11:10:14.0', 'admin', '', '<field><id>744321</id><name>测试方式</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1167', '744313', '744321', '测试方式', 'modify', '2014-05-04 11:10:33.0', 'admin', '<field><id>744321</id><name>测试方式</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>', '<field><id>744321</id><name>测试方式</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744322</id><name>手工测试</name><description>手工测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744323</id><name>自动化测试</name><description>自动化测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1168', '744313', '744319', '测试类型', 'modify', '2014-05-04 11:12:18.0', 'admin', '<field><id>744319</id><name>测试类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>', '<field><id>744319</id><name>测试类型</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744324</id><name>功能测试</name><description>功能测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744325</id><name>需求检查</name><description>需求检查</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744326</id><name>代码检查</name><description>代码检查</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option><option><id>744327</id><name>单元测试</name><description>单元测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>4</indexOrder></option><option><id>744328</id><name>冒烟测试</name><description>冒烟测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>5</indexOrder></option><option><id>744329</id><name>系统测试</name><description>系统测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>6</indexOrder></option><option><id>744330</id><name>随机测试</name><description>随机测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>7</indexOrder></option><option><id>744331</id><name>回归测试</name><description>回归测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>8</indexOrder></option><option><id>744332</id><name>客户反馈</name><description>客户反馈</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>9</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1169', '744313', '744320', '测试阶段', 'modify', '2014-05-04 11:13:12.0', 'admin', '<field><id>744320</id><name>测试阶段</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>', '<field><id>744320</id><name>测试阶段</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options><option><id>744333</id><name>单元测试</name><description>单元测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>1</indexOrder></option><option><id>744334</id><name>集成测试</name><description>集成测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>2</indexOrder></option><option><id>744335</id><name>系统测试</name><description>系统测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>3</indexOrder></option><option><id>744336</id><name>运营测试</name><description>运营测试</description><controlOptionId></controlOptionId><forbidden>f_permit</forbidden><indexOrder>4</indexOrder></option></options><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1170', '744313', '744337', '关联bug', 'add', '2014-05-04 11:14:21.0', 'admin', '', '<field><id>744337</id><name>关联bug</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_selection</type><dataType>dt_single</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds><controlActionId>744291_82_0</controlActionId></controlActionIds><actionIds><actionId>744291</actionId></actionIds></field>');
INSERT INTO `template_operate_log` VALUES ('1171', '744313', '744338', '其它信息', 'add', '2014-05-04 11:14:53.0', 'admin', '', '<field><id>744338</id><name>其它信息</name><description></description><fieldTip></fieldTip><fieldSize>1</fieldSize><type>t_input</type><dataType>dt_text</dataType><controlFieldId></controlFieldId><x>0</x><y>0</y><width>0</width><height>0</height><align></align><valign></valign><defaultValue></defaultValue><options/><controlOptionIds/><controlHiddenFieldId/><controlHiddenFields/><controlHiddenStates/><controlRoleIds/><controlActionIds/><actionIds><actionId>744291</actionId></actionIds></field>');

-- ----------------------------
-- Table structure for template_type
-- ----------------------------
DROP TABLE IF EXISTS `template_type`;
CREATE TABLE `template_type` (
  `id` int(11) NOT NULL auto_increment,
  `name` varchar(64) NOT NULL,
  `description` varchar(256) default NULL,
  `icon` varchar(64) default NULL,
  `displayIndex` int(11) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of template_type
-- ----------------------------
INSERT INTO `template_type` VALUES ('1', '缺陷', null, 'Bug.gif', '1025');
INSERT INTO `template_type` VALUES ('2', '任务', null, 'Task.gif', '1026');

-- ----------------------------
-- Table structure for timer
-- ----------------------------
DROP TABLE IF EXISTS `timer`;
CREATE TABLE `timer` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `action_id` int(10) unsigned NOT NULL,
  `year` varchar(512) default NULL,
  `month` varchar(512) default NULL,
  `week` varchar(512) default NULL,
  `day` varchar(512) default NULL,
  `hour` varchar(512) default NULL,
  `minute` varchar(512) default NULL,
  `second` varchar(512) default NULL,
  `create_user` varchar(128) NOT NULL,
  `create_time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `action_param` longtext NOT NULL,
  `is_start` tinyint(1) NOT NULL,
  `filter_id` int(10) unsigned default NULL,
  `statisticer_id` int(10) unsigned default NULL,
  `retry_account` int(10) unsigned NOT NULL,
  `retry_delay` int(10) unsigned NOT NULL,
  `is_send_null` tinyint(1) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `FK_timer_1` USING BTREE (`action_id`),
  KEY `FK_timer_2` USING BTREE (`filter_id`),
  KEY `FK_timer_4` USING BTREE (`statisticer_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of timer
-- ----------------------------

-- ----------------------------
-- Table structure for timer_action
-- ----------------------------
DROP TABLE IF EXISTS `timer_action`;
CREATE TABLE `timer_action` (
  `id` int(10) unsigned NOT NULL,
  `name` varchar(128) NOT NULL,
  `class_name` varchar(128) NOT NULL,
  `method` varchar(128) NOT NULL,
  `param` longtext NOT NULL,
  `is_public` int(1) default '0',
  `create_user` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of timer_action
-- ----------------------------
INSERT INTO `timer_action` VALUES ('120846', '发列表邮件', 'com.sogou.qadev.service.cynthia.bean.SendMail', 'sendMail', '<?xml version=\'1.0\' encoding=\'UTF-8\'?>\r\n<action name=\"发列表邮件\" id=\"120846\">\r\n	<params>\r\n		<param name=\"title\" displayName=\"邮件标题\" type=\"text\" value=\"[Cynthia]\"/>\r\n		<param name=\"mailList\" displayName=\"收件人列表\" type=\"textarea\" />\r\n		<param name=\"ccMailList\" displayName=\"抄送列表\" type=\"textarea\" />\r\n		<param name=\"bccMailList\" displayName=\"密件抄送列表\" type=\"textarea\" />\r\n	</params>\r\n</action>', '0', null);
INSERT INTO `timer_action` VALUES ('762390', '待处理', 'com.sogou.qadev.service.cynthia.service.StatisticerManager', 'execute', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>\n<type>public</type>\n<name>待处理</name>\n<templateId></templateId>\n<timeRange>\n	<timeType></timeType>\n	<startTime></startTime>\n	<endTime></endTime>\n</timeRange>\n<task><taskId></taskId><taskFieldId></taskFieldId></task>\n<person><roleId></roleId><roleActionIds></roleActionIds></person>\n<model><modelfieldId></modelfieldId></model>\n<queryCondition>\n<where>\n<field id=\"assign_user\" method=\"=\" name=\"指派人\">$current_user$</field>\n<condition>and</condition>\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\n</where>\n</queryCondition>\n<stats/>\n<mailTime></mailTime>\n<reciever/>\n<graph>pie</graph>\n</root>', '1', null);
INSERT INTO `timer_action` VALUES ('762410', '待跟踪', 'com.sogou.qadev.service.cynthia.service.StatisticerManager', 'execute', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>\n<type>public</type>\n<name>待跟踪</name>\n<templateId></templateId>\n<timeRange>\n	<timeType></timeType>\n	<startTime></startTime>\n	<endTime></endTime>\n</timeRange>\n<task><taskId></taskId><taskFieldId></taskFieldId></task>\n<person><roleId></roleId><roleActionIds></roleActionIds></person>\n<model><modelfieldId></modelfieldId></model>\n<queryCondition>\n<where>\n<field id=\"create_user\" method=\"=\" name=\"创建人\">$current_user$</field>\n<condition>and</condition>\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\n</where>\n</queryCondition>\n<stats/>\n<mailTime></mailTime>\n<reciever/>\n<graph>pie</graph>\n</root>', '1', null);
INSERT INTO `timer_action` VALUES ('762420', '己处理【未关闭】', 'com.sogou.qadev.service.cynthia.service.StatisticerManager', 'execute', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>\n<type>public</type>\n<name>己处理[未关闭]</name>\n<templateId></templateId>\n<timeRange>\n	<timeType></timeType>\n	<startTime></startTime>\n	<endTime></endTime>\n</timeRange>\n<task><taskId></taskId><taskFieldId></taskFieldId></task>\n<person><roleId></roleId><roleActionIds></roleActionIds></person>\n<model><modelfieldId></modelfieldId></model>\n<queryCondition>\n<where>\n<field id=\"action_index\" method=\"&gt;\" name=\"执行序号\">1</field>\n<condition>and</condition>\n<field id=\"action_user\" method=\"=\" name=\"执行人\">$current_user$</field>\n<condition>and</condition>\n<field id=\"status_id\" method=\"!=\" name=\"状态\">[逻辑关闭]</field>\n</where>\n</queryCondition>\n<stats/>\n<mailTime></mailTime>\n<reciever/>\n<graph>pie</graph>\n</root>', '1', null);
INSERT INTO `timer_action` VALUES ('762430', '己处理【己关闭】', 'com.sogou.qadev.service.cynthia.service.StatisticerManager', 'execute', '<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<root>\n<type>public</type>\n<name>己处理[己关闭]</name>\n<templateId></templateId>\n<timeRange>\n	<timeType></timeType>\n	<startTime></startTime>\n	<endTime></endTime>\n</timeRange>\n<task><taskId></taskId><taskFieldId></taskFieldId></task>\n<person><roleId></roleId><roleActionIds></roleActionIds></person>\n<model><modelfieldId></modelfieldId></model>\n<queryCondition>\n<where>\n<field id=\"status_id\" method=\"=\" name=\"状态\" timerange=\"current\">[逻辑关闭]</field>\n<condition>and</condition>\n<field id=\"action_user\" method=\"=\" name=\"执行人\">$current_user$</field>\n<condition>and</condition>\n<field id=\"action_index\" method=\"&gt;\" name=\"执行序号\">1</field>\n</where>\n</queryCondition>\n<stats/>\n<mailTime></mailTime>\n<reciever/>\n<graph>pie</graph>\n</root>', '1', null);

-- ----------------------------
-- Table structure for tree
-- ----------------------------
DROP TABLE IF EXISTS `tree`;
CREATE TABLE `tree` (
  `id` int(11) NOT NULL auto_increment,
  `parent_id` int(11) NOT NULL,
  `position` int(11) NOT NULL,
  `user_name` varchar(255) default NULL,
  `title` varchar(255) default NULL,
  `filters` varchar(1024) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=732 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of tree
-- ----------------------------
INSERT INTO `tree` VALUES ('6', '1', '0', null, '我的过滤器(右击新建)', null);

-- ----------------------------
-- Table structure for user_default_template
-- ----------------------------
DROP TABLE IF EXISTS `user_default_template`;
CREATE TABLE `user_default_template` (
  `user_name` varchar(256) character set utf8 NOT NULL,
  `template_id` varchar(20) character set utf8 NOT NULL,
  PRIMARY KEY  (`user_name`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of user_default_template
-- ----------------------------

-- ----------------------------
-- Table structure for user_default_value
-- ----------------------------
DROP TABLE IF EXISTS `user_default_value`;
CREATE TABLE `user_default_value` (
  `user_name` varchar(100) NOT NULL COMMENT '用户邮箱',
  `template_id` int(50) NOT NULL COMMENT '表单id',
  `default_value_json` longtext COMMENT '所有默认值json',
  PRIMARY KEY  (`user_name`,`template_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_default_value
-- ----------------------------

-- ----------------------------
-- Table structure for user_focus_filter
-- ----------------------------
DROP TABLE IF EXISTS `user_focus_filter`;
CREATE TABLE `user_focus_filter` (
  `user` varchar(128) NOT NULL,
  `filter_id` int(10) unsigned NOT NULL,
  PRIMARY KEY  (`user`,`filter_id`),
  KEY `FK_user_focus_filter_1` USING BTREE (`filter_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_focus_filter
-- ----------------------------

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL auto_increment,
  `user_name` varchar(256) character set latin1 collate latin1_general_cs NOT NULL,
  `nick_name` varchar(256) default NULL,
  `password` longtext NOT NULL COMMENT '用户密码',
  `user_role` varchar(15) NOT NULL default '0' COMMENT '用户角色',
  `user_stat` varchar(15) NOT NULL default '2' COMMENT '用户状态',
  `last_login_time` timestamp NULL default NULL,
  `create_time` timestamp NULL default NULL,
  `pic_id` varchar(50) default NULL COMMENT '用户头像id对应attachment',
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=750737 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_info
-- ----------------------------
INSERT INTO `user_info` VALUES ('750732', 'admin', 'admin', '21232f297a57a5a743894a0e4a801fc3', 'super_admin', 'normal', '2014-05-05 00:00:00', '2014-05-04 15:09:20', '');

-- ----------------------------
-- Table structure for user_new_data
-- ----------------------------
DROP TABLE IF EXISTS `user_new_data`;
CREATE TABLE `user_new_data` (
  `id` int(50) NOT NULL auto_increment,
  `filter_id` varchar(20) NOT NULL,
  `user` varchar(50) NOT NULL,
  `old_id` varchar(20) NOT NULL,
  PRIMARY KEY  (`id`),
  KEY `old_id_index` USING BTREE (`old_id`)
) ENGINE=MyISAM AUTO_INCREMENT=336657 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of user_new_data
-- ----------------------------

-- ----------------------------
-- Table structure for uuid
-- ----------------------------
DROP TABLE IF EXISTS `uuid`;
CREATE TABLE `uuid` (
  `id` int(10) unsigned NOT NULL auto_increment,
  `type` varchar(4) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=744728 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of uuid
-- ----------------------------
INSERT INTO `uuid` VALUES ('47', 'ACTI');
INSERT INTO `uuid` VALUES ('48', 'ACTI');
INSERT INTO `uuid` VALUES ('119695', 'FILT');
INSERT INTO `uuid` VALUES ('119891', 'FILT');
INSERT INTO `uuid` VALUES ('119892', 'FILT');
INSERT INTO `uuid` VALUES ('119893', 'FILT');
INSERT INTO `uuid` VALUES ('120846', 'TIAC');
INSERT INTO `uuid` VALUES ('626813', 'FILT');
INSERT INTO `uuid` VALUES ('722793', 'SCRI');
INSERT INTO `uuid` VALUES ('744289', 'FLOW');
INSERT INTO `uuid` VALUES ('744313', 'TEMP');
