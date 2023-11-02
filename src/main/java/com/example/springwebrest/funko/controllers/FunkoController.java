package com.example.springwebrest.funko.controllers;

import com.example.springwebrest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.funko.mapper.FunkoMapper;
import com.example.springwebrest.funko.services.FunkoServices;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto borrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        funkoServices.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
