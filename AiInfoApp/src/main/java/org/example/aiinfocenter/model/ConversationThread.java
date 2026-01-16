package org.example.aiinfocenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "conversation_threads")
public class ConversationThread {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @NotBlank
    private String title;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public ConversationThread() {}

    public ConversationThread(User student, String title) {
        this.student = student;
        this.title = title;
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public String getTitle() { return title; }
    public Instant getCreatedAt() { return createdAt; }

    public void setTitle(String title) { this.title = title; }
}
