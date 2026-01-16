package org.example.aiinfocenter.service;

import org.example.aiinfocenter.dto.LoginRequest;
import org.example.aiinfocenter.dto.RegisterRequest;
import org.example.aiinfocenter.model.StudentProfile;
import org.example.aiinfocenter.model.User;
import org.example.aiinfocenter.model.UserRole;
import org.example.aiinfocenter.repo.StudentProfileRepository;
import org.example.aiinfocenter.repo.UserRepository;
import org.example.aiinfocenter.util.ValidationUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final StudentProfileRepository profiles;
    private final ValidationUtil validation;

    public AuthService(UserRepository users, StudentProfileRepository profiles, ValidationUtil validation) {
        this.users = users;
        this.profiles = profiles;
        this.validation = validation;
    }

    @Transactional
    public User register(RegisterRequest dto) {
        if (dto.role == null || dto.role.trim().isEmpty()) {
            throw new IllegalArgumentException("role is required");
        }

        UserRole role;
        try {
            role = UserRole.valueOf(dto.role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("role must be STUDENT / ADMIN");
        }

        if (users.findByEmail(dto.email).isPresent()) {
            throw new IllegalArgumentException("email already exists");
        }

        User user = users.save(new User(dto.name, dto.email, dto.password, role));

        if (role == UserRole.STUDENT) {
            validation.validateStudentFields(dto.faculty, dto.yearOfStudy);
            profiles.save(new StudentProfile(user, dto.faculty, dto.yearOfStudy));
        }

        return user;
    }

    public User login(LoginRequest dto) {
        User u = users.findByEmail(dto.email)
                .orElseThrow(() -> new IllegalArgumentException("The account doesn't exist"));

        if (!u.getPassword().equals(dto.password)) {
            throw new IllegalArgumentException("Incorrect password");
        }

        return u;
    }
}
