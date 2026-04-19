// RejectionRequestDTO.java
package com.example.loan.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectionRequestDTO {

    @NotBlank
    private String reason;

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}