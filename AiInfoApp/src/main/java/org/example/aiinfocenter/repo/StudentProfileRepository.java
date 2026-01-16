package org.example.aiinfocenter.repo;

import org.example.aiinfocenter.model.StudentProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentProfileRepository extends JpaRepository<StudentProfile, Long> {}
