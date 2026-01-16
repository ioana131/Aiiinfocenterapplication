package org.example.aiinfocenter.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "message_log")
public class MessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 4000)
    private String message;

    @Column(columnDefinition = "text")
    private String responseJson;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected MessageLog() {}

    public MessageLog(String message) { this.message = message; }

    public Long getId() { return id; }
    public String getMessage() { return message; }
    public String getResponseJson() { return responseJson; }
    public Instant getCreatedAt() { return createdAt; }

    public void setResponseJson(String responseJson) { this.responseJson = responseJson; }
}
