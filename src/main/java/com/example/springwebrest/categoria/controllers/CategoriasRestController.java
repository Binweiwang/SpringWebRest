package com.example.springwebrest.categoria.controllers;

import com.example.springwebrest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.categoria.models.Categoria;
import com.example.springwebrest.categoria.services.CategoriaServices;
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
import java.util.UUID;

@RestController
@RequestMapping("/categorias")
public class CategoriasRestController {
    private final CategoriaServices categoriaServices;
    private final CategoriasMapper categoriasMapper;

    @Autowired
    public CategoriasRestController(CategoriaServices categoriaServices, CategoriasMapper categoriasMapper) {
        this.categoriaServices = categoriaServices;
        this.categoriasMapper = categoriasMapper;
    }

    @GetMapping()
    public ResponseEntity<List<Categoria>> getAllCategorias(){
        return ResponseEntity.ok(categoriaServices.findAll());
    }

    @GetMapping("/{id}")
    public  ResponseEntity<Categoria> getCategoriaById(@PathVariable UUID id){
        return ResponseEntity.ok(categoriaServices.findById(id));
    }
    @PostMapping()
    public ResponseEntity<Categoria> createCategoria(@Valid @RequestBody CategoriaRequest categoria){
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaServices.save(categoria));
    }
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> updateCategoria(@PathVariable UUID id, @Valid @RequestBody CategoriaRequest categoria){
        return ResponseEntity.ok(categoriaServices.update(id,categoria));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable UUID id){
        categoriaServices.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
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
