-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: cinema
-- ------------------------------------------------------
-- Server version	8.0.31

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
-- Table structure for table `addition`
--

DROP TABLE IF EXISTS `addition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addition` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `price` decimal(7,2) NOT NULL,
  `available` tinyint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `addition`
--

LOCK TABLES `addition` WRITE;
/*!40000 ALTER TABLE `addition` DISABLE KEYS */;
INSERT INTO `addition` VALUES (1,'Кава 350мл',27.00,1),(2,'Булочка з яблуками',15.00,1),(3,'Булочка з чорницею',17.00,1),(4,'Кекс з родзинками',25.00,1),(5,'Кекс без родзинок',20.00,0),(6,'Тістечко з корицею ',12.00,0),(7,'Чай з лимоном 350мл',15.00,1),(39,'Морозиво пломбір 70г',25.00,1),(40,'Попкорн 150г',60.00,1),(41,'Газована вода 0,5л',20.00,1);
/*!40000 ALTER TABLE `addition` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bill_to_pay`
--

DROP TABLE IF EXISTS `bill_to_pay`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `bill_to_pay` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `bill_to_pay` varchar(29) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `billToPay_UNIQUE` (`bill_to_pay`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bill_to_pay`
--

LOCK TABLES `bill_to_pay` WRITE;
/*!40000 ALTER TABLE `bill_to_pay` DISABLE KEYS */;
INSERT INTO `bill_to_pay` VALUES (3,'Головний','UA213223130000026007233566001'),(4,'Запасний','UA213223130000026007233566002'),(5,'Про всяк випадок','UA213223130000026007233566004'),(7,'Новий','UA213223130000026007233566005');
/*!40000 ALTER TABLE `bill_to_pay` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `film`
--

DROP TABLE IF EXISTS `film`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `film` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `image_name` char(16) DEFAULT NULL,
  `duration` time NOT NULL,
  `description` text,
  `year` smallint DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `film`
--

LOCK TABLES `film` WRITE;
/*!40000 ALTER TABLE `film` DISABLE KEYS */;
INSERT INTO `film` VALUES (1,'Аватар','1684218140111','02:42:00','Джейк Саллі — колишній морський піхотинець, прикутий до інвалідного крісла. Незважаючи на немічне тіло, Джейк в душі, як і раніше залишається воїном. Він отримує завдання здійснити подорож у кілька світлових років до бази землян на планеті Пандора, де корпорації видобувають рідкісний мінерал, що має величезне значення для виходу Землі з енергетичної кризи.',2009),(2,'Джуманджі','1684218140112','01:40:00','Симпатичне та дотепне пригодницьке фентезі про оживший світ магічної настільної гри, який перетворив невеличке провінційне містечко не непрохідні джунглі, повні диких та небезпечних тварин. В центрі сюжету – історія Алана Перріша, який в дитинстві знайшов загадкову настільну гру під назвою «Джуманджі». Не знаючи про таємничі особливості гри, він переноситься в джунглі. Після 26 років проживання в диких лісах, його звільняють двоє ні про що не підозрюючих підлітків. І тепер їм належить перемогти могутню силу гри, яка випускає всю живність лісів Джуманджі в місто… Фільм «Джуманджі» або ж «Джуманджи» (1995) буде цікаво дивитись українською усім шанувальникам якісного пригодницького кіно.',1995),(3,'Подорож до центру Землі','1684218140113','01:33:00','Три шукача пригод потрапляють у підземний світ. Вони спускаються туди через вулканічний кратер. Те, що постає перед ними, вражає головних героїв. Тут живуть створіння, які вимерли на поверхні землі тисячі років тому. Мандрівники прямують углиб, де на них чекають інші знахідки.',2008),(14,'Зоряні війни. Прихована загроза','1684218140206','02:17:00','Торгова федерація була зобов\'язана платити податки на планеті Набу, якщо бажала тут торгувати. Однак вона не захотіла цього робити, і почалася тривала війна. На стороні королеви Амідали борються два воїна-джедая - Обі-Ван-Кенобі та його вчитель Квай-Гон-Жин.',1999),(21,'Вперед і вгору','1684218878462','01:36:00','Головний герой, Карл Фредріксен, все життя провів, мріючи про незвідані краї, мандри і експедиції у найвіддаленіші куточки земної кулі. І несподівано доля дарує йому шанс, зводячи з новим другом Расселом. Разом ця пара перетне простір і час, подолає моря і гори і, звичайно, знайде на свою голову багато проблем. Якщо цього літа ви хочете пригод, тоді — «Вперед і вгору!».',2009);
/*!40000 ALTER TABLE `film` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `flyway_schema_history`
--

DROP TABLE IF EXISTS `flyway_schema_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `flyway_schema_history`
--

LOCK TABLES `flyway_schema_history` WRITE;
/*!40000 ALTER TABLE `flyway_schema_history` DISABLE KEYS */;
INSERT INTO `flyway_schema_history` VALUES (1,'1','<< Flyway Baseline >>','BASELINE','<< Flyway Baseline >>',NULL,'root','2023-05-01 17:48:42',0,1);
/*!40000 ALTER TABLE `flyway_schema_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `login_data`
--

DROP TABLE IF EXISTS `login_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `login_data` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `nickname` varchar(50) NOT NULL,
  `password` varchar(44) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nickName_UNIQUE` (`nickname`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `fk_logindata_usertypes1_idx` (`role_id`),
  CONSTRAINT `fk_login_data_roles1` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `login_data`
--

LOCK TABLES `login_data` WRITE;
/*!40000 ALTER TABLE `login_data` DISABLE KEYS */;
INSERT INTO `login_data` VALUES (3,'Dmytro','d15pArefW7O+qIQhoLk1Lw==','f.dv.register@gmail.com',2),(5,'Anatoliy','d15pArefW7O+qIQhoLk1Lw==','Anatoliy@cs.khpi.edu.ua',1),(32,'DmytroFomenko','d15pArefW7O+qIQhoLk1Lw==','3@gmail.com',2),(33,'Dmytro_a','d15pArefW7O+qIQhoLk1Lw==','10@gmail.com',2),(38,'Volodymyr','c6pkTxtC2ELn1fXe466nwg==','f.va.register@gmail.com',2),(39,'Dmytro_b','d15pArefW7O+qIQhoLk1Lw==','10@cs.khpi.edu.ua',2);
/*!40000 ALTER TABLE `login_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `place`
--

DROP TABLE IF EXISTS `place`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `place` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `placement_id` bigint unsigned NOT NULL,
  `place_kind_id` bigint unsigned NOT NULL,
  `number` varchar(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_place_place_kind1_idx` (`place_kind_id`),
  KEY `fk_place_placement1_idx` (`placement_id`),
  CONSTRAINT `fk_place_place_kind1` FOREIGN KEY (`place_kind_id`) REFERENCES `place_kind` (`id`),
  CONSTRAINT `fk_place_placement1` FOREIGN KEY (`placement_id`) REFERENCES `placement` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `place`
--

LOCK TABLES `place` WRITE;
/*!40000 ALTER TABLE `place` DISABLE KEYS */;
INSERT INTO `place` VALUES (1,2,2,'ряд1міс1'),(2,2,4,'ряд1міс2'),(3,2,3,'ряд2міс1'),(5,2,2,'ряд2міс2'),(6,2,3,'ряд2міс3'),(7,1,3,'ряд1міс1'),(9,1,2,'ряд1міс2'),(11,1,5,'міс1'),(12,1,2,'міс2'),(14,11,3,'ряд1міс1'),(15,11,2,'ряд1міс2'),(16,11,4,'ряд3міс1');
/*!40000 ALTER TABLE `place` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `place_kind`
--

DROP TABLE IF EXISTS `place_kind`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `place_kind` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `add_price` decimal(7,2) NOT NULL DEFAULT '0.00',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `place_kind`
--

LOCK TABLES `place_kind` WRITE;
/*!40000 ALTER TABLE `place_kind` DISABLE KEYS */;
INSERT INTO `place_kind` VALUES (2,'М\'який стілець',0.00),(3,'Крісло',60.00),(4,'Диван',120.00),(5,'Крісло-масажер',150.00);
/*!40000 ALTER TABLE `place_kind` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `placement`
--

DROP TABLE IF EXISTS `placement`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `placement` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(256) NOT NULL,
  `image_name` char(16) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `placement`
--

LOCK TABLES `placement` WRITE;
/*!40000 ALTER TABLE `placement` DISABLE KEYS */;
INSERT INTO `placement` VALUES (1,'Велика поляна','1683700101216'),(2,'Мала поляна','1683699601594'),(11,'Крихітна поляна','1683727962587');
/*!40000 ALTER TABLE `placement` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `role` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `role` varchar(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `role_UNIQUE` (`role`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `role`
--

LOCK TABLES `role` WRITE;
/*!40000 ALTER TABLE `role` DISABLE KEYS */;
INSERT INTO `role` VALUES (1,'admin'),(2,'client');
/*!40000 ALTER TABLE `role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `seance`
--

DROP TABLE IF EXISTS `seance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `seance` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `film_id` bigint unsigned NOT NULL,
  `begin_time` timestamp NOT NULL,
  `base_price` decimal(7,2) unsigned NOT NULL,
  `bill_to_pay_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_seance_film1_idx` (`film_id`),
  KEY `fk_seance_billtopay1_idx` (`bill_to_pay_id`),
  CONSTRAINT `fk_seance_bill_to_pay1` FOREIGN KEY (`bill_to_pay_id`) REFERENCES `bill_to_pay` (`id`),
  CONSTRAINT `fk_seance_film1` FOREIGN KEY (`film_id`) REFERENCES `film` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `seance`
--

LOCK TABLES `seance` WRITE;
/*!40000 ALTER TABLE `seance` DISABLE KEYS */;
INSERT INTO `seance` VALUES (1,1,'2022-01-01 10:00:00',150.00,3),(12,3,'2023-05-18 09:30:00',125.00,3),(14,14,'2023-10-10 14:00:46',450.00,3),(20,3,'2023-05-17 22:50:32',155.00,3),(21,3,'2023-05-18 00:50:46',120.00,3),(24,1,'2023-05-28 17:09:30',130.00,3),(30,21,'2023-06-01 07:00:02',50.00,4),(31,1,'2023-05-29 06:45:34',80.00,3),(32,21,'2023-05-31 07:00:12',35.00,3),(36,14,'2023-05-24 16:00:46',170.00,4),(37,1,'2023-05-24 09:43:01',50.00,3),(38,1,'2023-05-25 17:45:39',50.00,3),(39,1,'2023-05-29 17:42:05',20.00,3),(42,14,'2023-06-30 10:20:42',150.00,7),(43,1,'2023-06-06 13:54:01',150.00,4),(44,3,'2023-06-09 08:15:33',125.00,3),(45,21,'2023-06-09 13:30:23',110.00,3),(46,1,'2023-06-08 19:00:19',200.00,3),(47,21,'2023-06-08 16:30:17',160.00,3);
/*!40000 ALTER TABLE `seance` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket`
--

DROP TABLE IF EXISTS `ticket`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `seance_id` bigint unsigned NOT NULL,
  `place_id` bigint unsigned NOT NULL,
  `place_status` enum('FREE','RESERVED','TAKEN') NOT NULL,
  `client_id` bigint unsigned DEFAULT NULL,
  `code` varchar(36) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code_UNIQUE` (`code`),
  KEY `fk_ticket_seance1_idx` (`seance_id`) /*!80000 INVISIBLE */,
  KEY `fk_seat_logindata1_idx` (`client_id`),
  KEY `fk_ticket_place1_idx` (`place_id`),
  CONSTRAINT `fk_ticket_login_data1` FOREIGN KEY (`client_id`) REFERENCES `login_data` (`id`),
  CONSTRAINT `fk_ticket_place1` FOREIGN KEY (`place_id`) REFERENCES `place` (`id`),
  CONSTRAINT `fk_ticket_seance1` FOREIGN KEY (`seance_id`) REFERENCES `seance` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket`
--

LOCK TABLES `ticket` WRITE;
/*!40000 ALTER TABLE `ticket` DISABLE KEYS */;
INSERT INTO `ticket` VALUES (6,1,1,'TAKEN',3,'Dmytro1683778253036'),(7,1,2,'TAKEN',33,'Dmytro_a1683778253037'),(8,1,3,'FREE',NULL,NULL),(9,1,5,'TAKEN',33,'Dmytro_a1683778253038'),(10,1,6,'TAKEN',3,'Dmytro1645896325471'),(28,12,14,'TAKEN',3,'Dmytro1683728521651'),(29,12,15,'TAKEN',3,'Dmytro1684435428703'),(30,12,16,'TAKEN',3,'Dmytro1683728521666'),(62,14,7,'TAKEN',33,'Dmytro_a1684770108488'),(63,14,9,'TAKEN',3,'Dmytro1684834756508'),(64,14,11,'TAKEN',3,'Dmytro1684636556435'),(65,14,12,'TAKEN',3,'Dmytro1684636556439'),(97,20,7,'TAKEN',3,'Dmytro1684364091335'),(98,20,9,'TAKEN',3,'Dmytro1684364091339'),(99,20,11,'TAKEN',3,'Dmytro1684364091342'),(100,20,12,'FREE',NULL,NULL),(101,21,7,'FREE',NULL,NULL),(102,21,9,'TAKEN',3,'Dmytro1684364101414'),(103,21,11,'FREE',NULL,NULL),(104,21,12,'FREE',NULL,NULL),(113,24,7,'TAKEN',33,'Dmytro_a1684770108489'),(114,24,9,'TAKEN',33,'Dmytro_a1684770108491'),(115,24,11,'TAKEN',33,'Dmytro_a1684770108492'),(116,24,12,'FREE',NULL,NULL),(134,30,1,'TAKEN',38,'Volodymyr1685039134041'),(135,30,2,'FREE',NULL,NULL),(136,30,3,'TAKEN',38,'Volodymyr1685039134056'),(137,30,5,'FREE',NULL,NULL),(138,30,6,'FREE',NULL,NULL),(139,31,7,'FREE',NULL,NULL),(140,31,9,'TAKEN',33,'Dmytro_a1684770108493'),(141,31,11,'TAKEN',33,'Dmytro_a1684770108494'),(142,31,12,'FREE',NULL,NULL),(143,32,14,'FREE',NULL,NULL),(144,32,15,'FREE',NULL,NULL),(145,32,16,'FREE',NULL,NULL),(159,36,7,'FREE',NULL,NULL),(160,36,9,'FREE',NULL,NULL),(161,36,11,'FREE',NULL,NULL),(162,36,12,'FREE',NULL,NULL),(163,37,7,'TAKEN',33,'Dmytro_a1684770108495'),(164,37,9,'FREE',NULL,NULL),(165,37,11,'FREE',NULL,NULL),(166,37,12,'FREE',NULL,NULL),(167,38,1,'FREE',NULL,NULL),(168,38,2,'TAKEN',33,'Dmytro_a1684770108496'),(169,38,3,'TAKEN',33,'Dmytro_a1684770108497'),(170,38,5,'FREE',NULL,NULL),(171,38,6,'FREE',NULL,NULL),(172,39,7,'TAKEN',33,'Dmytro_a1684770108498'),(173,39,9,'TAKEN',33,'Dmytro_a1684770108499'),(174,39,11,'FREE',NULL,NULL),(175,39,12,'TAKEN',33,'Dmytro_a1684770108500'),(188,42,1,'FREE',NULL,NULL),(189,42,2,'FREE',NULL,NULL),(190,42,3,'FREE',NULL,NULL),(191,42,5,'FREE',NULL,NULL),(192,42,6,'TAKEN',3,'Dmytro1685616041666'),(193,43,1,'TAKEN',3,'Dmytro1685973371305'),(194,43,2,'TAKEN',3,'Dmytro1685973371319'),(195,43,3,'FREE',NULL,NULL),(196,43,5,'FREE',NULL,NULL),(197,43,6,'FREE',NULL,NULL),(198,44,1,'FREE',NULL,NULL),(199,44,2,'FREE',NULL,NULL),(200,44,3,'FREE',NULL,NULL),(201,44,5,'FREE',NULL,NULL),(202,44,6,'FREE',NULL,NULL),(203,45,7,'FREE',NULL,NULL),(204,45,9,'FREE',NULL,NULL),(205,45,11,'FREE',NULL,NULL),(206,45,12,'FREE',NULL,NULL),(207,46,7,'FREE',NULL,NULL),(208,46,9,'FREE',NULL,NULL),(209,46,11,'FREE',NULL,NULL),(210,46,12,'FREE',NULL,NULL),(211,47,1,'FREE',NULL,NULL),(212,47,2,'FREE',NULL,NULL),(213,47,3,'FREE',NULL,NULL),(214,47,5,'FREE',NULL,NULL),(215,47,6,'FREE',NULL,NULL);
/*!40000 ALTER TABLE `ticket` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ticket_addition`
--

DROP TABLE IF EXISTS `ticket_addition`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ticket_addition` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `addition_id` bigint unsigned NOT NULL,
  `ticket_id` bigint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_ticket_addition_addition1_idx` (`addition_id`),
  KEY `fk_ticket_addition_ticket1_idx` (`ticket_id`),
  CONSTRAINT `fk_ticket_addition_addition1` FOREIGN KEY (`addition_id`) REFERENCES `addition` (`id`),
  CONSTRAINT `fk_ticket_addition_ticket1` FOREIGN KEY (`ticket_id`) REFERENCES `ticket` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=251 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ticket_addition`
--

LOCK TABLES `ticket_addition` WRITE;
/*!40000 ALTER TABLE `ticket_addition` DISABLE KEYS */;
INSERT INTO `ticket_addition` VALUES (1,2,0),(2,5,0),(3,1,0),(4,6,10),(95,1,28),(96,2,28),(97,5,30),(98,3,30),(99,39,30),(162,1,97),(163,2,97),(164,3,98),(165,7,98),(166,39,98),(167,41,99),(168,1,99),(169,1,102),(170,4,64),(171,4,65),(182,7,63),(183,3,63),(184,4,63),(185,39,63),(186,2,63),(187,1,63),(188,41,63),(207,2,134),(208,2,136),(209,1,134),(210,1,136),(211,39,134),(226,4,192),(228,7,192),(236,1,193),(237,2,193),(238,39,194);
/*!40000 ALTER TABLE `ticket_addition` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-06-07 23:20:29
