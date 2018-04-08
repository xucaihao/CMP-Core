/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50524
Source Host           : localhost:3306
Source Database       : cloudmp

Target Server Type    : MYSQL
Target Server Version : 50524
File Encoding         : 65001

Date: 2018-04-07 18:42:28
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tbl_cloud
-- ----------------------------
DROP TABLE IF EXISTS `tbl_cloud`;
CREATE TABLE `tbl_cloud` (
  `cloud_id` varchar(36) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '云ID',
  `cloud_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL COMMENT '云名称',
  `cloud_type` varchar(20) CHARACTER SET utf8 NOT NULL COMMENT '云类型值',
  `visibility` varchar(36) CHARACTER SET utf8 NOT NULL,
  `cloud_protocol` varchar(20) CHARACTER SET utf8 NOT NULL,
  `cloud_ip` varchar(36) CHARACTER SET utf8 NOT NULL,
  `cloud_port` varchar(10) CHARACTER SET utf8 NOT NULL,
  `status` varchar(36) CHARACTER SET utf8 NOT NULL,
  `description` varchar(100) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`cloud_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `id` varchar(36) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '云ID',
  `type_name` varchar(20) CHARACTER SET utf8 NOT NULL COMMENT '云类型值',
  `type_value` varchar(36) CHARACTER SET utf8 NOT NULL,
  `disable` varchar(36) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `user_id` varchar(36) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '云ID',
  `user_name` varchar(50) CHARACTER SET utf8 NOT NULL COMMENT '云名称',
  `password` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '云类型值',
  `role_name` varchar(36) CHARACTER SET utf8 NOT NULL,
  `token` varchar(100) CHARACTER SET utf8 NOT NULL,
  `phone` varchar(36) CHARACTER SET utf8 NOT NULL,
  `email` varchar(100) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  `id` varchar(36) CHARACTER SET utf8 NOT NULL DEFAULT '' COMMENT '云ID',
  `cmp_user_id` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '云名称',
  `cmp_user_name` varchar(36) CHARACTER SET utf8 NOT NULL COMMENT '云类型值',
  `access_key` varchar(36) CHARACTER SET utf8 NOT NULL,
  `auth_info` varchar(100) CHARACTER SET utf8 NOT NULL,
  `cloud_id` varchar(36) CHARACTER SET utf8 NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of tbl_user_mapping
-- ----------------------------
