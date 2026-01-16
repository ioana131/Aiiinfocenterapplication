package org.example.aiinfocenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;

@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    public enum Sender { STUDENT, AI }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ConversationThread thread;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sender sender;

    @NotBlank
    @Column(columnDefinition = "text")
    private String text;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public ChatMessage() {}

    public ChatMessage(ConversationThread thread, Sender sender, String text) {
        this.thread = thread;
        this.sender = sender;
        this.text = text;
    }

    public Long getId() { return id; }
    public ConversationThread getThread() { return thread; }
    public Sender getSender() { return sender; }
    public String getText() { return text; }
    public Instant getCreatedAt() { return createdAt; }
}
