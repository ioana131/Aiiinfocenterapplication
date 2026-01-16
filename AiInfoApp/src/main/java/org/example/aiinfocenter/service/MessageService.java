package org.example.aiinfocenter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.aiinfocenter.dto.MessageRequest;
import org.example.aiinfocenter.dto.N8nOutputItem;
import org.example.aiinfocenter.model.MessageLog;
import org.example.aiinfocenter.repo.MessageLogRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MessageService {

    private static final String WEBHOOK_URL =
            "https://n8n-service-scmr.onrender.com/webhook/message";

    private final MessageLogRepository repo;
    private final ObjectMapper mapper;
    private final RestTemplate rest = new RestTemplate();

    public MessageService(MessageLogRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    // ✅ folosita de ConversationService si si de /message (test)
    public String askAiText(String message) {
        MessageRequest req = new MessageRequest();
        req.message = message;

        // 1) save message
        MessageLog log = repo.save(new MessageLog(req.message));

        // 2) call n8n (as String, ca sa nu mai pice conversia)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<MessageRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<String> resp;
        try {
            resp = rest.postForEntity(WEBHOOK_URL, entity, String.class);
        } catch (RestClientException e) {
            throw new IllegalArgumentException("n8n call failed: " + e.getMessage());
        }

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalArgumentException("n8n returned invalid response: " + resp.getStatusCode());
        }

        String rawJson = resp.getBody();

        // 4) save raw response to db
        log.setResponseJson(rawJson);
        repo.save(log);

        // 5) extract output text (accepta si array, si obiect)
        try {
            String s = rawJson.trim();

            // array: [ { "output": "..." } ]
            if (s.startsWith("[")) {
                N8nOutputItem[] arr = mapper.readValue(s, N8nOutputItem[].class);
                if (arr.length > 0 && arr[0] != null && arr[0].output != null) return arr[0].output;
                return "(empty)";
            }

            // obiect: { "output": "..." }
            N8nOutputItem obj = mapper.readValue(s, N8nOutputItem.class);
            return (obj.output != null) ? obj.output : "(empty)";

        } catch (Exception e) {
            return "(could not parse n8n response)";
        }
    }
}
