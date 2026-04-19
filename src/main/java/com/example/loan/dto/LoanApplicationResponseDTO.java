// LoanApplicationResponseDTO.java
package com.example.loan.dto;

import com.example.loan.entity.enums.ApplicationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class LoanApplicationResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private ApplicationStatus status;
    private String rejectionReason;
    private BigDecimal loanAmount;
    private Integer loanPeriodMonths;
    private LocalDateTime createdAt;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
    public Integer getLoanPeriodMonths() { return loanPeriodMonths; }
    public void setLoanPeriodMonths(Integer loanPeriodMonths) { this.loanPeriodMonths = loanPeriodMonths; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}