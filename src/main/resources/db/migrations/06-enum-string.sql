-- Cashflow Schedule
ALTER TABLE `cashflow_schedule` MODIFY COLUMN `date_type` VARCHAR(255) NULL DEFAULT NULL;

-- Customizable Cashflow
ALTER TABLE `customizable_cashflow` MODIFY COLUMN `cashflow_amount` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `customizable_cashflow` MODIFY COLUMN `cashflow_dates` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `customizable_cashflow` MODIFY COLUMN `cashflow_type` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `customizable_cashflow` MODIFY COLUMN `frequency` VARCHAR(255) NULL DEFAULT NULL;

-- Customizable Cashflow Excel
ALTER TABLE `customizable_cashflow_excel` MODIFY COLUMN `cashflow_dates` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `customizable_cashflow_excel` MODIFY COLUMN `cashflow_type` VARCHAR(255) NULL DEFAULT NULL;

-- Debt Model Inputs
ALTER TABLE `debt_model_inputs` MODIFY COLUMN `inputs` VARCHAR(255) NULL DEFAULT NULL;

-- Discount Adjustments
ALTER TABLE `discount_adjustment` MODIFY COLUMN `median` double DEFAULT 0;
ALTER TABLE `discount_adjustment` MODIFY COLUMN `quartile1` double DEFAULT 0;
ALTER TABLE `discount_adjustment` MODIFY COLUMN `quartile3` double DEFAULT 0;

-- Interest Details
ALTER TABLE `interest_details` MODIFY COLUMN `interest_paid_or_accrued` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `interest_details` MODIFY COLUMN `interest_payment_frequency` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `interest_details` MODIFY COLUMN `interest_payment_type` VARCHAR(255) NULL DEFAULT NULL;
ALTER TABLE `interest_details` MODIFY COLUMN `interest_payment_frequency` VARCHAR(255) NULL DEFAULT NULL;

-- Interest Undrawn Capital
ALTER TABLE `interest_undrawn_capital` MODIFY COLUMN `interest_payment_frequency` VARCHAR(255) NULL DEFAULT NULL;

-- Repayment Details
ALTER TABLE `repayment_details` MODIFY COLUMN `principal_repayment_pattern` VARCHAR(255) NULL DEFAULT NULL;

-- Skims
ALTER TABLE `skims` MODIFY COLUMN `skim_payment_frequency` VARCHAR(255) NULL DEFAULT NULL;
