package com.S3FileStorageApp.demo.services;

import com.S3FileStorageApp.demo.domain.entities.FileEntity;
import com.S3FileStorageApp.demo.repositories.FileRepository;
import com.S3FileStorageApp.demo.services.impl.FileServiceImpl;
import jakarta.validation.constraints.AssertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Load the full Spring Boot application context
@ActiveProfiles("test") // Use the 'test' profile for testing (application-test.properties)
@TestPropertySource(properties = {"BUCKET_NAME=test-bucket"})
@Transactional
public class FileServiceTests {

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    FileRepository fileRepository;

    @BeforeEach
    public void setUp() {
        fileRepository.deleteAll();  // Ensure the database is clean before each test
    }

    @Test public void saveFile(){
        FileEntity fileEntity = FileEntity.builder()
                .name("TestFile")
                .isPublic(false)
                .path("TestFile")
                .size(1)
                .date("")
                .build();
        fileService.save(fileEntity);
        Optional<FileEntity> fileEntity1 = fileService.findOne("TestFile");
        assertTrue(fileEntity1.isPresent());
        assertEquals(fileEntity1.get().getName(),fileEntity.getName());
    }

    @Test
    public void updatePublic(){
        FileEntity fileEntity = FileEntity.builder()
                .name("TestFile1")
                .isPublic(true)
                .path("TestFile1.txt")
                .size(1)
                .date("")
                .build();
        fileService.save(fileEntity);
        Optional<FileEntity> testFile = fileService.findOne("TestFile1.txt");
        assertTrue(testFile.isPresent());
        assertTrue(testFile.get().isPublic());
        fileService.updateIsPublic("TestFile1.txt",false);
        testFile = fileService.findOne("TestFile1.txt");
        assertTrue(testFile.isPresent());
        assertFalse(testFile.get().isPublic());
    }
}
