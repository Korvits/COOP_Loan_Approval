// LoanApplicationController.java
package com.example.loan.controller;

import com.example.loan.dto.*;
import com.example.loan.service.LoanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/loan-applications")
@Tag(name = "Loan Application", description = "Loan application management API")
public class LoanApplicationController {

    private final LoanApplicationService loanService;

    public LoanApplicationController(LoanApplicationService loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    @Operation(summary = "Submit a new loan application")
    public ResponseEntity<LoanApplicationResponseDTO> submitApplication(
            @Valid @RequestBody LoanApplicationRequestDTO request) {
        return new ResponseEntity<>(loanService.createApplication(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan application details with payment schedule")
    public ResponseEntity<LoanApplicationDetailsDTO> getApplicationDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.getApplicationDetails(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve a loan application (must be IN_REVIEW)")
    public ResponseEntity<Void> approveApplication(@PathVariable UUID id) {
        loanService.approveApplication(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reject")
    @Operation(summary = "Reject a loan application with a reason")
    public ResponseEntity<Void> rejectApplication(
            @PathVariable UUID id,
            @Valid @RequestBody RejectionRequestDTO rejection) {
        loanService.rejectApplication(id, rejection.getReason());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/regenerate-schedule")
    @Operation(summary = "Regenerate payment schedule with new parameters (must be IN_REVIEW)")
    public ResponseEntity<LoanApplicationDetailsDTO> regenerateSchedule(
            @PathVariable UUID id,
            @Valid @RequestBody RegenerateScheduleRequestDTO request) {
        return ResponseEntity.ok(loanService.regenerateSchedule(id, request));
    }
}
