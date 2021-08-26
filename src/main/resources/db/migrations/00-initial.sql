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
-- Table structure for table `annual_historical_financial`
--

DROP TABLE IF EXISTS `annual_historical_financial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annual_historical_financial` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `account_payable` double DEFAULT NULL,
  `account_receivable` double DEFAULT NULL,
  `advertising_promotion` double DEFAULT NULL,
  `assets` double DEFAULT NULL,
  `capital_expenditure` double DEFAULT NULL,
  `cash` double DEFAULT NULL,
  `cash_flow_operation` double DEFAULT NULL,
  `cash_from_financing` double DEFAULT NULL,
  `cash_from_investing` double DEFAULT NULL,
  `closing_cash_balance` double DEFAULT NULL,
  `cost_good_sold` double DEFAULT NULL,
  `depreciation_amortization` double NOT NULL,
  `earning_before_interest_taxes` double DEFAULT NULL,
  `earning_before_taxes` double DEFAULT NULL,
  `ebitda` double DEFAULT NULL,
  `gross_profit` double DEFAULT NULL,
  `interest_expanse` double DEFAULT NULL,
  `inventory` double DEFAULT NULL,
  `long_term_debt` double DEFAULT NULL,
  `net_income` double DEFAULT NULL,
  `other_expenses` double DEFAULT NULL,
  `research_develop_cost` double DEFAULT NULL,
  `salaries_benefits_wages` double DEFAULT NULL,
  `selling_general_administrative_expenses` double DEFAULT NULL,
  `short_term_debt` double DEFAULT NULL,
  `taxes` double DEFAULT NULL,
  `total_assets` double DEFAULT NULL,
  `total_current_assets` double DEFAULT NULL,
  `total_current_liabilities` double DEFAULT NULL,
  `total_fixed_assets` double DEFAULT NULL,
  `total_liabilities` double DEFAULT NULL,
  `total_liabilities_equity` double DEFAULT NULL,
  `total_non_current_liabilities` double DEFAULT NULL,
  `total_operating_expenses` double DEFAULT NULL,
  `total_revenue` double DEFAULT NULL,
  `total_shareholders_equity` double DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  `issuer_financial` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKd7ag5sme3533ag7ies0ohbt12` (`issuer_financial`),
  CONSTRAINT `FKd7ag5sme3533ag7ies0ohbt12` FOREIGN KEY (`issuer_financial`) REFERENCES `issuer_financial` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `annual_projected_financial`
--

DROP TABLE IF EXISTS `annual_projected_financial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `annual_projected_financial` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `revenue` double DEFAULT NULL,
  `account_receivable_amount` double DEFAULT NULL,
  `accounts_payable` double DEFAULT NULL,
  `capital_expenditure` double DEFAULT NULL,
  `cost_of_good_sold` double DEFAULT NULL,
  `depreciation_amortization` double DEFAULT NULL,
  `ebitda` double DEFAULT NULL,
  `employee_benefit` double DEFAULT NULL,
  `equity_fundraising_plans` double DEFAULT NULL,
  `gross_profit` double NOT NULL,
  `gross_profit_margin` double DEFAULT NULL,
  `interest_payment` double DEFAULT NULL,
  `inventory` double DEFAULT NULL,
  `marketing_expenses` double DEFAULT NULL,
  `net_profit` double DEFAULT NULL,
  `other_expenses` double NOT NULL,
  `principal_repayment` double DEFAULT NULL,
  `research_development` double NOT NULL,
  `selling_general_admin` double NOT NULL,
  `tax_payment` double DEFAULT NULL,
  `total_debt` double DEFAULT NULL,
  `total_shareholders_equity` double DEFAULT NULL,
  `total_taxable_income` double DEFAULT NULL,
  `year` int DEFAULT NULL,
  `issuer_financial` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKc1c5t5de0ipxowmuhggd68p3w` (`issuer_financial`),
  CONSTRAINT `FKc1c5t5de0ipxowmuhggd68p3w` FOREIGN KEY (`issuer_financial`) REFERENCES `issuer_financial` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `base_rate`
--

DROP TABLE IF EXISTS `base_rate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_rate` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `is_fixed` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `base_rate_curve`
--

DROP TABLE IF EXISTS `base_rate_curve`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `base_rate_curve` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `base_rate` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2ynwu33i8d3aaw6976urmjx2i` (`base_rate`),
  CONSTRAINT `FK2ynwu33i8d3aaw6976urmjx2i` FOREIGN KEY (`base_rate`) REFERENCES `base_rate` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `call_premium`
--

DROP TABLE IF EXISTS `call_premium`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `call_premium` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `percentage` double DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq15h469hjpmebrl04k43otoly` (`debt_model_id`),
  CONSTRAINT `FKq15h469hjpmebrl04k43otoly` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cashflow`
--

DROP TABLE IF EXISTS `cashflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashflow` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `day_count_convention` int DEFAULT NULL,
  `discount_rate` double DEFAULT NULL,
  `exit_date` date DEFAULT NULL,
  `internal_rate_of_return` double DEFAULT NULL,
  `maturity_date` date DEFAULT NULL,
  `origination_date` date DEFAULT NULL,
  `percentage_par` double DEFAULT NULL,
  `percentage_par_exit` double DEFAULT NULL,
  `present_value_sum` double DEFAULT NULL,
  `present_value_sum_exit` double DEFAULT NULL,
  `valuation_date` date DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKij2y9aj46cpwht5criq82rbyd` (`debt_model_id`),
  CONSTRAINT `FKij2y9aj46cpwht5criq82rbyd` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cashflow_schedule`
--

DROP TABLE IF EXISTS `cashflow_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cashflow_schedule` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `annual_fee_percentage` double DEFAULT NULL,
  `base_rate` double DEFAULT NULL,
  `base_rate_spread` double DEFAULT NULL,
  `call_premium_amount` double DEFAULT NULL,
  `call_premium_rate` double DEFAULT NULL,
  `committed_capital` double DEFAULT NULL,
  `date_type` int DEFAULT NULL,
  `deal_fees_outflow` double DEFAULT NULL,
  `discounting_factor` double DEFAULT NULL,
  `from_date` date DEFAULT NULL,
  `interest_outflow` double DEFAULT NULL,
  `interest_undrawn_capital_outflow` double DEFAULT NULL,
  `interest_undrawn_percentage` double DEFAULT NULL,
  `opening_principal_outstanding` double DEFAULT NULL,
  `partial_period` double DEFAULT NULL,
  `present_value` double DEFAULT NULL,
  `principal_inflow` double DEFAULT NULL,
  `principal_repayment` double DEFAULT NULL,
  `skim_percentage` double DEFAULT NULL,
  `skims_outflow` double DEFAULT NULL,
  `to_date` date DEFAULT NULL,
  `total_cash_movement` double DEFAULT NULL,
  `total_interest_rate` double DEFAULT NULL,
  `total_principal_outstanding` double DEFAULT NULL,
  `undrawn_capital` double DEFAULT NULL,
  `cashflow_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKimtu92611o3t162dj64fkucw3` (`cashflow_id`),
  CONSTRAINT `FKimtu92611o3t162dj64fkucw3` FOREIGN KEY (`cashflow_id`) REFERENCES `cashflow` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cutomizable_cashflow`
--

DROP TABLE IF EXISTS `cutomizable_cashflow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cutomizable_cashflow` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `cashflow_amount` int DEFAULT NULL,
  `cashflow_base_custom_amount` double DEFAULT NULL,
  `cashflow_computation_base` varchar(255) DEFAULT NULL,
  `cashflow_dates` int DEFAULT NULL,
  `cashflow_fixed_amount` double DEFAULT NULL,
  `cashflow_payment_mode` varchar(255) DEFAULT NULL,
  `cashflow_percentage` double DEFAULT NULL,
  `cashflow_type` int DEFAULT NULL,
  `date_selection` date DEFAULT NULL,
  `day_of_payment` int DEFAULT NULL,
  `first_payment_date` date DEFAULT NULL,
  `frequency` int DEFAULT NULL,
  `name_of_the_property` varchar(255) DEFAULT NULL,
  `regime_end_date` date DEFAULT NULL,
  `regime_start_date` date DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrrmui8tqqmywvkpndqli4psqo` (`debt_model_id`),
  CONSTRAINT `FKrrmui8tqqmywvkpndqli4psqo` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `cutomizable_cashflow_excel`
--

DROP TABLE IF EXISTS `cutomizable_cashflow_excel`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cutomizable_cashflow_excel` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `cashflow_dates` int DEFAULT NULL,
  `cashflow_type` int DEFAULT NULL,
  `name_of_the_property` varchar(255) DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKmwcefouv31o4482x0kkk6wx2h` (`debt_model_id`),
  CONSTRAINT `FKmwcefouv31o4482x0kkk6wx2h` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `deal_fees`
--

DROP TABLE IF EXISTS `deal_fees`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deal_fees` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `annual_fee_percentage` double DEFAULT NULL,
  `day_of_payment` int DEFAULT NULL,
  `fee_base` int DEFAULT NULL,
  `first_payment_date` date DEFAULT NULL,
  `interest_payment_frequency` int DEFAULT NULL,
  `regime_end_date` date DEFAULT NULL,
  `regime_start_date` date DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq7r3ccmyqjbx5yvtc7rff8gdq` (`debt_model_id`),
  CONSTRAINT `FKq7r3ccmyqjbx5yvtc7rff8gdq` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `debt_model`
--

DROP TABLE IF EXISTS `debt_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debt_model` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `analysis_date` datetime(6) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `fund_id` bigint DEFAULT NULL,
  `fund_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discount_adjustment`
--

DROP TABLE IF EXISTS `discount_adjustment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount_adjustment` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `adjustment_name` varchar(255) NOT NULL,
  `medium` int DEFAULT NULL,
  `quarter1` int DEFAULT NULL,
  `quarter3` int DEFAULT NULL,
  `discount_adjustment` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKgcdvbpgnqt2qvrwpoj0gf68qb` (`discount_adjustment`),
  CONSTRAINT `FKgcdvbpgnqt2qvrwpoj0gf68qb` FOREIGN KEY (`discount_adjustment`) REFERENCES `discount_rate_computation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `discount_rate_computation`
--

DROP TABLE IF EXISTS `discount_rate_computation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discount_rate_computation` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `concluded_credit_spread_medium` double DEFAULT NULL,
  `concluded_credit_spread_quarter1` double DEFAULT NULL,
  `concluded_credit_spread_quarter3` double DEFAULT NULL,
  `risk_free_rate_medium` double DEFAULT NULL,
  `risk_free_rate_quarter1` double DEFAULT NULL,
  `risk_free_rate_quarter3` double DEFAULT NULL,
  `ytm_medium` double DEFAULT NULL,
  `ytm_quarter1` double DEFAULT NULL,
  `ytm_quarter3` double DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1rcbbrcajhhwgcfipahe693y6` (`debt_model_id`),
  CONSTRAINT `FK1rcbbrcajhhwgcfipahe693y6` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `general_details`
--

DROP TABLE IF EXISTS `general_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `general_details` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `currency` varchar(255) NOT NULL,
  `day_count_convention` int NOT NULL,
  `description` longtext,
  `discount_rate` double DEFAULT NULL,
  `exit_date` date DEFAULT NULL,
  `geography` varchar(255) DEFAULT NULL,
  `issuer_name` varchar(255) NOT NULL,
  `maturity_date` date DEFAULT NULL,
  `origination_date` date DEFAULT NULL,
  `percentage_of_called_down` double DEFAULT NULL,
  `portfolio_company_name` varchar(255) NOT NULL,
  `principal_amount` double NOT NULL,
  `principal_outstanding` double NOT NULL,
  `sector` varchar(255) DEFAULT NULL,
  `security_name` varchar(255) DEFAULT NULL,
  `valuation_date` date DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `website` longtext,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcqp0f6n4jn0lhl08k2wqpj3ig` (`debt_model_id`),
  CONSTRAINT `FKcqp0f6n4jn0lhl08k2wqpj3ig` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interest_details`
--

DROP TABLE IF EXISTS `interest_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interest_details` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `base_rate_cap` double DEFAULT NULL,
  `base_rate_floor` double DEFAULT NULL,
  `base_rate_spread` double DEFAULT NULL,
  `first_interest_payment_date` date DEFAULT NULL,
  `fixed_base_rate` double DEFAULT NULL,
  `interest_paid_or_accrued` int DEFAULT NULL,
  `interest_payment_day` int DEFAULT NULL,
  `interest_payment_frequency` int DEFAULT NULL,
  `interest_payment_type` int DEFAULT NULL,
  `regime_end_date` date DEFAULT NULL,
  `regime_start_date` date DEFAULT NULL,
  `version_id` int DEFAULT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interest_undrawn_capital`
--

DROP TABLE IF EXISTS `interest_undrawn_capital`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interest_undrawn_capital` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `day_of_payment` int DEFAULT NULL,
  `first_payment_date` date DEFAULT NULL,
  `interest_payment_frequency` int DEFAULT NULL,
  `interest_undrawn_payment_percentage` double DEFAULT NULL,
  `regime_end_date` date DEFAULT NULL,
  `regime_start_date` date DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK136es5el24rg2np6i1h0427q0` (`debt_model_id`),
  CONSTRAINT `FK136es5el24rg2np6i1h0427q0` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `interim_payment_details`
--

DROP TABLE IF EXISTS `interim_payment_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `interim_payment_details` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `amount` double NOT NULL,
  `date` date NOT NULL,
  `cutomizable_cashflow_excel` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKajnpec21hi8aab6sm35c74ldq` (`cutomizable_cashflow_excel`),
  CONSTRAINT `FKajnpec21hi8aab6sm35c74ldq` FOREIGN KEY (`cutomizable_cashflow_excel`) REFERENCES `cutomizable_cashflow_excel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `issuer_financial`
--

DROP TABLE IF EXISTS `issuer_financial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `issuer_financial` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `debt_senior_issue` double DEFAULT NULL,
  `enterprise_value` double DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1crtrs326nkqyncl3je77427h` (`debt_model_id`),
  CONSTRAINT `FK1crtrs326nkqyncl3je77427h` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `look_up_debt_details`
--

DROP TABLE IF EXISTS `look_up_debt_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `look_up_debt_details` (
  `company_id` varchar(255) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `company_name` varchar(255) DEFAULT NULL,
  `debt_id` bigint DEFAULT NULL,
  `fund_id` varchar(255) DEFAULT NULL,
  `fund_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `look_up_valuation_details`
--

DROP TABLE IF EXISTS `look_up_valuation_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `look_up_valuation_details` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `valuation_date` date DEFAULT NULL,
  `valuation_date_id` varchar(255) DEFAULT NULL,
  `version` int DEFAULT NULL,
  `company_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq7l8bs6p5s5c1jcxhf4277jh4` (`company_id`),
  CONSTRAINT `FKq7l8bs6p5s5c1jcxhf4277jh4` FOREIGN KEY (`company_id`) REFERENCES `look_up_debt_details` (`company_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `payment_schedule`
--

DROP TABLE IF EXISTS `payment_schedule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payment_schedule` (
  `id` bigint NOT NULL,
  `amount` double NOT NULL,
  `date` date NOT NULL,
  `prepayment_details` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKa93m7bd3gvbr0rvdovdbf0hgh` (`prepayment_details`),
  CONSTRAINT `FKa93m7bd3gvbr0rvdovdbf0hgh` FOREIGN KEY (`prepayment_details`) REFERENCES `prepayment_details` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `prepayment_details`
--

DROP TABLE IF EXISTS `prepayment_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `prepayment_details` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `principal_repayment_pattern` int NOT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqag710v35pk2yk82amvfym92q` (`debt_model_id`),
  CONSTRAINT `FKqag710v35pk2yk82amvfym92q` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `skims`
--

DROP TABLE IF EXISTS `skims`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `skims` (
  `id` bigint NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `created_by` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `updated_by` varchar(255) DEFAULT NULL,
  `day_of_payment` int DEFAULT NULL,
  `first_payment_date` date DEFAULT NULL,
  `outstanding_balance_of_valuation_date` double DEFAULT NULL,
  `regime_end_date` date DEFAULT NULL,
  `regime_start_date` date DEFAULT NULL,
  `skim_base` int DEFAULT NULL,
  `skim_payment_frequency` int DEFAULT NULL,
  `skim_percentage` double DEFAULT NULL,
  `version_id` int DEFAULT NULL,
  `debt_model_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3bn7ry0eheey69si36o19x8u8` (`debt_model_id`),
  CONSTRAINT `FK3bn7ry0eheey69si36o19x8u8` FOREIGN KEY (`debt_model_id`) REFERENCES `debt_model` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Add a default Hibernate sequence
INSERT INTO `hibernate_sequence` (`next_val`) VALUES ('1');

-- Dump completed on 2021-08-23 13:23:20
