package com.example.springwebrest.rest.categoria.controllers;

import com.example.springwebrest.rest.categoria.dto.CategoriaRequest;
import com.example.springwebrest.rest.categoria.mapper.CategoriasMapper;
import com.example.springwebrest.rest.categoria.models.Categoria;
import com.example.springwebrest.rest.categoria.services.CategoriaServices;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.example.springwebrest.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

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
    public ResponseEntity<PageResponse<Categoria>> getAllCategorias(
            @RequestParam(required = false) Optional<String> tipo,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
            ){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<Categoria> pageResult = categoriaServices.findAll(tipo, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", PaginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, direction));
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
