package org.example.aiinfocenter;

import org.example.aiinfocenter.dto.LoginRequest;
import org.example.aiinfocenter.dto.RegisterRequest;
import org.example.aiinfocenter.model.Request;
import org.example.aiinfocenter.model.User;
import org.example.aiinfocenter.model.UserRole;
import org.example.aiinfocenter.repo.RequestRepository;
import org.example.aiinfocenter.repo.StudentProfileRepository;
import org.example.aiinfocenter.repo.UserRepository;
import org.example.aiinfocenter.service.AuthService;
import org.example.aiinfocenter.service.RequestService;
import org.example.aiinfocenter.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

public class ServiceFunctionalityTests {

    @Test
    void register_duplicate_email_fails() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        StudentProfileRepository profileRepo = Mockito.mock(StudentProfileRepository.class);

        Mockito.when(userRepo.findByEmail("a@mail.com"))
                .thenReturn(Optional.of(new User("x", "a@mail.com", "p", UserRole.STUDENT)));

        AuthService auth = new AuthService(userRepo, profileRepo, new ValidationUtil());

        RegisterRequest dto = new RegisterRequest();
        dto.name = "Ana";
        dto.email = "a@mail.com";
        dto.password = "1234";
        dto.role = "STUDENT";
        dto.faculty = "CS";
        dto.yearOfStudy = 2;

        assertThrows(IllegalArgumentException.class, () -> auth.register(dto));
    }

    @Test
    void login_wrong_password_fails() {
        UserRepository userRepo = Mockito.mock(UserRepository.class);
        StudentProfileRepository profileRepo = Mockito.mock(StudentProfileRepository.class);

        User u = new User("Ana", "ana@mail.com", "GOOD", UserRole.STUDENT);
        Mockito.when(userRepo.findByEmail("ana@mail.com")).thenReturn(Optional.of(u));

        AuthService auth = new AuthService(userRepo, profileRepo, new ValidationUtil());

        LoginRequest dto = new LoginRequest();
        dto.email = "ana@mail.com";
        dto.password = "BAD";

        assertThrows(IllegalArgumentException.class, () -> auth.login(dto));
    }

    @Test
    void create_request_saves_for_student() {
        RequestRepository reqRepo = Mockito.mock(RequestRepository.class);
        UserRepository userRepo = Mockito.mock(UserRepository.class);

        User student = new User("Ana", "ana@mail.com", "1234", UserRole.STUDENT);

        Mockito.when(userRepo.findById(1L)).thenReturn(Optional.of(student));
        Mockito.when(reqRepo.save(any(Request.class))).thenAnswer(inv -> inv.getArgument(0));

        RequestService service = new RequestService(reqRepo, userRepo);

        Request saved = service.create(1L, "need paper");

        assertEquals(student, saved.getStudent());
        assertEquals("need paper", saved.getMessage());
        assertEquals("GENERAL", saved.getType());
        assertEquals(Request.Status.OPEN, saved.getStatus());
        assertNotNull(saved.getCreatedAt());
    }
}
