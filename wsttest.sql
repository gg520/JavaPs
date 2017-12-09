/*
Navicat MySQL Data Transfer

Source Server         : localhost_3306
Source Server Version : 50534
Source Host           : localhost:3306
Source Database       : test

Target Server Type    : MYSQL
Target Server Version : 50534
File Encoding         : 65001

Date: 2017-12-09 20:19:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for wsttest
-- ----------------------------
DROP TABLE IF EXISTS `wsttest`;
CREATE TABLE `wsttest` (
  `md5` varchar(255) NOT NULL,
  `title` text CHARACTER SET utf8,
  `url` text CHARACTER SET utf8 NOT NULL,
  `author` text,
  `content` longtext CHARACTER SET utf8,
  `eventtime` varchar(255) DEFAULT NULL,
  `pageview` text CHARACTER SET utf8 NOT NULL,
  `informNUM` varchar(255) DEFAULT NULL,
  `publish` varchar(255) DEFAULT NULL,
  `createtime` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  PRIMARY KEY (`md5`)
) ENGINE=InnoDB DEFAULT CHARSET=gb2312;
