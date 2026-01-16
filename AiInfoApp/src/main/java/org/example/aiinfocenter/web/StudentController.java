package org.example.aiinfocenter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.aiinfocenter.model.ChatMessage;
import org.example.aiinfocenter.model.ConversationThread;
import org.example.aiinfocenter.model.Request;
import org.example.aiinfocenter.service.ConversationService;
import org.example.aiinfocenter.service.RequestService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final ConversationService conv;
    private final RequestService req;

    public StudentController(ConversationService conv, RequestService req) {
        this.conv = conv;
        this.req = req;
    }

    // ================= CONVERSATIONS =================

    public static class CreateConversationDto {
        @NotNull @Min(1) public Long studentId;
        @NotBlank public String title;
    }

    @PostMapping("/conversations")
    public ConversationThread createConversation(@RequestBody @Valid CreateConversationDto dto) {
        return conv.createConversation(dto.studentId, dto.title);
    }

    @GetMapping("/conversations")
    public List<ConversationThread> listConversations(
            @RequestParam @NotNull @Min(1) Long studentId
    ) {
        return conv.listConversations(studentId);
    }

    @DeleteMapping("/conversations/{id}")
    public void deleteConversation(
            @PathVariable @Min(1) Long id,
            @RequestParam @NotNull @Min(1) Long studentId
    ) {
        conv.deleteConversation(studentId, id);
    }

    public static class SendMessageDto {
        @NotNull @Min(1) public Long studentId;
        @NotBlank public String message;
        public Long conversationId; // optional
    }

    @GetMapping("/conversations/{id}/messages")
    public List<ChatMessage> listMessages(
            @PathVariable @Min(1) Long id,
            @RequestParam @NotNull @Min(1) Long studentId
    ) {
        return conv.listMessages(studentId, id);
    }

    @PostMapping("/conversations/{id}/messages")
    public ChatMessage sendMessage(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid SendMessageDto dto
    ) {
        return conv.sendStudentMessage(dto.studentId, id, dto.message);
    }

    // ================= REQUESTS =================

    public static class CreateRequestDto {
        @NotNull @Min(1) public Long studentId;
        @NotBlank public String message;
    }

    @PostMapping("/requests")
    public Request submitRequest(@RequestBody @Valid CreateRequestDto dto) {
        return req.create(dto.studentId, dto.message);
    }

    @GetMapping("/requests")
    public List<Request> myRequests(
            @RequestParam @NotNull @Min(1) Long studentId
    ) {
        return req.listForStudent(studentId);
    }

    // ================= REQUEST ATTACHMENT DOWNLOAD =================

    @GetMapping("/requests/{id}/attachment")
    public ResponseEntity<Resource> downloadAttachment(
            @PathVariable @Min(1) Long id,
            @RequestParam @NotNull @Min(1) Long studentId
    ) throws Exception {

        // get request using service
        Request r = req.getById(id);

        // ownership check
        if (!r.getStudent().getId().equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // no attachment
        if (r.getAttachmentPath() == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(r.getAttachmentPath());
        Resource res = new UrlResource(path.toUri());
        if (!res.exists()) {
            return ResponseEntity.notFound().build();
        }

        String contentType =
                (r.getAttachmentContentType() == null || r.getAttachmentContentType().isBlank())
                        ? "application/octet-stream"
                        : r.getAttachmentContentType();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + r.getAttachmentName() + "\""
                )
                .body(res);
    }
}
