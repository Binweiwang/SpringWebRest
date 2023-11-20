package com.example.springwebrest.storage.services;

import com.example.springwebrest.rest.storage.exceptions.StorageBadRequest;
import com.example.springwebrest.rest.storage.exceptions.StorageInternal;
import com.example.springwebrest.rest.storage.exceptions.StorageNotFound;
import com.example.springwebrest.rest.storage.services.FileSystemStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class StorageServicesTest {

    private FileSystemStorageService fileSystemStorageService = new FileSystemStorageService("storage-dir");

    @BeforeEach
    public void setUp() {
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        fileSystemStorageService.store(new MockMultipartFile("funko", "funko.jpg", "image/jpeg", "funko".getBytes()));
        fileSystemStorageService.store(new MockMultipartFile("funko", "funko2.jpg", "image/jpeg", "funko".getBytes()));
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attrs = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attrs);
    }

    @Test
    public void store() {
        String file = fileSystemStorageService.store(new MockMultipartFile("funko", "funko.jpg", "image/jpeg", "funko".getBytes()));

        assertAll(
                () -> assertNotNull(file),
                () -> assertTrue(file.contains("funko.jpg"))
        );
    }

    @Test
    public void storeWithEmptyFile() {
        var res = assertThrows(StorageBadRequest.class, () -> fileSystemStorageService.store(new MockMultipartFile("funko", "funko.jpg", "image/jpeg", "".getBytes())));
        assertEquals("Fichero vacío funko.jpg", res.getMessage());
    }


    @Test
    public void storeWith2puntos(){
        var res = assertThrows(StorageBadRequest.class, () -> fileSystemStorageService.store(new MockMultipartFile("funko", "../funko.jpg", "image/jpeg", "funko".getBytes())));
        assertEquals("No se puede almacenar un fichero con una ruta relativa fuera del directorio actual ../funko.jpg", res.getMessage());
    }

    @Test
    void loadAll(){
        var res = fileSystemStorageService.loadAll();
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.toList().size() > 0)
        );
    }

    @Test
    void load(){
        var res = fileSystemStorageService.load("funko.jpg");
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.getFileName().toString().contains("funko.jpg"))
        );
    }

    @Test
    void loadAsResource(){
        var file = fileSystemStorageService.store(new MockMultipartFile("funko", "funko.jpg", "image/jpeg", "funko".getBytes()));
        var res = fileSystemStorageService.loadAsResource(file);
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.getFilename().contains("funko.jpg"))
        );
    }

    @Test
    void loadAsResoureNotFound(){
        var res = assertThrows(StorageNotFound.class, () -> fileSystemStorageService.loadAsResource("funko2.jpg"));
        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals("No se puede leer fichero: funko2.jpg", res.getMessage())
        );
    }

    @Test
    void loadAsResoureMalformedUrl(){
        var res = assertThrows(StorageNotFound.class, () -> fileSystemStorageService.loadAsResource("funko2.jpg"));

        assertAll(
                () -> assertNotNull(res),
                () -> assertEquals("No se puede leer fichero: funko2.jpg", res.getMessage())
        );
    }

    @Test
    void deleteAll(){
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        var res = fileSystemStorageService.loadAll();

        assertAll(
                () -> assertTrue(res.toList().isEmpty())
        );
    }

    @Test
    void deleteAllInternalError(){
        fileSystemStorageService.deleteAll();
        var res = assertThrows(StorageInternal.class, () -> fileSystemStorageService.loadAll());
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.getMessage().contains("Fallo al leer ficheros almacenados "))
        );
    }

    @Test
    void init(){
        fileSystemStorageService.deleteAll();
        fileSystemStorageService.init();
        var res = fileSystemStorageService.loadAll();
        assertAll(
                () -> assertNotNull(res),
                () -> assertTrue(res.toList().isEmpty())
        );
    }

    @Test
    void delete(){
        var file = fileSystemStorageService.store(new MockMultipartFile("funko", "funko.jpg", "image/jpeg", "funko".getBytes()));
        fileSystemStorageService.delete(file);
        var res = fileSystemStorageService.loadAll().toList();
        assertAll(
                () -> assertNotNull(res)
        );
    }

    @Test
    void getUrl() {
        String filename = "testFile.txt";
        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String expectedUrl = baseUrl + "/storage/" + filename;
        String actualUrl = fileSystemStorageService.getUrl(filename);
        assertEquals(expectedUrl, actualUrl);
    }


}