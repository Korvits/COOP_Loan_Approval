// LoanApplicationDetailsDTO.java
package com.example.loan.dto;

import com.example.loan.entity.enums.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LoanApplicationDetailsDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String personalCode;
    private Integer loanPeriodMonths;
    private BigDecimal interestMargin;
    private BigDecimal baseInterestRate;
    private BigDecimal loanAmount;
    private ApplicationStatus status;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private List<PaymentScheduleEntryDTO> paymentSchedule;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
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
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<PaymentScheduleEntryDTO> getPaymentSchedule() { return paymentSchedule; }
    public void setPaymentSchedule(List<PaymentScheduleEntryDTO> paymentSchedule) { this.paymentSchedule = paymentSchedule; }
}