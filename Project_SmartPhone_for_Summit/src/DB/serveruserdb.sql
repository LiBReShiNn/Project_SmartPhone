CREATE DATABASE  IF NOT EXISTS `serveruserdb` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `serveruserdb`;
-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: serveruserdb
-- ------------------------------------------------------
-- Server version	5.7.10-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `chattertb`
--

DROP TABLE IF EXISTS `chattertb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `chattertb` (
  `userID` char(40) NOT NULL,
  `counterID` char(40) NOT NULL,
  KEY `fk_chattertb_userId` (`userID`),
  KEY `fk_chattertb_counterId` (`counterID`),
  CONSTRAINT `fk_chattertb_counterId` FOREIGN KEY (`counterID`) REFERENCES `usertb` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_chattertb_userId` FOREIGN KEY (`userID`) REFERENCES `usertb` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chattertb`
--

LOCK TABLES `chattertb` WRITE;
/*!40000 ALTER TABLE `chattertb` DISABLE KEYS */;
INSERT INTO `chattertb` VALUES ('test1@naver.com','test2@naver.com');
/*!40000 ALTER TABLE `chattertb` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `phaddrbooktb`
--

DROP TABLE IF EXISTS `phaddrbooktb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `phaddrbooktb` (
  `phName` char(20) NOT NULL,
  `phEmail` char(50) DEFAULT NULL,
  `phNo` char(20) NOT NULL,
  `phImagePath` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`phNo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `phaddrbooktb`
--

LOCK TABLES `phaddrbooktb` WRITE;
/*!40000 ALTER TABLE `phaddrbooktb` DISABLE KEYS */;
/*!40000 ALTER TABLE `phaddrbooktb` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roomtb`
--

DROP TABLE IF EXISTS `roomtb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `roomtb` (
  `userID` char(40) NOT NULL,
  `roomName` varchar(1000) NOT NULL,
  `counterID` char(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roomtb`
--

LOCK TABLES `roomtb` WRITE;
/*!40000 ALTER TABLE `roomtb` DISABLE KEYS */;
INSERT INTO `roomtb` VALUES ('test1@naver.com','test1@naver.com,test2@naver.com','test2@naver.com');
/*!40000 ALTER TABLE `roomtb` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usertb`
--

DROP TABLE IF EXISTS `usertb`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usertb` (
  `ID` char(40) NOT NULL,
  `PW` char(16) NOT NULL,
  `phoneNo` char(13) NOT NULL,
  `nicName` char(20) NOT NULL,
  `imagePath` varchar(300) DEFAULT NULL,
  `logOn` char(3) NOT NULL DEFAULT 'off',
  `ipAddress` char(30) DEFAULT NULL,
  `port` char(6) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usertb`
--

LOCK TABLES `usertb` WRITE;
/*!40000 ALTER TABLE `usertb` DISABLE KEYS */;
INSERT INTO `usertb` VALUES ('test1@naver.com','asdf1234','010-1111-1111','TEST1',NULL,'off',NULL,NULL),('test2@naver.com','asdf1234','010-2222-2222','TEST2',NULL,'off',NULL,NULL),('test3@naver.com','asdf1234','010-3333-3333','TEST3',NULL,'off',NULL,NULL),('test4@naver.com','asdf1234','010-4444-4444','TEST4',NULL,'off',NULL,NULL),('test5@naver.com','asdf1234','010-5555-5555','TEST5',NULL,'off',NULL,NULL),('test6@naver.com','asdf1234','010-6666-6666','TEST6',NULL,'off',NULL,NULL);
/*!40000 ALTER TABLE `usertb` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-02-18  9:56:34
