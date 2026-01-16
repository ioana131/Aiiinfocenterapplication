package org.example.aiinfocenter.dto;

public class StudentViewDto {
    public Long id;
    public String name;
    public String email;
    public String faculty;
    public Integer yearOfStudy;

    public StudentViewDto(Long id, String name, String email, String faculty, Integer yearOfStudy) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.faculty = faculty;
        this.yearOfStudy = yearOfStudy;
    }
}
