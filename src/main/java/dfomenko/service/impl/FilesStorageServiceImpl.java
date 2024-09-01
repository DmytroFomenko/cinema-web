package dfomenko.service.impl;

import dfomenko.service.FilesStorageService;
import dfomenko.utils.CinemaFilesUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;


@RequiredArgsConstructor
@Service
public class FilesStorageServiceImpl implements FilesStorageService {

    private final CinemaFilesUtils cinemaFilesUtils;

    private Path storagePath;


    @Override
    @PostConstruct
    public void init() {
        try {
            storagePath = cinemaFilesUtils.getCinemaFilesAbsPath();
            //System.out.println("Cinema storage Path: " + storagePath);
            Files.createDirectories(storagePath);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    @Override
    public void save(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        try {
            assert originalFileName != null;
            Files.copy(file.getInputStream(),
                       this.storagePath.resolve(originalFileName),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    @Override
    public void save(MultipartFile file, String fileName) {
        try {
            Files.copy(file.getInputStream(),
                       this.storagePath.resolve(fileName),
                       StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }


    @Override
    public Resource load(String filename) {
        try {
            Path file = storagePath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file \"" + filename + "\" !");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(String filename) {
        try {
            Path file = storagePath.resolve(filename);
            return Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(storagePath.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files
                    .walk(this.storagePath, 1)
                    .filter(path -> !path.equals(this.storagePath))
                    .map(this.storagePath::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

}
