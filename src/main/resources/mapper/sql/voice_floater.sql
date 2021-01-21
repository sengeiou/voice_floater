/*
Navicat MySQL Data Transfer

Source Server         : 112.74.106.83
Source Server Version : 50649
Source Host           : 112.74.106.83:3306
Source Database       : voice_floater

Target Server Type    : MYSQL
Target Server Version : 50649
File Encoding         : 65001

Date: 2021-01-21 15:28:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for message
-- ----------------------------
DROP TABLE IF EXISTS `message`;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_from_id` int(11) DEFAULT NULL COMMENT '消息主体',
  `user_to_id` int(11) DEFAULT NULL,
  `recording_id` int(11) DEFAULT NULL COMMENT '被回复的漂流瓶id',
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息内容',
  `queue` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息所属队列名',
  `send_time` datetime DEFAULT NULL COMMENT '消息发送时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `permisstion_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限名',
  `title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目录标题',
  `index` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限路径',
  `type` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '类型（menu or button）',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '目录图标',
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '权限描述',
  `parent_id` int(11) DEFAULT NULL COMMENT '父权限id',
  `available` tinyint(1) DEFAULT '1' COMMENT '是否可用（1：可用 0：不可用）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for queue
-- ----------------------------
DROP TABLE IF EXISTS `queue`;
CREATE TABLE `queue` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `queue_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '队列名',
  `user_from_id` int(11) DEFAULT NULL COMMENT '如果是null则表示是系统',
  `user_to_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for recording
-- ----------------------------
DROP TABLE IF EXISTS `recording`;
CREATE TABLE `recording` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) DEFAULT NULL COMMENT '录音所属用户id',
  `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '音频文件所在地址',
  `type` tinyint(4) DEFAULT NULL COMMENT '漂流瓶的方式（0：自然方式 1：语音合成）',
  `status` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` tinyint(255) NOT NULL DEFAULT '0' COMMENT '角色状态',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission` (
  `role_id` int(11) NOT NULL,
  `permission_id` int(11) NOT NULL,
  PRIMARY KEY (`role_id`,`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for template
-- ----------------------------
DROP TABLE IF EXISTS `template`;
CREATE TABLE `template` (
  `id` int(11) NOT NULL,
  `body` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '模板内容主体',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nick_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '管理员登录后台的密码',
  `open_id` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `session_key` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_time` timestamp NULL DEFAULT NULL COMMENT '上次登录时间的时间戳',
  `status` tinyint(1) NOT NULL DEFAULT '0' COMMENT '用户状态',
  `prohibit_time` datetime DEFAULT NULL COMMENT '被禁止登录的时间',
  `prohibit_days` int(11) DEFAULT NULL COMMENT '禁止时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '用户角色被创建时间',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
