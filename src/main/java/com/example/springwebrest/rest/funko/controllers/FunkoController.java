package com.example.springwebrest.rest.funko.controllers;

import com.example.springwebrest.rest.funko.dto.FunkoCreateRequest;
import com.example.springwebrest.rest.funko.dto.FunkoResponseDto;
import com.example.springwebrest.rest.funko.dto.FunkoUpdateRequest;
import com.example.springwebrest.rest.funko.mapper.FunkoMapper;
import com.example.springwebrest.rest.funko.services.FunkoServices;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.example.springwebrest.utils.pagination.PaginationLinksUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

/**
 * Controlador de Funkos
 */
@RestController
@Slf4j
@RequestMapping("${api.version}/funkos")
@Tag(name = "Funkos", description = "Listado de funkos")
public class FunkoController {
    private final FunkoServices funkoServices;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public FunkoController(FunkoServices funkoServices, FunkoMapper funkoMapper, PaginationLinksUtils paginationLinksUtils) {
        this.funkoServices = funkoServices;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    /**
     * Obtiene todos los funkos
     * @param name nombre del funko
     * @param price precio del funko
     * @param quantity cantidad del funko
     * @param categoria categoria del funko
     * @param page pagina a mostrar
     * @param size tamaño de la pagina
     * @param sortBy campo por el que ordenar
     * @param direction direccion de la ordenacion
     * @param request peticion http
     * @return lista de funkos
     */
    @Operation(summary = "Obtiene todos los funkos", description = "Obtiene todos los funkos")
    @Parameters({
            @Parameter(name = "name", description = "Nombre del funko", example = "funko"),
            @Parameter(name = "price", description = "Precio del funko", example = "10.00"),
            @Parameter(name = "quantity", description = "Cantidad del funko", example = "1"),
            @Parameter(name = "categoria", description = "Categoria del funko", example = "DISNEY"),
            @Parameter(name = "page", description = "Página a mostrar", example = "0"),
            @Parameter(name = "size", description = "Tamaño de la página", example = "10"),
            @Parameter(name = "sortBy", description = "Campo por el que ordenar", example = "id"),
            @Parameter(name = "direction", description = "Dirección de la ordenación", example = "asc"),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de funkos"),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @GetMapping()
    public ResponseEntity<PageResponse<FunkoResponseDto>> getFunkos(
            @RequestParam(required = false) Optional<String> name,
            @RequestParam(required = false) Optional<Double> price,
            @RequestParam(required = false) Optional<Integer> quantity,
            @RequestParam(required = false) Optional<String> categoria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            HttpServletRequest request
    ) {
        Sort sort =  direction.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(request.getRequestURL().toString());
        Page<FunkoResponseDto> result = funkoServices.findAll(name,price,quantity,categoria, pageable);
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(result, uriBuilder))
                .body(PageResponse.of(result, sortBy, direction));
    }

    /**
     * Obtiene un funko por su id
     * @param id id del funko
     * @return funko encontrado
     */
    @Operation(summary = "Obtiene un funko por su id", description = "Obtiene un funko por su id")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del funko", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko encontrado"),
            @ApiResponse(responseCode = "400", description = "Petición incorrecta"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @GetMapping("/{id}")
    public ResponseEntity<FunkoResponseDto> getFunko(@PathVariable Long id) {
        return ResponseEntity.ok(funkoServices.findById(id));
    }

    /**
     * Crea un nuevo funko
     * @param funko funko a crear
     * @return funko creado
     */
    @Operation(summary = "Crea un nuevo funko", description = "Crea un nuevo funko")
    @Parameters({
            @Parameter(name = "funko", description = "Funko a crear", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Funko creado"),
            @ApiResponse(responseCode = "400", description = "Funko no válido"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> postFunko(@Valid @RequestBody FunkoCreateRequest funko) {
        return ResponseEntity.status(HttpStatus.CREATED).body(funkoServices.save(funko));
    }

    /**
     * Actualiza un funko
     * @param id id del funko
     * @param funko funko a actualizar
     * @return funko actualizado
     */
    @Operation(summary = "Actualiza un funko", description = "Actualiza un funko")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del funko", example = "1", required = true),
            @Parameter(name = "funko", description = "Funko a actualizar", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko actualizado"),
            @ApiResponse(responseCode = "400", description = "Funko no válido"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> putFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateRequest funko) {
      return ResponseEntity.ok(funkoServices.update(id, funko));
    }

    /**
     * Actualiza un funko
     * @param id id del funko
     * @param funko funko a actualizar
     * @return funko actualizado
     */
    @Operation(summary = "Actualiza un funko", description = "Actualiza un funko")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del funko", example = "1", required = true),
            @Parameter(name = "funko", description = "Funko a actualizar", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Funko actualizado"),
            @ApiResponse(responseCode = "400", description = "Funko no válido"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunkoResponseDto> patchFunko(@PathVariable Long id, @Valid @RequestBody FunkoUpdateRequest funko) {
        return ResponseEntity.ok(funkoServices.update(id, funko));
    }

    /**
     * Elimina un funko
     * @param id id del funko
     * @return funko eliminado
     */
    @Operation(summary = "Elimina un funko", description = "Elimina un funko")
    @Parameters({
            @Parameter(name = "id", description = "Identificador del funko", example = "1", required = true)
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Funko eliminado"),
            @ApiResponse(responseCode = "400", description = "Funko no válido"),
            @ApiResponse(responseCode = "404", description = "Funko no encontrado"),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFunko(@PathVariable Long id) {
        funkoServices.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * Actualiza la imagen de un funko
     * @param id id del funko
     * @param file fichero a subir
     * @return funko actualizado
     */
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
    @PreAuthorize("hasRole('ADMIN')")
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

    /**
     * Manejador de excepciones de validacion
     * @param ex excepcion a manejar
     * @return  errores de validacion
     */
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
