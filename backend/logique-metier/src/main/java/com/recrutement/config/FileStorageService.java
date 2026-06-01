package com.recrutement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) throws IOException {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.root);
    }

    public String store(MultipartFile file, String subfolder) throws IOException {
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }

        if (!ext.equalsIgnoreCase(".pdf")) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont autorisés.");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("Le type de fichier doit être application/pdf.");
        }
        
        long maxSize = 1 * 1024 * 1024; // 1 MB
        if (file.getSize() > maxSize) {
            throw new IllegalArgumentException("La taille du fichier ne doit pas dépasser 1 MB.");
        }

        String filename = UUID.randomUUID() + ext;
        Path targetDir = root.resolve(subfolder);
        Files.createDirectories(targetDir);
        Path target = targetDir.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + subfolder + "/" + filename;
    }
}
