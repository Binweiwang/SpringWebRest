package com.example.springwebrest.rest.funko.controllers;

import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.mapper.FunkoMapper;
import com.example.springwebrest.rest.funko.services.FunkoServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/funkos")
public class FunkoController {
    private final FunkoServices funkoServices;
    private final FunkoMapper funkoMapper;

    @Autowired
    public FunkoController(FunkoServices funkoServices, FunkoMapper funkoMapper) {
        this.funkoServices = funkoServices;
        this.funkoMapper = funkoMapper;
    }

    @GetMapping()
    public ResponseEntity<List<FunkoResponseDto>> getFunkos() {
        return ResponseEntity.ok(funkoMapper.toResponses(funkoServices.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> getFunko(@PathVariable Long id) {
        return ResponseEntity.ok(funkoServices.findById(id));
    }

    @PostMapping()
    public ResponseEntity<FunkoResponseDto> postFunko(@Valid @RequestBody FunkoCreateRequest funko) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoServices.save(funko));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> putFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateRequest funko) {
      return ResponseEntity.ok(funkoServices.update(id, funko));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> patchFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateRequest funko) {
        return ResponseEntity.ok(funkoServices.update(id, funko));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        funkoServices.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "Actualiza la imagen de un funko", description = "Actualiza la imagen de un funko")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del funko", example = "1", required = true),
            @Parameter(name = "file", description = "Fichero a subir", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Producto no válido"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
    })
    @PatchMapping(value = "/imagen/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FunkoResponseDto> nuevoFunko(
            @PathVariable Long id,
            @RequestPart("file") MultipartFile file) {

        log.info("Actualizando imagen de producto por id: " + id);



        // Buscamos la raqueta
        if (!file.isEmpty() && file.getContentType().equals("image/jpeg")  || file.getContentType().equals("image/png") || file.getContentType().equals("image/jpg")) {
            // Actualizamos el producto
            return ResponseEntity.ok(funkoServices.updateImage(id, file, true));

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se ha enviado una imagen para el producto o esta está vacía");
        }
    }




    // Para capturar los errores de validación
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
