package org.example.aiinfocenter.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.aiinfocenter.dto.AdminStudentDto;
import org.example.aiinfocenter.model.ChatMessage;
import org.example.aiinfocenter.model.ConversationThread;
import org.example.aiinfocenter.model.Request;
import org.example.aiinfocenter.service.AdminService;
import org.example.aiinfocenter.service.ConversationService;
import org.example.aiinfocenter.service.FileStorageService;
import org.example.aiinfocenter.service.RequestService;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final RequestService requests;
    private final AdminService admin;
    private final ConversationService conv;
    private final FileStorageService files;

    public AdminController(
            RequestService requests,
            AdminService admin,
            ConversationService conv,
            FileStorageService files
    ) {
        this.requests = requests;
        this.admin = admin;
        this.conv = conv;
        this.files = files;
    }

    // ================= STUDENTS =================

    @GetMapping("/students")
    public List<AdminStudentDto> listStudents() {
        return admin.listStudents();
    }

    public static class RenameDto {
        @NotBlank public String name;
    }

    @PutMapping("/students/{id}")
    public AdminStudentDto renameStudent(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid RenameDto dto
    ) {
        return admin.renameStudent(id, dto.name);
    }

    // ================= REQUESTS =================

    @GetMapping("/requests")
    public List<Request> allRequests() {
        return requests.allRequestsAdmin();
    }

    @GetMapping("/students/{id}/requests")
    public List<Request> requestsForStudent(@PathVariable @Min(1) Long id) {
        return requests.requestsForStudentAdmin(id);
    }

    public static class RespondDto {
        @NotBlank public String adminResponse;
        @NotNull public Request.Status status;
    }

    @PostMapping("/requests/{id}/respond")
    public Request respond(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid RespondDto dto
    ) {
        return requests.respond(id, dto.adminResponse, dto.status);
    }

    // ================= CONVERSATIONS (ADMIN READ-ONLY) =================

    @GetMapping("/students/{id}/conversations")
    public List<ConversationThread> conversationsForStudent(@PathVariable @Min(1) Long id) {
        return conv.listConversations(id);
    }

    @GetMapping("/students/{studentId}/conversations/{threadId}/messages")
    public List<ChatMessage> messagesForStudentConversation(
            @PathVariable @Min(1) Long studentId,
            @PathVariable @Min(1) Long threadId
    ) {
        return conv.listMessages(studentId, threadId);
    }

    // ================= REQUEST ATTACHMENT =================

    @PostMapping(value = "/requests/{id}/attachment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Request uploadAttachment(
            @PathVariable @Min(1) Long id,
            @RequestParam("file") MultipartFile file
    ) throws Exception {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("select a file first");
        }

        Request r = requests.getById(id);

        var stored = files.save(file);

        r.setAttachmentId(stored.id());
        r.setAttachmentName(stored.name());
        r.setAttachmentContentType(stored.contentType());
        r.setAttachmentPath(stored.path());

        return requests.save(r);
    }
}
