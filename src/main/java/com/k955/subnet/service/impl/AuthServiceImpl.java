package com.k955.subnet.service.impl;

import com.k955.subnet.dtos.Auth.AuthResponse;
import com.k955.subnet.dtos.Auth.LoginRequest;
import com.k955.subnet.dtos.Auth.SignupRequest;
import com.k955.subnet.entity.User;
import com.k955.subnet.enums.User.UserStatus;
import com.k955.subnet.exception.BadRequestException;
import com.k955.subnet.exception.ErrorCode;
import com.k955.subnet.exception.ResourceNotFoundException;
import com.k955.subnet.mapper.UserMapper;
import com.k955.subnet.repository.UserRepository;
import com.k955.subnet.security.JwtAuthUtil;
import com.k955.subnet.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtAuthUtil jwtAuthUtil;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse signup(SignupRequest signupRequest) {
        boolean exists = userRepository.existsByEmail(signupRequest.email());
        if(exists) throw new BadRequestException(ErrorCode.USER_ALREADY_EXISTS, "An account with this email already exists.");

        User user = User.builder()
                .name(signupRequest.name())
                .email(signupRequest.email())
                .password(signupRequest.password())
                .status(UserStatus.ACTIVE)
                .build();
        user.setPassword(passwordEncoder.encode(signupRequest.password()));
        User savedUser = userRepository.save(user);

        String token = jwtAuthUtil.generateAccessToken(savedUser);

        return new AuthResponse(token, userMapper.toUserProfileResponse(savedUser));
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password())
            );
        } catch (Exception e) {
            throw new BadRequestException(ErrorCode.INVALID_CREDENTIALS, "Invalid Email or Password.");
        }

        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorCode.USER_NOT_FOUND, "User with email: " + loginRequest.email() + " not found."
                ));

        String token = jwtAuthUtil.generateAccessToken(user);

        return new AuthResponse(token, userMapper.toUserProfileResponse(user));
    }

}
