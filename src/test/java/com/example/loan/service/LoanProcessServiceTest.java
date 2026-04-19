package com.example.loan.service;

import com.example.loan.entity.LoanApplication;
import com.example.loan.entity.PaymentScheduleEntry;
import com.example.loan.entity.enums.ApplicationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoanProcessServiceTest {

    private LoanProcessService processService;

    @BeforeEach
    void setUp() {
        processService = new LoanProcessService();
        ReflectionTestUtils.setField(processService, "maxAgeLimit", 70);
    }

    // ── Vanuse arvutamine ──────────────────────────────────────────────────────

    @Test
    void calculateAge_centuryIndicator3or4_returns1900sAge() {
        // 3YYMMDD → 1900s
        String personalCode = "38001011234"; // sündinud 1980-01-01
        int age = processService.calculateAgeFromPersonalCode(personalCode);
        assertThat(age).isGreaterThanOrEqualTo(45);
    }

    @Test
    void calculateAge_centuryIndicator5or6_returns2000sAge() {
        // 5YYMMDD → 2000s
        String personalCode = "50501011234"; // sündinud 2005-01-01
        int age = processService.calculateAgeFromPersonalCode(personalCode);
        assertThat(age).isGreaterThanOrEqualTo(20).isLessThan(30);
    }

    @Test
    void calculateAge_centuryIndicator1or2_returns1800sAge() {
        // 1YYMMDD → 1800s — peaks olema väga vana
        String personalCode = "19901011234"; // sündinud 1899-01-01
        int age = processService.calculateAgeFromPersonalCode(personalCode);
        assertThat(age).isGreaterThan(100);
    }

    // ── Vanusepiiri kontroll ───────────────────────────────────────────────────

    @Test
    void process_customerOlderThan70_isRejected() {
        LoanApplication app = buildApplication("38001011234"); // ~45 pole liiga vana
        // Kasutame isikukoodi, mis annab vanuse > 70 (sündinud 1950)
        app.setPersonalCode("35001011234"); // sündinud 1950-01-01 → vanus ~75
        processService.process(app);

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.REJECTED);
        assertThat(app.getRejectionReason()).isEqualTo("CUSTOMER_TOO_OLD");
    }

    @Test
    void process_customerExactly70_isAccepted() {
        // Arvutame isikukoodi, kus vanus on täpselt 70
        int currentYear = java.time.LocalDate.now().getYear();
        int birthYear = currentYear - 70;
        String yy = String.format("%02d", birthYear % 100);
        // 1900s → century indicator 3 või 4
        String personalCode = "3" + yy + "01011234";

        LoanApplication app = buildApplication(personalCode);
        processService.process(app);

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);
    }

    @Test
    void process_customerYoungerThan70_isInReview() {
        LoanApplication app = buildApplication("38001011234"); // ~45
        processService.process(app);

        assertThat(app.getStatus()).isEqualTo(ApplicationStatus.IN_REVIEW);
    }

    // ── Maksegraafiku genereerimine ───────────────────────────────────────────

    @Test
    void process_validApplication_generatesPaymentSchedule() {
        LoanApplication app = buildApplication("38001011234");
        processService.process(app);

        assertThat(app.getPaymentSchedule()).hasSize(app.getLoanPeriodMonths());
    }

    @Test
    void generateSchedule_paymentNumbersAreSequential() {
        LoanApplication app = buildApplication("38001011234");
        processService.process(app);

        List<PaymentScheduleEntry> schedule = app.getPaymentSchedule();
        for (int i = 0; i < schedule.size(); i++) {
            assertThat(schedule.get(i).getPaymentNumber()).isEqualTo(i + 1);
        }
    }

    @Test
    void generateSchedule_lastEntryRemainingBalanceIsZero() {
        LoanApplication app = buildApplication("38001011234");
        processService.process(app);

        List<PaymentScheduleEntry> schedule = app.getPaymentSchedule();
        BigDecimal lastBalance = schedule.get(schedule.size() - 1).getRemainingBalance();
        assertThat(lastBalance.compareTo(BigDecimal.ZERO)).isEqualTo(0);
    }

    @Test
    void generateSchedule_firstPaymentDateIsToday() {
        LoanApplication app = buildApplication("38001011234");
        processService.process(app);

        LocalDate firstDate = app.getPaymentSchedule().get(0).getPaymentDate();
        assertThat(firstDate).isEqualTo(LocalDate.now());
    }

    @Test
    void generateSchedule_zeroInterestRate_equalPrincipalPayments() {
        LoanApplication app = buildApplication("38001011234");
        app.setBaseInterestRate(BigDecimal.ZERO);
        app.setInterestMargin(BigDecimal.ZERO);
        processService.process(app);

        List<PaymentScheduleEntry> schedule = app.getPaymentSchedule();
        BigDecimal expectedPrincipal = app.getLoanAmount()
                .divide(BigDecimal.valueOf(app.getLoanPeriodMonths()), 2, java.math.RoundingMode.HALF_UP);

        // Kõik maksed peale viimase peaksid olema võrdsed
        for (int i = 0; i < schedule.size() - 1; i++) {
            assertThat(schedule.get(i).getPrincipalPayment()).isEqualByComparingTo(expectedPrincipal);
        }
    }

    // ── Abimeetod ─────────────────────────────────────────────────────────────

    private LoanApplication buildApplication(String personalCode) {
        LoanApplication app = new LoanApplication();
        app.setFirstName("Mart");
        app.setLastName("Tamm");
        app.setPersonalCode(personalCode);
        app.setLoanAmount(new BigDecimal("10000.00"));
        app.setLoanPeriodMonths(12);
        app.setBaseInterestRate(new BigDecimal("5.0"));
        app.setInterestMargin(new BigDecimal("2.0"));
        return app;
    }
}