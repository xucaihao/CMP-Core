/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50524
Source Host           : localhost:3306
Source Database       : cloudmp

Target Server Type    : MYSQL
Target Server Version : 50524
File Encoding         : 65001

Date: 2018-04-09 20:44:16
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_cloud
-- ----------------------------
DROP TABLE IF EXISTS `tbl_cloud`;
CREATE TABLE `tbl_cloud` (
  `cloud_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '云ID',
  `cloud_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云名称',
  `cloud_type` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云类型值',
  `visibility` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '可见类型',
  `cloud_protocol` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '网络协议',
  `cloud_ip` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT 'ip地址',
  `cloud_port` varchar(10) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '端口号',
  `status` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云状态',
  `description` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '描述',
  PRIMARY KEY (`cloud_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tbl_cloud
-- ----------------------------
INSERT INTO `tbl_cloud` VALUES ('1', '阿里云', 'ali-cloud', 'PUBLIC', 'default', 'default', 'default', 'active', 'alicloud');
INSERT INTO `tbl_cloud` VALUES ('2', '腾讯云', 'tencent-cloud', 'PUBLIC', 'default', 'default', 'default', 'active', 'tencent-cloud');
INSERT INTO `tbl_cloud` VALUES ('43634264-196e-47f4-a673-3cdb48d9b780', 'ali3', 'ali-cloud', 'PUBLIC', 'defalut', 'defalut', 'defalut', 'active', 'test');

-- ----------------------------
-- Table structure for tbl_cloud_type
-- ----------------------------
DROP TABLE IF EXISTS `tbl_cloud_type`;
CREATE TABLE `tbl_cloud_type` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '云类型ID',
  `type_name` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云类型名',
  `type_value` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云类型值',
  `disable` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '启用状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tbl_cloud_type
-- ----------------------------
INSERT INTO `tbl_cloud_type` VALUES ('1', '阿里云', 'ali-cloud', '1');
INSERT INTO `tbl_cloud_type` VALUES ('2', '腾讯云', 'tencent-cloud', '0');

-- ----------------------------
-- Table structure for tbl_user
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user` (
  `user_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户ID',
  `user_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户登录名',
  `password` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密码',
  `role_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '角色',
  `token` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '密钥',
  `phone` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '电话号码',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '邮箱',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tbl_user
-- ----------------------------
INSERT INTO `tbl_user` VALUES ('00000000-00000000-0000000000000000', 'admin', 'cmproot', 'MANAGER', 'name: admin password: cmproot', '18030420512', '18030420512@163.com');
INSERT INTO `tbl_user` VALUES ('be59d9c8-cf9f-411c-a054-8df3b65152d4', 'xuhao', 'xuhao', 'USER', 'name: xuhao password: xuhao', '17830420512', '467046520@qq.com');

-- ----------------------------
-- Table structure for tbl_user_mapping
-- ----------------------------
DROP TABLE IF EXISTS `tbl_user_mapping`;
CREATE TABLE `tbl_user_mapping` (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '' COMMENT '用户映射id',
  `cmp_user_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户id',
  `cmp_user_name` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '用户登录名',
  `access_key` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云accessKey',
  `auth_info` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云上用户认证信息',
  `cloud_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL COMMENT '云id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of tbl_user_mapping
-- ----------------------------
