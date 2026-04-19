// LoanApplicationService.java
package com.example.loan.service;

import com.example.loan.dto.*;
import com.example.loan.entity.LoanApplication;
import com.example.loan.entity.PaymentScheduleEntry;
import com.example.loan.entity.enums.ApplicationStatus;
import com.example.loan.exception.BusinessException;
import com.example.loan.exception.ResourceNotFoundException;
import com.example.loan.repository.LoanApplicationRepository;
import com.example.loan.repository.PaymentScheduleEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoanApplicationService {

    private final LoanApplicationRepository repository;
    private final PaymentScheduleEntryRepository scheduleRepository;
    private final LoanProcessService processService;

    public LoanApplicationService(
            LoanApplicationRepository repository,
            PaymentScheduleEntryRepository scheduleRepository,
            LoanProcessService processService) {
        this.repository = repository;
        this.scheduleRepository = scheduleRepository;
        this.processService = processService;
    }

    @Transactional
    public LoanApplicationResponseDTO createApplication(LoanApplicationRequestDTO request) {
        // Check for existing active application
        boolean hasActive = repository.existsByPersonalCodeAndStatusNotIn(
                request.getPersonalCode(),
                List.of(ApplicationStatus.APPROVED, ApplicationStatus.REJECTED)
        );
        if (hasActive) {
            throw new BusinessException("Customer already has an active loan application.");
        }

        LoanApplication app = new LoanApplication();
        app.setFirstName(request.getFirstName());
        app.setLastName(request.getLastName());
        app.setPersonalCode(request.getPersonalCode());
        app.setLoanPeriodMonths(request.getLoanPeriodMonths());
        app.setInterestMargin(request.getInterestMargin());
        app.setBaseInterestRate(request.getBaseInterestRate());
        app.setLoanAmount(request.getLoanAmount());

        processService.process(app);

        LoanApplication saved = repository.save(app);
        return toResponseDTO(saved);
    }

    public LoanApplicationDetailsDTO getApplicationDetails(UUID id) {
        LoanApplication app = findOrThrow(id);
        return toDetailsDTO(app);
    }

    @Transactional
    public void approveApplication(UUID id) {
        LoanApplication app = findOrThrow(id);
        if (app.getStatus() != ApplicationStatus.IN_REVIEW) {
            throw new BusinessException("Application can only be approved when IN_REVIEW. Current status: " + app.getStatus());
        }
        app.setStatus(ApplicationStatus.APPROVED);
        repository.save(app);
    }

    @Transactional
    public void rejectApplication(UUID id, String reason) {
        LoanApplication app = findOrThrow(id);
        if (app.getStatus() == ApplicationStatus.APPROVED || app.getStatus() == ApplicationStatus.REJECTED) {
            throw new BusinessException("Cannot reject a finalized application. Current status: " + app.getStatus());
        }
        app.setStatus(ApplicationStatus.REJECTED);
        app.setRejectionReason(reason);
        repository.save(app);
    }

    @Transactional
    public LoanApplicationDetailsDTO regenerateSchedule(UUID id, RegenerateScheduleRequestDTO request) {
        LoanApplication app = findOrThrow(id);
        if (app.getStatus() != ApplicationStatus.IN_REVIEW) {
            throw new BusinessException("Schedule can only be regenerated when IN_REVIEW. Current status: " + app.getStatus());
        }

        app.setLoanAmount(request.getLoanAmount());
        app.setLoanPeriodMonths(request.getLoanPeriodMonths());
        app.setBaseInterestRate(request.getBaseInterestRate());
        app.setInterestMargin(request.getInterestMargin());

        app.getPaymentSchedule().clear();
        List<PaymentScheduleEntry> newSchedule = processService.generateAnnuitySchedule(app);
        app.getPaymentSchedule().addAll(newSchedule);

        LoanApplication saved = repository.save(app);
        return toDetailsDTO(saved);
    }

    private LoanApplication findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found: " + id));
    }

    private LoanApplicationResponseDTO toResponseDTO(LoanApplication app) {
        LoanApplicationResponseDTO dto = new LoanApplicationResponseDTO();
        dto.setId(app.getId());
        dto.setFirstName(app.getFirstName());
        dto.setLastName(app.getLastName());
        dto.setStatus(app.getStatus());
        dto.setRejectionReason(app.getRejectionReason());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setLoanPeriodMonths(app.getLoanPeriodMonths());
        dto.setCreatedAt(app.getCreatedAt());
        return dto;
    }

    private LoanApplicationDetailsDTO toDetailsDTO(LoanApplication app) {
        LoanApplicationDetailsDTO dto = new LoanApplicationDetailsDTO();
        dto.setId(app.getId());
        dto.setFirstName(app.getFirstName());
        dto.setLastName(app.getLastName());
        dto.setPersonalCode(app.getPersonalCode());
        dto.setLoanPeriodMonths(app.getLoanPeriodMonths());
        dto.setInterestMargin(app.getInterestMargin());
        dto.setBaseInterestRate(app.getBaseInterestRate());
        dto.setLoanAmount(app.getLoanAmount());
        dto.setStatus(app.getStatus());
        dto.setRejectionReason(app.getRejectionReason());
        dto.setCreatedAt(app.getCreatedAt());
        dto.setPaymentSchedule(
                app.getPaymentSchedule().stream()
                        .map(this::toScheduleDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private PaymentScheduleEntryDTO toScheduleDTO(PaymentScheduleEntry e) {
        PaymentScheduleEntryDTO dto = new PaymentScheduleEntryDTO();
        dto.setId(e.getId());
        dto.setPaymentNumber(e.getPaymentNumber());
        dto.setPaymentDate(e.getPaymentDate());
        dto.setTotalPayment(e.getTotalPayment());
        dto.setPrincipalPayment(e.getPrincipalPayment());
        dto.setInterestPayment(e.getInterestPayment());
        dto.setRemainingBalance(e.getRemainingBalance());
        return dto;
    }
}