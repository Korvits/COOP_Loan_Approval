// LoanApplication.java
package com.example.loan.entity;

import com.example.loan.entity.enums.ApplicationStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loan_applications")
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 32)
    private String firstName;

    @Column(nullable = false, length = 32)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String personalCode;

    @Column(nullable = false)
    private Integer loanPeriodMonths;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal interestMargin;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal baseInterestRate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal loanAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    @Column
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("paymentNumber ASC")
    private List<PaymentScheduleEntry> paymentSchedule = new ArrayList<>();

    // Getters & Setters
    public UUID getId() { return id; }
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
    public List<PaymentScheduleEntry> getPaymentSchedule() { return paymentSchedule; }
    public void setPaymentSchedule(List<PaymentScheduleEntry> paymentSchedule) { this.paymentSchedule = paymentSchedule; }
}