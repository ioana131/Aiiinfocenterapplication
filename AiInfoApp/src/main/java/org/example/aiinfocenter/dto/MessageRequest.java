package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {
    @NotBlank
    public String message;
}
