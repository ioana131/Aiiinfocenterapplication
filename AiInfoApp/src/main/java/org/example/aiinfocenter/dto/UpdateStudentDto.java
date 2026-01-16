package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.*;

public class UpdateStudentDto {
    @NotBlank public String name;
    @NotBlank public String faculty;
    @NotNull @Min(1) @Max(10) public Integer yearOfStudy;
}
