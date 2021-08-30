-- Discount Adjustments
ALTER TABLE `discount_adjustment` CHANGE COLUMN `medium`  `median` INT(11);


ALTER TABLE `discount_adjustment` CHANGE COLUMN `quarter1`  `quartile1` INT(11) ;
ALTER TABLE `discount_adjustment` CHANGE COLUMN `quarter3`  `quartile3` INT(11);

-- Discount Rate Computation
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `concluded_credit_spread_medium`  `concluded_credit_spread_median` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `concluded_credit_spread_quarter1`  `concluded_credit_spread_quartile1` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `concluded_credit_spread_quarter3`  `concluded_credit_spread_quartile3` DOUBLE;

ALTER TABLE `discount_rate_computation` CHANGE COLUMN `risk_free_rate_medium`  `risk_free_rate_median` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `risk_free_rate_quarter1`  `risk_free_rate_quartile1` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `risk_free_rate_quarter3`  `risk_free_rate_quartile3` DOUBLE;

ALTER TABLE `discount_rate_computation` CHANGE COLUMN `ytm_medium`  `ytm_median` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `ytm_quarter1`  `ytm_quartile1` DOUBLE;
ALTER TABLE `discount_rate_computation` CHANGE COLUMN `ytm_quarter3`  `ytm_quartile3` DOUBLE;

-- Interest Undrawn Capital
ALTER TABLE `interest_undrawn_capital` CHANGE COLUMN `interest_undrawn_payment_percentage`  `interest_undrawn_percentage` DOUBLE;

-- Interim Payment Details
ALTER TABLE `interim_payment_details` CHANGE COLUMN `cutomizable_cashflow_excel`  `customizable_cashflow_excel` BIGINT(20);

-- Payment Schedule
ALTER TABLE `payment_schedule` CHANGE COLUMN `prepayment_details`  `repayment_details` BIGINT(20);