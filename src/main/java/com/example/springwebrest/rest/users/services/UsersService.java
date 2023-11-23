package com.example.springwebrest.rest.users.services;

import com.example.springwebrest.rest.users.dto.UserInfoResponse;
import com.example.springwebrest.rest.users.dto.UserRequest;
import com.example.springwebrest.rest.users.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UsersService {
    Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable);
    UserInfoResponse findById(Long id);
    UserResponse save(UserRequest userRequest);
    UserResponse update(Long id, UserRequest userRequest);
    void deleteById(Long id);
}
