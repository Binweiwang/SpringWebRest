package com.example.springwebrest.rest.users.controllers;

import com.example.springwebrest.rest.pedidos.models.Pedido;
import com.example.springwebrest.rest.pedidos.services.PedidosService;
import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.dto.UserRequest;
import com.example.springwebrest.rest.users.dto.UserResponse;
import com.example.springwebrest.rest.users.models.User;
import com.example.springwebrest.rest.users.services.UsersService;
import com.example.springwebrest.utils.pagination.PageResponse;
import com.example.springwebrest.utils.pagination.PaginationLinksUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
@RestController
@RequestMapping("${api.version}/users")
@PreAuthorize("hasRole('USER')")
public class UsersController {
    private final UsersService usersService;
    private final PedidosService pedidosService;
    private final PaginationLinksUtils paginationLinksUtils;

    @Autowired
    public UsersController(UsersService usersService, PedidosService pedidosService, PaginationLinksUtils paginationLinksUtils) {
        this.usersService = usersService;
        this.pedidosService = pedidosService;
        this.paginationLinksUtils = paginationLinksUtils;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<UserResponse>> findAll(
            @RequestParam(required = false) Optional<String> username,
            @RequestParam(required = false) Optional<String> email,
            @RequestParam(required = false) Optional<Boolean> isDeleted,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order,
            HttpServletRequest request
    ) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(request.getRequestURL().toString());
        Page<UserResponse> pageResult = usersService.findAll(username, email, isDeleted, PageRequest.of(page, size, sort));
        return ResponseEntity.ok()
                .header("link", paginationLinksUtils.createLinkHeader(pageResult, uriBuilder))
                .body(PageResponse.of(pageResult, sortBy, order));

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserInfoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(usersService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usersService.save(userRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(usersService.update(id, userRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usersService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserInfoResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(usersService.findById(user.getId()));
    }

    @PutMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal User user, @Valid @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(usersService.update(user.getId(), userRequest));
    }

    @DeleteMapping("/me/profile")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteProfile(@AuthenticationPrincipal User user) {
        usersService.deleteById(user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/pedidos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<Pedido>> getPedidosByUsuario(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        Sort sort = order.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(PageResponse.of(pedidosService.findByIdUsuario(user.getId(), pageable), sortBy, order));
    }

    @GetMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Pedido> getPedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido
    ) {
        Pedido pedido = pedidosService.findById(idPedido);
        if (!pedido.getIdUsuario().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(pedidosService.findById(idPedido));
    }

    @PostMapping("/me/pedidos")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Pedido> createPedido(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody Pedido pedido) {
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidosService.save(pedido));
    }

    @PutMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Pedido> updatePedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido,
            @Valid @RequestBody Pedido pedido) {
        pedido.setIdUsuario(user.getId());
        return ResponseEntity.ok(pedidosService.update(idPedido, pedido));
    }

    @DeleteMapping("/me/pedidos/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deletePedido(
            @AuthenticationPrincipal User user,
            @PathVariable("id") ObjectId idPedido) {
        pedidosService.delete(idPedido);
        return ResponseEntity.noContent().build();
    }

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


