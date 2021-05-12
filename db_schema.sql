-- MySQL dump 10.13  Distrib 8.0.23, for osx10.16 (x86_64)
--
-- Host: localhost    Database: debt_model
-- ------------------------------------------------------
-- Server version	8.0.23

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
-- Table structure for table `base_rate`
--

DROP TABLE IF EXISTS `base_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_rate` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_fixed` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `base_rate`
--

LOCK TABLES `base_rate` WRITE;
/*!40000 ALTER TABLE `base_rate` DISABLE KEYS */;
/*!40000 ALTER TABLE `base_rate` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `base_rate_curve`
--

DROP TABLE IF EXISTS `base_rate_curve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_rate_curve` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `base_rate` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2ynwu33i8d3aaw6976urmjx2i` (`base_rate`),
  CONSTRAINT `FK2ynwu33i8d3aaw6976urmjx2i` FOREIGN KEY (`base_rate`) REFERENCES `base_rate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `base_rate_curve`
--

LOCK TABLES `base_rate_curve` WRITE;
/*!40000 ALTER TABLE `base_rate_curve` DISABLE KEYS */;
/*!40000 ALTER TABLE `base_rate_curve` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashflow`
--

DROP TABLE IF EXISTS `cashflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashflow` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `day_count_convention` int DEFAULT NULL,
  `discount_rate` double DEFAULT NULL,
  `maturity_date` date DEFAULT NULL,
  `origination_date` date DEFAULT NULL,
  `percentage_par` double DEFAULT NULL,
  `present_value_sum` double DEFAULT NULL,
  `valuation_date` date DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKij2y9aj46cpwht5criq82rbyd` (`debt_model_id`),
  CONSTRAINT `FKij2y9aj46cpwht5criq82rbyd` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashflow`
--

LOCK TABLES `cashflow` WRITE;
/*!40000 ALTER TABLE `cashflow` DISABLE KEYS */;
/*!40000 ALTER TABLE `cashflow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cashflow_schedule`
--

DROP TABLE IF EXISTS `cashflow_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashflow_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `base_rate` double DEFAULT NULL,
  `base_rate_spread` double DEFAULT NULL,
  `date_type` int DEFAULT NULL,
  `discounting_factor` double DEFAULT NULL,
  `from_date` date DEFAULT NULL,
  `interest_outflow` double DEFAULT NULL,
  `partial_period` double DEFAULT NULL,
  `present_value` double DEFAULT NULL,
  `principal_inflow` double DEFAULT NULL,
  `principal_repayment` double DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `total_cash_movement` double DEFAULT NULL,
  `total_interest_rate` double DEFAULT NULL,
  `total_principal_outstanding` double DEFAULT NULL,
  `cashflow_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKimtu92611o3t162dj64fkucw3` (`cashflow_id`),
  CONSTRAINT `FKimtu92611o3t162dj64fkucw3` FOREIGN KEY (`cashflow_id`) REFERENCES `cashflow` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cashflow_schedule`
--

LOCK TABLES `cashflow_schedule` WRITE;
/*!40000 ALTER TABLE `cashflow_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `cashflow_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debt_model`
--

DROP TABLE IF EXISTS `debt_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debt_model` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `analysis_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debt_model`
--

LOCK TABLES `debt_model` WRITE;
/*!40000 ALTER TABLE `debt_model` DISABLE KEYS */;
/*!40000 ALTER TABLE `debt_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debt_model_inputs`
--

DROP TABLE IF EXISTS `debt_model_inputs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debt_model_inputs` (
  `debt_model_id` bigint NOT NULL,
  `inputs` int DEFAULT NULL,
  KEY `FKmx0mrh9e5lck29fldahfolbhg` (`debt_model_id`),
  CONSTRAINT `FKmx0mrh9e5lck29fldahfolbhg` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debt_model_inputs`
--

LOCK TABLES `debt_model_inputs` WRITE;
/*!40000 ALTER TABLE `debt_model_inputs` DISABLE KEYS */;
/*!40000 ALTER TABLE `debt_model_inputs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `general_details`
--

DROP TABLE IF EXISTS `general_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `general_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `currency` varchar(255) NOT NULL,
  `day_count_convention` int NOT NULL,
  `debt_security_name` varchar(255) NOT NULL,
  `discount_rate` double DEFAULT NULL,
  `fund_name` varchar(255) NOT NULL,
  `maturity_date` date DEFAULT NULL,
  `origination_date` date DEFAULT NULL,
  `portfolio_company_name` varchar(255) NOT NULL,
  `principal_amount` double NOT NULL,
  `principal_outstanding` double NOT NULL,
  `valuation_date` date DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcqp0f6n4jn0lhl08k2wqpj3ig` (`debt_model_id`),
  CONSTRAINT `FKcqp0f6n4jn0lhl08k2wqpj3ig` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `general_details`
--

LOCK TABLES `general_details` WRITE;
/*!40000 ALTER TABLE `general_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `general_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interest_details`
--

DROP TABLE IF EXISTS `interest_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interest_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `base_rate_cap` double DEFAULT NULL,
  `base_rate_floor` double DEFAULT NULL,
  `base_rate_spread` double DEFAULT NULL,
  `first_interest_payment_date` date DEFAULT NULL,
  `fixed_base_rate` double DEFAULT NULL,
  `has_interest_payment` bit(1) NOT NULL,
  `interest_paid_or_accrued` int DEFAULT NULL,
  `interest_payment_day` int DEFAULT NULL,
  `interest_payment_frequency` int DEFAULT NULL,
  `interest_payment_type` int DEFAULT NULL,
  `last_interest_payment_date` date DEFAULT NULL,
  `base_rate` bigint DEFAULT NULL,
  `base_rate_curve` bigint DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKih4k0aqkccrlsn8fq8mvyvupw` (`base_rate`),
  KEY `FKpsprlk4u51u03sekwfnmvjwvs` (`base_rate_curve`),
  KEY `FKn7aqj2d70qvec8o5e9ycyvkga` (`debt_model_id`),
  CONSTRAINT `FKih4k0aqkccrlsn8fq8mvyvupw` FOREIGN KEY (`base_rate`) REFERENCES `base_rate` (`id`),
  CONSTRAINT `FKn7aqj2d70qvec8o5e9ycyvkga` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`),
  CONSTRAINT `FKpsprlk4u51u03sekwfnmvjwvs` FOREIGN KEY (`base_rate_curve`) REFERENCES `base_rate_curve` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interest_details`
--

LOCK TABLES `interest_details` WRITE;
/*!40000 ALTER TABLE `interest_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `interest_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payment_schedule`
--

DROP TABLE IF EXISTS `payment_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `amount` double NOT NULL,
  `date` date NOT NULL,
  `prepayment_details` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa93m7bd3gvbr0rvdovdbf0hgh` (`prepayment_details`),
  CONSTRAINT `FKa93m7bd3gvbr0rvdovdbf0hgh` FOREIGN KEY (`prepayment_details`) REFERENCES `prepayment_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payment_schedule`
--

LOCK TABLES `payment_schedule` WRITE;
/*!40000 ALTER TABLE `payment_schedule` DISABLE KEYS */;
/*!40000 ALTER TABLE `payment_schedule` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prepayment_details`
--

DROP TABLE IF EXISTS `prepayment_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prepayment_details` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `principal_repayment_pattern` int NOT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqag710v35pk2yk82amvfym92q` (`debt_model_id`),
  CONSTRAINT `FKqag710v35pk2yk82amvfym92q` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prepayment_details`
--

LOCK TABLES `prepayment_details` WRITE;
/*!40000 ALTER TABLE `prepayment_details` DISABLE KEYS */;
/*!40000 ALTER TABLE `prepayment_details` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-12  9:46:50
