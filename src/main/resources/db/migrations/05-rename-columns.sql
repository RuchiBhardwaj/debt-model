-- Discount Adjustments
ALTER TABLE `discount_adjustment` RENAME COLUMN `medium` to `median`;
ALTER TABLE `discount_adjustment` RENAME COLUMN `quarter1` to `quartile1`;
ALTER TABLE `discount_adjustment` RENAME COLUMN `quarter3` to `quartile3`;

-- Discount Rate Computation
ALTER TABLE `discount_rate_computation` RENAME COLUMN `concluded_credit_spread_medium` to `concluded_credit_spread_median`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `concluded_credit_spread_quarter1` to `concluded_credit_spread_quartile1`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `concluded_credit_spread_quarter3` to `concluded_credit_spread_quartile3`;

ALTER TABLE `discount_rate_computation` RENAME COLUMN `risk_free_rate_medium` to `risk_free_rate_median`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `risk_free_rate_quarter1` to `risk_free_rate_quartile1`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `risk_free_rate_quarter3` to `risk_free_rate_quartile3`;

ALTER TABLE `discount_rate_computation` RENAME COLUMN `ytm_medium` to `ytm_median`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `ytm_quarter1` to `ytm_quartile1`;
ALTER TABLE `discount_rate_computation` RENAME COLUMN `ytm_quarter3` to `ytm_quartile3`;

-- Interest Undrawn Capital
ALTER TABLE `interest_undrawn_capital` RENAME COLUMN `interest_undrawn_payment_percentage` to `interest_undrawn_percentage`;

-- Interim Payment Details
ALTER TABLE `interim_payment_details` RENAME COLUMN `cutomizable_cashflow_excel` to `customizable_cashflow_excel`;

-- Payment Schedule
ALTER TABLE `payment_schedule` RENAME COLUMN `prepayment_details` to `repayment_details`;