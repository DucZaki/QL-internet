-- MySQL dump 10.13  Distrib 9.0.1, for macos14 (x86_64)
--
-- Host: localhost    Database: quanly_quan_net
-- ------------------------------------------------------
-- Server version	9.0.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Current Database: `quanly_quan_net`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `quanly_quan_net` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `quanly_quan_net`;

--
-- Table structure for table `khach`
--

DROP TABLE IF EXISTS `khach`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `khach` (
  `id` int NOT NULL AUTO_INCREMENT,
  `ten` varchar(100) NOT NULL,
  `so_dien_thoai` varchar(20) DEFAULT NULL,
  `so_du` int DEFAULT '0',
  `mat_khau` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `so_dien_thoai` (`so_dien_thoai`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `khach`
--

LOCK TABLES `khach` WRITE;
/*!40000 ALTER TABLE `khach` DISABLE KEYS */;
INSERT INTO `khach` VALUES (1,'duc',NULL,358442,'zaki'),(2,'duc','duc0866147595',264565,'aa'),(3,'1duc','0129482321',283873,'1'),(7,'hung','0987265478',10141953,'1'),(8,'as','0987653531',730465,'1'),(9,'ducza','0876546782',998288,'1');
/*!40000 ALTER TABLE `khach` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `may`
--

DROP TABLE IF EXISTS `may`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `may` (
  `id` int NOT NULL AUTO_INCREMENT,
  `trang_thai` enum('trong','dang_su_dung') DEFAULT 'trong',
  `id_khach` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `id_khach` (`id_khach`),
  CONSTRAINT `may_ibfk_1` FOREIGN KEY (`id_khach`) REFERENCES `khach` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `may`
--

LOCK TABLES `may` WRITE;
/*!40000 ALTER TABLE `may` DISABLE KEYS */;
INSERT INTO `may` VALUES (1,'trong',NULL),(2,'trong',NULL),(3,'trong',NULL),(4,'dang_su_dung',3),(5,'trong',NULL),(6,'dang_su_dung',2),(7,'dang_su_dung',1),(8,'dang_su_dung',8),(9,'trong',NULL),(10,'trong',NULL),(11,'trong',NULL),(12,'trong',NULL),(13,'dang_su_dung',7),(14,'trong',NULL),(15,'trong',NULL),(16,'trong',NULL),(17,'trong',NULL),(18,'dang_su_dung',9),(19,'trong',NULL),(20,'trong',NULL),(21,'trong',NULL),(22,'trong',NULL),(23,'trong',NULL),(24,'trong',NULL),(25,'trong',NULL),(26,'trong',NULL),(27,'trong',NULL),(28,'trong',NULL),(29,'trong',NULL),(30,'trong',NULL);
/*!40000 ALTER TABLE `may` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nap_tien`
--

DROP TABLE IF EXISTS `nap_tien`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `nap_tien` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_khach` int DEFAULT NULL,
  `so_tien` int NOT NULL,
  `thoi_gian` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_khach` (`id_khach`),
  CONSTRAINT `nap_tien_ibfk_1` FOREIGN KEY (`id_khach`) REFERENCES `khach` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nap_tien`
--

LOCK TABLES `nap_tien` WRITE;
/*!40000 ALTER TABLE `nap_tien` DISABLE KEYS */;
INSERT INTO `nap_tien` VALUES (1,7,55555,'2025-03-04 09:15:22'),(2,8,666666,'2025-03-04 09:18:28'),(3,1,100000,'2025-03-04 09:31:47'),(4,9,999888,'2025-03-04 09:34:39');
/*!40000 ALTER TABLE `nap_tien` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `thue_may`
--

DROP TABLE IF EXISTS `thue_may`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `thue_may` (
  `id` int NOT NULL AUTO_INCREMENT,
  `id_khach` int DEFAULT NULL,
  `id_may` int DEFAULT NULL,
  `thoi_gian_bat_dau` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `id_khach` (`id_khach`),
  KEY `id_may` (`id_may`),
  CONSTRAINT `thue_may_ibfk_1` FOREIGN KEY (`id_khach`) REFERENCES `khach` (`id`),
  CONSTRAINT `thue_may_ibfk_2` FOREIGN KEY (`id_may`) REFERENCES `may` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `thue_may`
--

LOCK TABLES `thue_may` WRITE;
/*!40000 ALTER TABLE `thue_may` DISABLE KEYS */;
/*!40000 ALTER TABLE `thue_may` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-03-10 22:57:18
