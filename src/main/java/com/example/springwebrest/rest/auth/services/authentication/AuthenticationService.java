package com.example.springwebrest.rest.auth.services.authentication;

import com.example.springwebrest.rest.auth.dto.JwtAuthResponse;
import com.example.springwebrest.rest.auth.dto.UserSignInRequest;
import com.example.springwebrest.rest.auth.dto.UserSignUpRequest;

public interface AuthenticationService {
    JwtAuthResponse signUp(UserSignUpRequest request);

    JwtAuthResponse signIn(UserSignInRequest request);
}
