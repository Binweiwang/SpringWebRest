package com.example.springwebrest.storage.services;

import com.example.springwebrest.rest.storage.exceptions.StorageBadRequest;
import com.example.springwebrest.rest.storage.services.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StorageServicesTest {

    @Autowired
    private StorageService storageService;


    private MultipartFile createTestFile() throws IOException {
        return new MockMultipartFile("testfile.txt", "Hello, World!".getBytes());
    }

    @BeforeEach
    public void init() {
        storageService.init();
    }

    @Test
    public void testStoreAndLoad() throws IOException {
        MultipartFile file = createTestFile();
        String storedFilename = storageService.store(file);

        assertNotNull(storedFilename);

        Path loadedFile = storageService.load(storedFilename);
        assertTrue(Files.exists(loadedFile));
        String fileContent = new String(Files.readAllBytes(loadedFile));
        assertEquals("Hello, World!", fileContent);
    }

    @Test
    public void testStoreEmptyFile() throws IOException {
        MultipartFile emptyFile = new MockMultipartFile("emptyfile.txt", new byte[0]);
        assertThrows(StorageBadRequest.class, () -> storageService.store(emptyFile));
    }


}
