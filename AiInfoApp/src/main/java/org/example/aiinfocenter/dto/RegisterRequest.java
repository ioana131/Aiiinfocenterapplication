package org.example.aiinfocenter.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {
    @NotBlank public String name;
    @Email @NotBlank public String email;
    @NotBlank public String password;

    // accept doar STUDENT sau ADMIN (exact)
    @NotBlank
    @Pattern(regexp = "STUDENT|ADMIN", message = "role must be STUDENT or ADMIN")
    public String role;

    // doar pentru student (raman optional, dar sunt verificate in service cand role=STUDENT)
    public String faculty;

    @Min(1) @Max(10)
    public Integer yearOfStudy;
}
