package org.example.aiinfocenter.service;

import org.example.aiinfocenter.dto.AdminStudentDto;
import org.example.aiinfocenter.model.User;
import org.example.aiinfocenter.model.UserRole;
import org.example.aiinfocenter.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository users;

    public AdminService(UserRepository users) {
        this.users = users;
    }

    public List<AdminStudentDto> listStudents() {
        return users.findAll().stream()
                .filter(u -> u.getRole() == UserRole.STUDENT)
                .map(u -> new AdminStudentDto(u.getId(), u.getName(), u.getEmail()))
                .toList();
    }

    @Transactional
    public AdminStudentDto renameStudent(Long studentId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("name required");
        }

        User u = users.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("student not found"));

        if (u.getRole() != UserRole.STUDENT) {
            throw new IllegalArgumentException("only STUDENT can be edited");
        }

        u.setName(newName.trim());
        users.save(u);

        return new AdminStudentDto(u.getId(), u.getName(), u.getEmail());
    }
}
