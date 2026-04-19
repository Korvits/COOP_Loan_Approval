package com.example.loan.service;

import com.example.loan.dto.LoanApplicationRequestDTO;
import com.example.loan.dto.LoanApplicationResponseDTO;
import com.example.loan.entity.LoanApplication;
import com.example.loan.entity.enums.ApplicationStatus;
import com.example.loan.exception.BusinessException;
import com.example.loan.exception.ResourceNotFoundException;
import com.example.loan.repository.LoanApplicationRepository;
import com.example.loan.repository.PaymentScheduleEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanApplicationServiceTest {

    @Mock
    private LoanApplicationRepository repository;

    @Mock
    private PaymentScheduleEntryRepository scheduleRepository;

    @Mock
    private LoanProcessService processService;

    @InjectMocks
    private LoanApplicationService service;

    private LoanApplicationRequestDTO validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoanApplicationRequestDTO();
        validRequest.setFirstName("Mart");
        validRequest.setLastName("Tamm");
        validRequest.setPersonalCode("38001011234");
        validRequest.setLoanAmount(new BigDecimal("10000.00"));
        validRequest.setLoanPeriodMonths(12);
        validRequest.setBaseInterestRate(new BigDecimal("5.0"));
        validRequest.setInterestMargin(new BigDecimal("2.0"));
    }

    // ── createApplication ─────────────────────────────────────────────────────

    @Test
    void createApplication_noActiveApplication_savesAndReturnsDTO() {
        when(repository.existsByPersonalCodeAndStatusNotIn(any(), any())).thenReturn(false);
        LoanApplication saved = buildSavedApplication(ApplicationStatus.IN_REVIEW);
        when(repository.save(any())).thenReturn(saved);

        LoanApplicationResponseDTO result = service.createApplication(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);
        verify(processService).process(any(LoanApplication.class));
        verify(repository).save(any(LoanApplication.class));
    }

    @Test
    void createApplication_alreadyHasActiveApplication_throwsBusinessException() {
        when(repository.existsByPersonalCodeAndStatusNotIn(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.createApplication(validRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("active loan application");

        verify(repository, never()).save(any());
    }

    // ── approveApplication ────────────────────────────────────────────────────

    @Test
    void approveApplication_statusInReview_setsApproved() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.IN_REVIEW);
        when(repository.findById(id)).thenReturn(Optional.of(app));
        when(repository.save(any())).thenReturn(app);

        service.approveApplication(id);

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        verify(repository).save(app);
    }

    @Test
    void approveApplication_statusNotInReview_throwsBusinessException() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.APPROVED);
        when(repository.findById(id)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> service.approveApplication(id))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("IN_REVIEW");
    }

    @Test
    void approveApplication_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.approveApplication(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── rejectApplication ─────────────────────────────────────────────────────

    @Test
    void rejectApplication_statusInReview_setsRejected() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.IN_REVIEW);
        when(repository.findById(id)).thenReturn(Optional.of(app));
        when(repository.save(any())).thenReturn(app);

        service.rejectApplication(id, "POOR_CREDIT_HISTORY");

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(app.getRejectionReason()).isEqualTo("POOR_CREDIT_HISTORY");
    }

    @Test
    void rejectApplication_alreadyApproved_throwsBusinessException() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.APPROVED);
        when(repository.findById(id)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> service.rejectApplication(id, "OTHER"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("finalized");
    }

    @Test
    void rejectApplication_alreadyRejected_throwsBusinessException() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.REJECTED);
        when(repository.findById(id)).thenReturn(Optional.of(app));

        assertThatThrownBy(() -> service.rejectApplication(id, "OTHER"))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void rejectApplication_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.rejectApplication(id, "OTHER"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── getApplicationDetails ─────────────────────────────────────────────────

    @Test
    void getApplicationDetails_found_returnsDetails() {
        UUID id = UUID.randomUUID();
        LoanApplication app = buildSavedApplication(ApplicationStatus.IN_REVIEW);
        when(repository.findById(id)).thenReturn(Optional.of(app));

        var result = service.getApplicationDetails(id);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Mart");
    }

    @Test
    void getApplicationDetails_notFound_throwsResourceNotFoundException() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getApplicationDetails(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Abimeetod ─────────────────────────────────────────────────────────────

    private LoanApplication buildSavedApplication(ApplicationStatus status) {
        LoanApplication app = new LoanApplication();
        app.setFirstName("Mart");
        app.setLastName("Tamm");
        app.setPersonalCode("38001011234");
        app.setLoanAmount(new BigDecimal("10000.00"));
        app.setLoanPeriodMonths(12);
        app.setBaseInterestRate(new BigDecimal("5.0"));
        app.setInterestMargin(new BigDecimal("2.0"));
        app.setStatus(status);
        return app;
    }
}