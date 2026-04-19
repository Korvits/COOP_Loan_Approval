// PaymentScheduleEntryRepository.java
package com.example.loan.repository;

import com.example.loan.entity.PaymentScheduleEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface PaymentScheduleEntryRepository extends JpaRepository<PaymentScheduleEntry, UUID> {
    void deleteByLoanApplicationId(UUID loanApplicationId);
}