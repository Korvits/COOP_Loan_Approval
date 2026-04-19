// LoanApplicationRequestDTO.java
package com.example.loan.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class LoanApplicationRequestDTO {

    @NotBlank @Size(max = 32)
    private String firstName;

    @NotBlank @Size(max = 32)
    private String lastName;

    @NotBlank
    @Pattern(
            regexp = "^[1-6][0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[0-9]{4}$",
            message = "Invalid Estonian personal code format"
    )
    private String personalCode;

    @NotNull @Min(6) @Max(360)
    private Integer loanPeriodMonths;

    @NotNull @DecimalMin("0.0")
    private BigDecimal interestMargin;

    @NotNull @DecimalMin("0.0")
    private BigDecimal baseInterestRate;

    @NotNull @DecimalMin("5000.0")
    private BigDecimal loanAmount;

    // Getters & Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getPersonalCode() { return personalCode; }
    public void setPersonalCode(String personalCode) { this.personalCode = personalCode; }
    public Integer getLoanPeriodMonths() { return loanPeriodMonths; }
    public void setLoanPeriodMonths(Integer loanPeriodMonths) { this.loanPeriodMonths = loanPeriodMonths; }
    public BigDecimal getInterestMargin() { return interestMargin; }
    public void setInterestMargin(BigDecimal interestMargin) { this.interestMargin = interestMargin; }
    public BigDecimal getBaseInterestRate() { return baseInterestRate; }
    public void setBaseInterestRate(BigDecimal baseInterestRate) { this.baseInterestRate = baseInterestRate; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
}