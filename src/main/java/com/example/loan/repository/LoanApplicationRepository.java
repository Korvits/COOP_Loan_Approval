// LoanApplicationRepository.java
package com.example.loan.repository;

import com.example.loan.entity.LoanApplication;
import com.example.loan.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {
    boolean existsByPersonalCodeAndStatusNotIn(String personalCode, java.util.List<ApplicationStatus> statuses);
    Optional<LoanApplication> findByPersonalCodeAndStatusNotIn(String personalCode, java.util.List<ApplicationStatus> statuses);
}