package org.example.aiinfocenter.repo;

import org.example.aiinfocenter.model.ConversationThread;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationThreadRepository extends JpaRepository<ConversationThread, Long> {
    List<ConversationThread> findByStudentIdOrderByCreatedAtDesc(Long studentId);
}
