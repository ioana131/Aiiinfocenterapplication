package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmitRequestDto {
    @NotNull public Long studentId;
    @NotBlank public String type;
    @NotBlank public String message;
}
