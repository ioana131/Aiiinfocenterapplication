package org.example.aiinfocenter.repo;

import org.example.aiinfocenter.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByThreadIdOrderByCreatedAtAsc(Long threadId);

    @Transactional
    void deleteByThreadId(Long threadId);
}
