-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: blogs
-- ------------------------------------------------------
-- Server version	5.7.43-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `admin_menu`
--

DROP TABLE IF EXISTS `admin_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `admin_menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `admin_id` int(11) NOT NULL,
  `menu_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `admin_menu`
--

LOCK TABLES `admin_menu` WRITE;
/*!40000 ALTER TABLE `admin_menu` DISABLE KEYS */;
INSERT INTO `admin_menu` VALUES (2,8,11,'2025-03-11 09:06:20'),(3,8,12,'2025-03-11 09:06:24'),(4,8,13,'2025-03-11 09:06:26'),(5,8,14,'2025-03-11 09:06:27'),(6,8,15,'2025-03-11 09:06:29'),(7,8,16,'2025-03-11 09:06:31'),(8,8,17,'2025-03-11 09:06:32'),(9,8,18,'2025-03-11 09:06:34'),(10,8,19,'2025-03-11 09:06:35'),(11,8,20,'2025-03-11 09:06:37'),(12,8,21,'2025-03-11 09:06:40'),(13,8,22,'2025-03-11 09:06:42'),(14,8,23,'2025-03-11 09:06:43'),(15,8,24,'2025-03-11 09:06:45'),(16,8,25,'2025-03-11 09:06:46'),(17,8,26,'2025-03-11 09:06:48'),(18,8,27,'2025-03-11 09:06:51'),(19,8,28,'2025-03-20 07:09:44'),(20,8,29,'2025-03-20 07:09:48'),(21,8,32,'2025-04-03 04:44:53'),(22,8,33,'2025-04-03 04:44:57'),(23,8,34,'2025-04-03 04:45:01'),(24,8,35,'2025-04-03 05:30:43'),(44,8,36,'2025-09-10 14:43:26');
/*!40000 ALTER TABLE `admin_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `album`
--

DROP TABLE IF EXISTS `album`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `album` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `album_name` varchar(45) NOT NULL,
  `album_cover` varchar(64) NOT NULL,
  `album_desc` varchar(64) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` int(11) NOT NULL DEFAULT '0',
  `photo_count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `album`
--

LOCK TABLES `album` WRITE;
/*!40000 ALTER TABLE `album` DISABLE KEYS */;
/*!40000 ALTER TABLE `album` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article`
--

DROP TABLE IF EXISTS `article`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(45) NOT NULL,
  `title` varchar(128) NOT NULL,
  `content` text NOT NULL,
  `article_cover` varchar(256) NOT NULL,
  `category` varchar(128) NOT NULL,
  `article_url` varchar(256) NOT NULL,
  `star_count` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `state` int(11) NOT NULL DEFAULT '0',
  `read_count` int(11) NOT NULL DEFAULT '0',
  `comment_count` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL,
  `user_avatar` varchar(64) NOT NULL,
  `tag_list` varchar(512) NOT NULL,
  `like_count` int(11) NOT NULL DEFAULT '0',
  `is_original` int(11) NOT NULL DEFAULT '1',
  `original_url` varchar(64) DEFAULT NULL,
  `is_top` int(11) NOT NULL DEFAULT '0',
  `brief` varchar(128) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article`
--

LOCK TABLES `article` WRITE;
/*!40000 ALTER TABLE `article` DISABLE KEYS */;
/*!40000 ALTER TABLE `article` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_category`
--

DROP TABLE IF EXISTS `article_category`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(45) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `nickname` varchar(16) NOT NULL DEFAULT '',
  `is_del` int(11) NOT NULL DEFAULT '0',
  `article_count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_category`
--

LOCK TABLES `article_category` WRITE;
/*!40000 ALTER TABLE `article_category` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_category` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_comment`
--

DROP TABLE IF EXISTS `article_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` varchar(512) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_id` int(11) NOT NULL DEFAULT '0',
  `state` tinyint(1) NOT NULL DEFAULT '0',
  `like_count` int(11) NOT NULL DEFAULT '0',
  `to_nickname` varchar(16) DEFAULT NULL,
  `user_nickname` varchar(16) NOT NULL,
  `to_id` int(11) DEFAULT NULL,
  `user_avatar` varchar(64) NOT NULL,
  `reply_count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_comment`
--

LOCK TABLES `article_comment` WRITE;
/*!40000 ALTER TABLE `article_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_comment_like`
--

DROP TABLE IF EXISTS `article_comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_comment_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `article_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_comment_like`
--

LOCK TABLES `article_comment_like` WRITE;
/*!40000 ALTER TABLE `article_comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_like`
--

DROP TABLE IF EXISTS `article_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `article_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `like_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_like`
--

LOCK TABLES `article_like` WRITE;
/*!40000 ALTER TABLE `article_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag`
--

DROP TABLE IF EXISTS `article_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(32) NOT NULL,
  `article_count` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `nickname` varchar(16) NOT NULL DEFAULT '',
  `is_del` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COMMENT='文章标签';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag`
--

LOCK TABLES `article_tag` WRITE;
/*!40000 ALTER TABLE `article_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `article_tag_link`
--

DROP TABLE IF EXISTS `article_tag_link`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `article_tag_link` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `tag_id` int(11) NOT NULL DEFAULT '0',
  `article_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `article_tag_link`
--

LOCK TABLES `article_tag_link` WRITE;
/*!40000 ALTER TABLE `article_tag_link` DISABLE KEYS */;
/*!40000 ALTER TABLE `article_tag_link` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat`
--

DROP TABLE IF EXISTS `chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL DEFAULT '0',
  `nickname` varchar(32) NOT NULL,
  `message` varchar(512) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` int(11) NOT NULL DEFAULT '0',
  `is_read` int(11) NOT NULL DEFAULT '0',
  `to_user` varchar(32) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `message_type` varchar(10) NOT NULL DEFAULT 'text',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat`
--

LOCK TABLES `chat` WRITE;
/*!40000 ALTER TABLE `chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classestable`
--

DROP TABLE IF EXISTS `classestable`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classestable` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `fir_week` varchar(10) NOT NULL,
  `fir_start` int(11) NOT NULL,
  `fir_end` int(11) NOT NULL,
  `sec_week` varchar(10) DEFAULT NULL,
  `sec_start` int(11) DEFAULT NULL,
  `sec_end` int(11) DEFAULT NULL,
  `week_num_start` int(11) NOT NULL,
  `week_num_end` int(11) NOT NULL,
  `semester` varchar(25) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classestable`
--

LOCK TABLES `classestable` WRITE;
/*!40000 ALTER TABLE `classestable` DISABLE KEYS */;
/*!40000 ALTER TABLE `classestable` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend`
--

DROP TABLE IF EXISTS `friend`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `friend` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` varchar(45) NOT NULL,
  `cover` varchar(45) NOT NULL,
  `introduction` varchar(128) NOT NULL,
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0 待审核 1 未通过 2 已通过',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `url` varchar(128) NOT NULL,
  `recommend_status` int(11) NOT NULL DEFAULT '0' COMMENT '推荐状态',
  `user_id` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COMMENT='友情链接';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend`
--

LOCK TABLES `friend` WRITE;
/*!40000 ALTER TABLE `friend` DISABLE KEYS */;
INSERT INTO `friend` VALUES (1,'叶玲子的小栈','https://www.yeling.top/image/logo.jpg','记录学习、生活、兴趣的次元小栈',2,'2025-04-29 04:22:42','https://www.yeling.top',0,8,'123456@123456.com');
/*!40000 ALTER TABLE `friend` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `group_chat`
--

DROP TABLE IF EXISTS `group_chat`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `group_chat` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `nickname` varchar(32) NOT NULL,
  `message` varchar(512) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` int(11) NOT NULL DEFAULT '0',
  `to_group` varchar(45) DEFAULT NULL,
  `ip` varchar(45) DEFAULT NULL,
  `message_type` varchar(10) NOT NULL DEFAULT 'text',
  `user_avatar` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `group_chat`
--

LOCK TABLES `group_chat` WRITE;
/*!40000 ALTER TABLE `group_chat` DISABLE KEYS */;
/*!40000 ALTER TABLE `group_chat` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `home_bg`
--

DROP TABLE IF EXISTS `home_bg`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `home_bg` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(128) NOT NULL,
  `state` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `home_bg`
--

LOCK TABLES `home_bg` WRITE;
/*!40000 ALTER TABLE `home_bg` DISABLE KEYS */;
/*!40000 ALTER TABLE `home_bg` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `menu_name` varchar(45) NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `path` varchar(100) DEFAULT NULL,
  `icon` varchar(45) DEFAULT NULL,
  `sort_order` int(11) DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` int(11) NOT NULL DEFAULT '0',
  `path_pattern` varchar(100) DEFAULT NULL,
  `component` varchar(100) DEFAULT NULL,
  `menu_type` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu`
--

LOCK TABLES `menu` WRITE;
/*!40000 ALTER TABLE `menu` DISABLE KEYS */;
INSERT INTO `menu` VALUES (1,'菜单管理',0,'/menu','Menu',4,'2025-03-10 09:13:45','2025-03-10 09:13:45',1,'/api/admin/menu/**','/menu/menuList',0),(2,'首页',0,'/home','HomeFilled',1,'2025-03-11 08:08:23','2025-03-11 08:08:23',1,'/api/home/**','',0),(3,'内容管理',0,'','Document',2,'2025-03-11 08:09:35','2025-03-11 08:09:35',1,'/api/home/**','',0),(4,'互动中心',0,'','Connection',3,'2025-03-11 08:10:12','2025-03-11 08:10:12',1,'','',0),(5,'用户与权限',0,'','User',3,'2025-03-11 08:11:06','2025-03-11 08:11:06',1,'','',0),(6,'系统维护',0,'','Setting',5,'2025-03-11 08:12:00','2025-03-11 08:12:00',1,'','',0),(7,'文章管理',0,'','Notebook',1,'2025-03-11 08:13:43','2025-03-11 08:13:43',1,'','',0),(8,'说说管理',0,'','ChatLineRound',2,'2025-03-11 08:14:08','2025-03-11 08:14:08',1,'','',0),(9,'评论管理',0,'','Comment',3,'2025-03-11 08:14:31','2025-03-11 08:14:31',1,'','',0),(10,'发布文章',0,'/article/write','',1,'2025-03-11 08:16:12','2025-03-11 08:16:12',1,'','',0),(11,'首页',0,'/home','HomeFilled',1,'2025-03-11 08:42:07','2025-03-11 08:42:07',0,'/api/admin/home/**','/home/home',2),(12,'文章管理',0,'','Notebook',2,'2025-03-11 08:47:12','2025-03-11 08:47:12',0,'/api/admin/article/**','',1),(13,'说说管理',0,'','ChatLineRound',3,'2025-03-11 08:49:10','2025-03-11 08:49:10',0,'/api/admin/talk/**','',1),(14,'互动中心',0,'','Connection',4,'2025-03-11 08:51:53','2025-03-11 08:51:53',0,'','',1),(15,'相册管理',0,'','Picture',6,'2025-03-11 08:52:39','2025-03-11 08:52:39',0,'/api/admin/album/**','',1),(16,'系统维护',0,'','Setting',9,'2025-03-11 08:53:13','2025-03-11 08:53:13',0,'/api/admin/system/**','',1),(17,'发布文章',12,'/article/write','',21,'2025-03-11 08:55:37','2025-03-11 08:55:37',0,'','/article/write',2),(18,'文章归档',12,'/article','',22,'2025-03-11 08:56:53','2025-03-11 08:56:53',0,'','/article/list',2),(19,'文章分类',12,'/article/category','',2,'2025-03-11 08:57:19','2025-03-11 08:57:19',0,'','/article/category',2),(20,'文章标签',12,'/article/tag','',4,'2025-03-11 08:57:49','2025-03-11 08:57:49',0,'','/article/tag',2),(21,'发布说说',13,'/talk/create','',1,'2025-03-11 08:59:22','2025-03-11 08:59:22',0,'','/talk/create',2),(22,'历史说说',13,'/talk','',2,'2025-03-11 08:59:46','2025-03-11 08:59:46',0,'','/talk/list',2),(23,'留言管理',14,'/message','',1,'2025-03-11 09:01:43','2025-03-11 09:01:43',0,'/api/admin/interact/message/**','/interact/message',2),(24,'友链管理',14,'/friend','',2,'2025-03-11 09:02:02','2025-03-11 09:02:02',0,'/api/admin/interact/friend/**','/interact/friend',2),(25,'相册分类',15,'/album/category','',1,'2025-03-11 09:02:54','2025-03-11 09:02:54',0,'','/album/category',2),(26,'图片上传',15,'/album/upload','',2,'2025-03-11 09:03:37','2025-03-11 09:03:37',0,'','/album/upload',2),(27,'数据备份',16,'/system/backup','',1,'2025-03-11 09:04:20','2025-03-11 09:04:20',0,'','/system/backup',2),(28,'权限管理',0,'','Operation',8,'2025-03-20 07:07:09','2025-03-20 07:07:09',0,'/api/admin/authority/**','',1),(29,'菜单列表',28,'/menu','',1,'2025-03-20 07:08:36','2025-03-20 07:08:36',0,'','/menu/list',2),(30,'评论管理',0,'','Comment',8,'2025-03-22 06:16:13','2025-03-22 06:16:13',1,'/api/admin/comment/**','',1),(31,'评论管理',0,'','Comment',5,'2025-03-22 06:16:35','2025-03-22 06:16:35',1,'/api/admin/comment/**','',1),(32,'评论管理',0,'','Comment',5,'2025-03-22 08:01:33','2025-03-22 08:01:33',0,'/api/admin/comment/**','',1),(33,'文章评论',32,'/comment/article','Notebook',1,'2025-03-22 08:03:22','2025-03-22 08:03:22',0,'','/comment/article',2),(34,'说说评论',32,'/comment/talk','ChatLineRound',2,'2025-03-22 08:04:28','2025-03-22 08:04:28',0,'','/comment/talk',2),(35,'背景图片',0,'/bg','Picture',7,'2025-04-03 05:29:48','2025-04-03 05:29:48',0,'/api/admin/background/**','/bg/bg',2),(36,'消息',0,'/chat/:chatId','Comment',9,'2025-09-09 15:59:27','2025-09-09 15:59:27',0,'/api/admin/chat/**','/chat/ChatLayout',2);
/*!40000 ALTER TABLE `menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message`
--

DROP TABLE IF EXISTS `message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `message` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content` varchar(255) NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip` varchar(45) NOT NULL,
  `ip_location` varchar(45) NOT NULL DEFAULT '未知',
  `state` int(11) NOT NULL DEFAULT '0' COMMENT '0 待审核, 1 删除, 2 通过',
  `user_avatar` varchar(64) DEFAULT NULL,
  `user_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message`
--

LOCK TABLES `message` WRITE;
/*!40000 ALTER TABLE `message` DISABLE KEYS */;
INSERT INTO `message` VALUES (1,'留言','游客','2025-04-16 15:04:03','0:0:0:0:0:0:0:1','未知',0,NULL,0),(2,'测试','游客','2025-04-20 17:26:21','0:0:0:1','未知',2,'',0),(3,'测试','游客','2025-04-20 17:26:22','0:0:0:1','未知',2,'',0),(4,'dhaduhaoidhaoidjaidjadpjadjoadjfbryjfgeyisi分数俄方好饿粉红色哦i纷纷色i飞机四分','游客','2025-05-02 07:29:18','0:0:0:0:0:0:0:1','未知',0,NULL,0),(5,'','游客','2025-05-02 07:29:35','0:0:0:0:0:0:0:1','未知',0,NULL,0),(6,'阿达达瓦的','游客','2025-05-02 07:31:20','0:0:0:0:0:0:0:1','未知',0,NULL,0),(7,'是大家都加了我的家啊看了上面的上课点名啊看得见吗里的经济模式卡没带打开了大门来得及啊宽的马路看到你1骄傲的马克思摩擦时开幕的摩擦扣税的马路的模块2','游客','2025-05-02 07:32:48','0:0:0:0:0:0:0:1','未知',0,NULL,0),(8,'123456','游客','2025-08-09 15:06:03','0:0:0:0:0:0:0:1','未知',0,NULL,0);
/*!40000 ALTER TABLE `message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `photo`
--

DROP TABLE IF EXISTS `photo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `photo` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `photo_name` varchar(256) NOT NULL,
  `photo_url` varchar(64) NOT NULL,
  `user_id` int(11) NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `album_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_del` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `photo`
--

LOCK TABLES `photo` WRITE;
/*!40000 ALTER TABLE `photo` DISABLE KEYS */;
/*!40000 ALTER TABLE `photo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `salt`
--

DROP TABLE IF EXISTS `salt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `salt` (
  `email` varchar(45) NOT NULL,
  `salt` varchar(16) NOT NULL,
  PRIMARY KEY (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `salt`
--

LOCK TABLES `salt` WRITE;
/*!40000 ALTER TABLE `salt` DISABLE KEYS */;
INSERT INTO `salt` VALUES ('123456@123456.com','afb78ec85c1f03c6');
/*!40000 ALTER TABLE `salt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `talk_comment`
--

DROP TABLE IF EXISTS `talk_comment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talk_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `user_nickname` varchar(16) NOT NULL,
  `user_avatar` varchar(64) NOT NULL,
  `content` varchar(512) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `parent_id` int(11) NOT NULL DEFAULT '0',
  `to_id` int(11) DEFAULT NULL,
  `to_nickname` varchar(16) DEFAULT NULL,
  `like_count` int(11) NOT NULL DEFAULT '0',
  `reply_count` int(11) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  `talk_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `talk_comment`
--

LOCK TABLES `talk_comment` WRITE;
/*!40000 ALTER TABLE `talk_comment` DISABLE KEYS */;
/*!40000 ALTER TABLE `talk_comment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `talk_comment_like`
--

DROP TABLE IF EXISTS `talk_comment_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talk_comment_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `comment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `talk_id` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `talk_comment_like`
--

LOCK TABLES `talk_comment_like` WRITE;
/*!40000 ALTER TABLE `talk_comment_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `talk_comment_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `talk_like`
--

DROP TABLE IF EXISTS `talk_like`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talk_like` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `talk_id` int(11) NOT NULL DEFAULT '0',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `talk_like`
--

LOCK TABLES `talk_like` WRITE;
/*!40000 ALTER TABLE `talk_like` DISABLE KEYS */;
/*!40000 ALTER TABLE `talk_like` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `talks`
--

DROP TABLE IF EXISTS `talks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `talks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickname` varchar(16) NOT NULL,
  `content` varchar(2560) NOT NULL,
  `image_url` varchar(2560) DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `state` tinyint(1) NOT NULL DEFAULT '0' COMMENT '0 展示 1 删除 2保存',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `user_avatar` varchar(64) NOT NULL,
  `is_top` int(11) NOT NULL DEFAULT '0',
  `like_count` int(11) NOT NULL DEFAULT '0',
  `comment_count` int(11) NOT NULL DEFAULT '0',
  `title` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `talks`
--

LOCK TABLES `talks` WRITE;
/*!40000 ALTER TABLE `talks` DISABLE KEYS */;
/*!40000 ALTER TABLE `talks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `nickname` varchar(16) NOT NULL,
  `signature` varchar(512) DEFAULT NULL,
  `birthday` varchar(10) DEFAULT NULL,
  `user_avatar` varchar(256) NOT NULL,
  `user_id` int(11) NOT NULL,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `article_count` int(11) NOT NULL DEFAULT '0',
  `category_count` int(11) NOT NULL DEFAULT '0',
  `tag_count` int(11) NOT NULL DEFAULT '0',
  `message` int(11) NOT NULL DEFAULT '0',
  `comment` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`email`),
  UNIQUE KEY `user_id_UNIQUE` (`user_id`),
  UNIQUE KEY `ni_cheng_UNIQUE` (`nickname`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_info`
--

LOCK TABLES `user_info` WRITE;
/*!40000 ALTER TABLE `user_info` DISABLE KEYS */;
INSERT INTO `user_info` VALUES (1,'123456@123456.com','管理员08',NULL,NULL,'/image/avatar/avatar88.png',8,'2019-12-31 16:00:00',0,0,0,0,0);
/*!40000 ALTER TABLE `user_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `user_password` varchar(64) NOT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `role` varchar(10) NOT NULL DEFAULT '0',
  `state` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_name_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (8,'123456@123456.com','d19b8753f81899abe82d9224ca960fa07bd1c1155b708fcab9efcb9f3ed6dc74','2025-01-28 07:45:17','2025-01-28 07:45:17','test',0);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `view_info`
--

DROP TABLE IF EXISTS `view_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `view_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `ip` varchar(24) NOT NULL,
  `city` varchar(45) NOT NULL,
  `create_time` varchar(45) NOT NULL DEFAULT 'CURRENT_TIMESTAMP',
  `user_id` int(11) NOT NULL DEFAULT '0',
  `nickname` varchar(16) NOT NULL DEFAULT '用户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `view_info`
--

LOCK TABLES `view_info` WRITE;
/*!40000 ALTER TABLE `view_info` DISABLE KEYS */;
INSERT INTO `view_info` VALUES (1,'0:0:0:0:0:0:0:1','本地','2025-04-20 18:19:33.762703',0,'游客'),(3,'0:0:0:0:0:0:0:1','本地','2025-04-20 22:05:45.275153',0,'游客');
/*!40000 ALTER TABLE `view_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `views`
--

DROP TABLE IF EXISTS `views`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `views` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `view_count` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `views`
--

LOCK TABLES `views` WRITE;
/*!40000 ALTER TABLE `views` DISABLE KEYS */;
INSERT INTO `views` VALUES (1,'2025-04-05 03:59:58',51),(2,'2025-04-06 03:59:58',120),(3,'2025-04-07 03:59:58',65),(4,'2025-04-08 03:59:58',39),(5,'2025-04-09 04:00:00',147),(6,'2025-04-10 03:59:58',47),(7,'2025-04-11 03:59:58',76),(8,'2025-04-12 03:59:58',89),(9,'2025-04-13 03:59:58',33),(10,'2025-04-14 03:59:58',51),(11,'2025-04-15 03:59:58',51),(12,'2025-04-19 13:51:53',1),(13,'2025-04-19 16:00:00',1);
/*!40000 ALTER TABLE `views` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-10 22:56:18
