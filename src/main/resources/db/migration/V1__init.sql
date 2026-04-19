-- V1__init.sql
CREATE TABLE loan_applications (
                                   id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                   first_name VARCHAR(32) NOT NULL,
                                   last_name VARCHAR(32) NOT NULL,
                                   personal_code VARCHAR(11) NOT NULL UNIQUE,
                                   loan_period_months INTEGER NOT NULL,
                                   interest_margin NUMERIC(10, 4) NOT NULL,
                                   base_interest_rate NUMERIC(10, 4) NOT NULL,
                                   loan_amount NUMERIC(15, 2) NOT NULL,
                                   status VARCHAR(20) NOT NULL,
                                   rejection_reason VARCHAR(255),
                                   created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE payment_schedule_entries (
                                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                          loan_application_id UUID NOT NULL REFERENCES loan_applications(id) ON DELETE CASCADE,
                                          payment_number INTEGER NOT NULL,
                                          payment_date DATE NOT NULL,
                                          total_payment NUMERIC(15, 2) NOT NULL,
                                          principal_payment NUMERIC(15, 2) NOT NULL,
                                          interest_payment NUMERIC(15, 2) NOT NULL,
                                          remaining_balance NUMERIC(15, 2) NOT NULL
);