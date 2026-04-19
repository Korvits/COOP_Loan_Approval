package com.example.loan.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class RegenerateScheduleRequestDTO {

    @NotNull @Min(6) @Max(360)
    private Integer loanPeriodMonths;

    @NotNull @DecimalMin("0.0")
    private BigDecimal interestMargin;

    @NotNull @DecimalMin("0.0")
    private BigDecimal baseInterestRate;

    @NotNull @DecimalMin("5000.0")
    private BigDecimal loanAmount;

    public Integer getLoanPeriodMonths() { return loanPeriodMonths; }
    public void setLoanPeriodMonths(Integer loanPeriodMonths) { this.loanPeriodMonths = loanPeriodMonths; }
    public BigDecimal getInterestMargin() { return interestMargin; }
    public void setInterestMargin(BigDecimal interestMargin) { this.interestMargin = interestMargin; }
    public BigDecimal getBaseInterestRate() { return baseInterestRate; }
    public void setBaseInterestRate(BigDecimal baseInterestRate) { this.baseInterestRate = baseInterestRate; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
}