package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateConversationDto {
    @NotNull public Long studentId;
    @NotBlank public String title;
}
