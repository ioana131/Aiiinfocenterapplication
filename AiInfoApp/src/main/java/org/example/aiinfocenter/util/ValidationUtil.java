package org.example.aiinfocenter.util;

import org.springframework.stereotype.Component;

@Component
public class ValidationUtil {
    public void validateStudentFields(String faculty, Integer year) {
        if (faculty == null || faculty.isBlank())
            throw new IllegalArgumentException("faculty is required for student");
        if (year == null || year < 1 || year > 10)
            throw new IllegalArgumentException("yearOfStudy must be 1..10");
    }
}
