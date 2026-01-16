package org.example.aiinfocenter.repo;

import org.example.aiinfocenter.model.MessageLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageLogRepository extends JpaRepository<MessageLog, Long> {
}
