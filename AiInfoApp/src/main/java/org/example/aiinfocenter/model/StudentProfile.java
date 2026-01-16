package org.example.aiinfocenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "student_profiles")
public class StudentProfile {

    @Id
    private Long userId;

    @OneToOne
    @MapsId
    private User user;

    @NotBlank
    private String faculty;

    @Min(1) @Max(10)
    private int yearOfStudy;

    public StudentProfile() {}

    public StudentProfile(User user, String faculty, int yearOfStudy) {
        this.user = user;
        this.faculty = faculty;
        this.yearOfStudy = yearOfStudy;
    }

    public Long getUserId() { return userId; }
    public User getUser() { return user; }
    public String getFaculty() { return faculty; }
    public int getYearOfStudy() { return yearOfStudy; }

    public void setFaculty(String faculty) { this.faculty = faculty; }
    public void setYearOfStudy(int yearOfStudy) { this.yearOfStudy = yearOfStudy; }
}
