package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SendMessageDto {
    @NotNull public Long studentId;
    @NotNull public Long conversationId;
    @NotBlank public String message;
}
