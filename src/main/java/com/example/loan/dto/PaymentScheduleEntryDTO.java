// PaymentScheduleEntryDTO.java
package com.example.loan.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class PaymentScheduleEntryDTO {
    private UUID id;
    private Integer paymentNumber;
    private LocalDate paymentDate;
    private BigDecimal totalPayment;
    private BigDecimal principalPayment;
    private BigDecimal interestPayment;
    private BigDecimal remainingBalance;

    // Getters & Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Integer getPaymentNumber() { return paymentNumber; }
    public void setPaymentNumber(Integer paymentNumber) { this.paymentNumber = paymentNumber; }
    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }
    public BigDecimal getTotalPayment() { return totalPayment; }
    public void setTotalPayment(BigDecimal totalPayment) { this.totalPayment = totalPayment; }
    public BigDecimal getPrincipalPayment() { return principalPayment; }
    public void setPrincipalPayment(BigDecimal principalPayment) { this.principalPayment = principalPayment; }
    public BigDecimal getInterestPayment() { return interestPayment; }
    public void setInterestPayment(BigDecimal interestPayment) { this.interestPayment = interestPayment; }
    public BigDecimal getRemainingBalance() { return remainingBalance; }
    public void setRemainingBalance(BigDecimal remainingBalance) { this.remainingBalance = remainingBalance; }
}