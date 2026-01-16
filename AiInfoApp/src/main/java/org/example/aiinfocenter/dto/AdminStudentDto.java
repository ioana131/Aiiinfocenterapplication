package org.example.aiinfocenter.dto;

public class AdminStudentDto {
    public Long id;
    public String name;
    public String email;

    public AdminStudentDto(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
