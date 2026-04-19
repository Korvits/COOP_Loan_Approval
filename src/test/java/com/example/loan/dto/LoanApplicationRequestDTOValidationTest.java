package com.example.loan.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class LoanApplicationRequestDTOValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void validRequest_noViolations() {
        assertThat(violations(validRequest())).isEmpty();
    }

    @Test
    void firstNameTooLong_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setFirstName("A".repeat(33));
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void lastNameTooLong_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setLastName("B".repeat(33));
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void invalidPersonalCode_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setPersonalCode("99999999999");
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void loanPeriodTooShort_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setLoanPeriodMonths(5);
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void loanPeriodTooLong_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setLoanPeriodMonths(361);
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void negativeInterestMargin_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setInterestMargin(new BigDecimal("-0.1"));
        assertThat(violations(req)).isNotEmpty();
    }

    @Test
    void loanAmountBelowMinimum_hasViolation() {
        LoanApplicationRequestDTO req = validRequest();
        req.setLoanAmount(new BigDecimal("4999.99"));
        assertThat(violations(req)).isNotEmpty();
    }

    private Set<ConstraintViolation<LoanApplicationRequestDTO>> violations(LoanApplicationRequestDTO dto) {
        return validator.validate(dto);
    }

    private LoanApplicationRequestDTO validRequest() {
        LoanApplicationRequestDTO req = new LoanApplicationRequestDTO();
        req.setFirstName("Mart");
        req.setLastName("Tamm");
        req.setPersonalCode("38001011234");
        req.setLoanPeriodMonths(12);
        req.setBaseInterestRate(new BigDecimal("5.0"));
        req.setInterestMargin(new BigDecimal("2.0"));
        req.setLoanAmount(new BigDecimal("10000.00"));
        return req;
    }
}