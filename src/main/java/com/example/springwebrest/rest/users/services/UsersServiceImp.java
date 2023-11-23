package com.example.springwebrest.rest.users.services;

import com.example.springwebrest.rest.pedidos.repositories.PedidosRepository;
import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.dto.UserRequest;
import com.example.springwebrest.rest.users.dto.UserResponse;
import com.example.springwebrest.rest.users.exceptions.UserNameOrEmailExists;
import com.example.springwebrest.rest.users.exceptions.UserNotFound;
import com.example.springwebrest.rest.users.mappers.UsersMapper;
import com.example.springwebrest.rest.users.models.User;
import com.example.springwebrest.rest.users.repositories.UsersRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImp implements UsersService{
    private final UsersRepository usersRepository;
    private final PedidosRepository pedidosRepository;
    private final UsersMapper usersMapper;

    public UsersServiceImp(UsersRepository usersRepository, PedidosRepository pedidosRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.pedidosRepository = pedidosRepository;
        this.usersMapper = usersMapper;
    }

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        Specification<User> specUsernameUser = (root, query, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<User> specEmailUser = (root, query, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<User> specIsDeleted = (root, query, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));
        Specification<User> criterio = Specification.where(specUsernameUser)
                .and(specEmailUser)
                .and(specIsDeleted);

        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    public UserInfoResponse findById(Long id) {
        var user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        var pedidos = pedidosRepository.findPedidosIdsByIdUsuario(id).stream().map(p -> p.getId().toHexString()).toList();
        return usersMapper.toUserInfoResponse(user, pedidos);
    }

    @Override
    public UserResponse save(UserRequest userRequest) {
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    throw new UserNameOrEmailExists("Username o email ya existe");
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    public UserResponse update(Long id, UserRequest userRequest) {
        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        usersRepository.findByUsernameEqualsIgnoreCase(userRequest.getUsername())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        throw new UserNameOrEmailExists("Username ya existe");
                    }
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    public void deleteById(Long id) {
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));

        if (pedidosRepository.existsByIdUsuario(id)) {
            usersRepository.updateIsDeletedToTrueById(id);
        } else {
            usersRepository.delete(user);
        }
    }
}
