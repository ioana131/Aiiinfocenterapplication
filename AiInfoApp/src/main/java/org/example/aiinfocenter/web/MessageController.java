package org.example.aiinfocenter.web;

import jakarta.validation.Valid;
import org.example.aiinfocenter.dto.MessageRequest;
import org.example.aiinfocenter.dto.N8nOutputItem;
import org.example.aiinfocenter.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MessageController {

    private final MessageService service;

    public MessageController(MessageService service) {
        this.service = service;
    }

    // endpoint DOAR pentru test (PowerShell / Postman)
    @PostMapping("/message")
    public List<N8nOutputItem> message(@RequestBody @Valid MessageRequest req) {
        N8nOutputItem item = new N8nOutputItem();
        item.output = service.askAiText(req.message); // ✅ exista la tine
        return List.of(item);
    }
}
