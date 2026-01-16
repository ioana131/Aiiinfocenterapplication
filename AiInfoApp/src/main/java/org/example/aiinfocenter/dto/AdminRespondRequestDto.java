package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.NotBlank;

public class AdminRespondRequestDto {
    @NotBlank public String status; // "SEEN" / "ANSWERED"
    @NotBlank public String adminResponse;
}
