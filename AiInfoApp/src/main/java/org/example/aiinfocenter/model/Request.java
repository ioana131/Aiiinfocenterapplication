package org.example.aiinfocenter.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.Instant;


@Entity
@Table(name = "requests")
public class Request {

    public enum Status { OPEN, SEEN, ANSWERED, REJECTED }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User student;

    @NotBlank
    private String type;

    @NotBlank
    @Column(columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.OPEN;

    @Column(columnDefinition = "text")
    private String adminResponse;

    // ===================== ATTACHMENT (ADMIN RESPONSE FILE) =====================
    private String attachmentId;
    private String attachmentName;
    private String attachmentContentType;
    private String attachmentPath;
// ===========================================================================

    @Column(nullable = false)
    private Instant createdAt = Instant.now();



    public Request() {}

    public Request(User student, String type, String message) {
        this.student = student;
        this.type = type;
        this.message = message;
    }

    public Long getId() { return id; }
    public User getStudent() { return student; }
    public String getType() { return type; }
    public String getMessage() { return message; }
    public Status getStatus() { return status; }
    public String getAdminResponse() { return adminResponse; }
    public Instant getCreatedAt() { return createdAt; }

    public void setStudent(User student) { this.student = student; }
    public void setType(String type) { this.type = type; }
    public void setMessage(String message) { this.message = message; }
    public void setStatus(Status status) { this.status = status; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }
    public String getAttachmentId() { return attachmentId; }
    public String getAttachmentName() { return attachmentName; }
    public String getAttachmentContentType() { return attachmentContentType; }
    public String getAttachmentPath() { return attachmentPath; }

    public void setAttachmentId(String attachmentId) { this.attachmentId = attachmentId; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }
    public void setAttachmentContentType(String attachmentContentType) { this.attachmentContentType = attachmentContentType; }
    public void setAttachmentPath(String attachmentPath) { this.attachmentPath = attachmentPath; }

}
