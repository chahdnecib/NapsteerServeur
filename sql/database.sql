-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: serveur
-- ------------------------------------------------------
-- Server version	8.3.0

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
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `adresseIP` varchar(15) NOT NULL,
  `port` int NOT NULL,
  `derniereConnexion` datetime NOT NULL,
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES ('amira','necib','234.578.67',34,'2025-02-23 22:21:42'),('chahd','necib','127.0.0.1',8081,'2025-02-27 03:05:31'),('client3','client','127.0.0.1',58829,'2025-02-23 17:56:24'),('dddd','qqqqqqqq','127.0.0.1',58835,'2025-02-23 17:59:02'),('dekra','necib','127.0.0.1',56612,'2025-02-23 00:48:51'),('eeeeeeeee','zzzzzzzzz','127.0.0.1',57010,'2025-02-23 01:41:07'),('fff','ffff','127.0.0.1',57057,'2025-02-23 01:46:48'),('ffff','ffffff','1234567890765',24567,'2025-02-25 22:02:53'),('jjjjj','rrrrrr','127.0.0.1',57076,'2025-02-23 01:49:32'),('kkk','kkk','192.168.1.10',8087,'2025-02-23 18:06:55'),('mahmoud','necib','12345678',34,'2025-02-25 23:33:27'),('mama','qqq','127.0.0.1',56733,'2025-02-23 00:54:26'),('nejm','necib','127.0.0.1',56960,'2025-02-23 01:31:58'),('ouss','soui','127.0.0.1',56839,'2025-02-23 01:10:44'),('oussi','qqq','127.0.0.1',56941,'2025-02-23 01:28:00'),('refka','necib','127.0.0.1',56672,'2025-02-23 00:45:37'),('serrr','qqq','127.0.0.1',56972,'2025-02-23 01:34:47'),('ss','qqqqq','127.0.0.1',56953,'2025-02-23 01:29:44'),('ssss','ddd','127.0.0.1',56863,'2025-02-23 01:14:53'),('user','user','127.0.0.1',58393,'2025-02-23 17:29:29'),('user1','hashed_password1','192.168.1.10',8080,'2025-02-23 00:12:05'),('user3','user','127.0.0.1',58689,'2025-02-23 17:51:30'),('vvvvv','bbbbb','127.0.0.1',57106,'2025-02-23 01:54:50'),('wwww','qqqq','127.0.0.1',56706,'2025-02-23 00:50:33');
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fichiers`
--

DROP TABLE IF EXISTS `fichiers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fichiers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) DEFAULT NULL,
  `nomFichier` varchar(255) NOT NULL,
  `tailleFichier` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `username` (`username`),
  CONSTRAINT `fichiers_ibfk_1` FOREIGN KEY (`username`) REFERENCES `clients` (`username`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fichiers`
--

LOCK TABLES `fichiers` WRITE;
/*!40000 ALTER TABLE `fichiers` DISABLE KEYS */;
INSERT INTO `fichiers` VALUES (1,'user1','fichier1.mp4',104857600),(2,'user1','chahd',104857600),(3,'refka','starwars',104857600),(4,'refka','starwars',104857600),(5,'kkk','rapport.pdf',1024),(6,'kkk','photo.jpg',2048),(7,'kkk','video.mp4',5120),(8,'amira','song.jpg',500),(9,'ffff','home.txt',2000),(10,'ffff','chance.mp4',288),(11,'ffff','alger',69900),(12,NULL,'java.txt',1024),(13,NULL,'python.txt',1245),(14,'chahd','love.mp4',1753);
/*!40000 ALTER TABLE `fichiers` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-02-27 19:46:01
