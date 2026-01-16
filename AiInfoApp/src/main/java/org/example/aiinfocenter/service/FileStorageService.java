package org.example.aiinfocenter.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path baseDir = Paths.get("uploads", "requests");

    public StoredFile save(MultipartFile file) throws Exception {
        Files.createDirectories(baseDir);

        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String safeName = Paths.get(original).getFileName().toString();

        String id = UUID.randomUUID().toString();
        Path path = baseDir.resolve(id + "_" + safeName);

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        return new StoredFile(id, safeName, file.getContentType(), path.toString());
    }

    public record StoredFile(String id, String name, String contentType, String path) {}
}
