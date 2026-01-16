package org.example.aiinfocenter.repo;

import org.example.aiinfocenter.model.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByStudentIdOrderByIdDesc(Long studentId);
    List<Request> findAllByOrderByIdDesc();
}
