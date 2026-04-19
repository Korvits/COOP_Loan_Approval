// LoanProcessService.java
package com.example.loan.service;

import com.example.loan.entity.LoanApplication;
import com.example.loan.entity.PaymentScheduleEntry;
import com.example.loan.entity.enums.ApplicationStatus;
import com.example.loan.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class LoanProcessService {

    @Value("${loan.validation.max-age:70}")
    private int maxAgeLimit;

    /**
     * Main processing entry point for new applications.
     */
    public void process(LoanApplication app) {
        app.setStatus(ApplicationStatus.STARTED);

        int age = calculateAgeFromPersonalCode(app.getPersonalCode());

        if (age > maxAgeLimit) {
            app.setStatus(ApplicationStatus.REJECTED);
            app.setRejectionReason("CUSTOMER_TOO_OLD");
            return;
        }

        List<PaymentScheduleEntry> schedule = generateAnnuitySchedule(app);
        app.setPaymentSchedule(schedule);
        app.setStatus(ApplicationStatus.IN_REVIEW);
    }

    /**
     * Parses the Estonian personal code to calculate the applicant's age.
     * Format: GYYMMDDSSSC
     * G = century indicator (1-2: 1800s, 3-4: 1900s, 5-6: 2000s)
     */
    public int calculateAgeFromPersonalCode(String personalCode) {
        int centuryIndicator = Character.getNumericValue(personalCode.charAt(0));
        int year = Integer.parseInt(personalCode.substring(1, 3));
        int month = Integer.parseInt(personalCode.substring(3, 5));
        int day = Integer.parseInt(personalCode.substring(5, 7));

        int century;
        if (centuryIndicator <= 2) century = 1800;
        else if (centuryIndicator <= 4) century = 1900;
        else century = 2000;

        LocalDate birthDate = LocalDate.of(century + year, month, day);
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * Generates an annuity (equal monthly payment) schedule.
     * Formula: M = P * [r(1+r)^n] / [(1+r)^n - 1]
     * where r = monthly interest rate, n = number of months, P = principal
     */
    public List<PaymentScheduleEntry> generateAnnuitySchedule(LoanApplication app) {
        BigDecimal principal = app.getLoanAmount();
        int n = app.getLoanPeriodMonths();
        // Annual rate = baseInterestRate + interestMargin (stored as percentage)
        BigDecimal annualRatePercent = app.getBaseInterestRate().add(app.getInterestMargin());
        BigDecimal monthlyRate = annualRatePercent
                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
                .divide(BigDecimal.valueOf(12), 10, RoundingMode.HALF_UP);

        List<PaymentScheduleEntry> schedule = new ArrayList<>();

        BigDecimal monthlyPayment;
        // Handle 0% interest edge case
        if (monthlyRate.compareTo(BigDecimal.ZERO) == 0) {
            monthlyPayment = principal.divide(BigDecimal.valueOf(n), 2, RoundingMode.HALF_UP);
        } else {
            // (1 + r)^n
            BigDecimal onePlusR = BigDecimal.ONE.add(monthlyRate);
            BigDecimal pow = onePlusR.pow(n, new MathContext(20, RoundingMode.HALF_UP));
            // M = P * r * (1+r)^n / ((1+r)^n - 1)
            monthlyPayment = principal
                    .multiply(monthlyRate.multiply(pow))
                    .divide(pow.subtract(BigDecimal.ONE), 2, RoundingMode.HALF_UP);
        }

        BigDecimal balance = principal;
        LocalDate paymentDate = LocalDate.now();

        for (int i = 1; i <= n; i++) {
            PaymentScheduleEntry entry = new PaymentScheduleEntry();
            entry.setLoanApplication(app);
            entry.setPaymentNumber(i);
            entry.setPaymentDate(paymentDate);

            BigDecimal interestPayment = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalPayment;

            // Last payment: pay remaining balance to avoid rounding drift
            if (i == n) {
                principalPayment = balance;
                monthlyPayment = principalPayment.add(interestPayment);
            } else {
                principalPayment = monthlyPayment.subtract(interestPayment);
            }

            balance = balance.subtract(principalPayment).setScale(2, RoundingMode.HALF_UP);

            entry.setInterestPayment(interestPayment);
            entry.setPrincipalPayment(principalPayment);
            entry.setTotalPayment(principalPayment.add(interestPayment));
            entry.setRemainingBalance(balance);

            schedule.add(entry);
            paymentDate = paymentDate.plusMonths(1);
        }

        return schedule;
    }
}