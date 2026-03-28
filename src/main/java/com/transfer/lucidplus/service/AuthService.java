package com.transfer.lucidplus.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.constants.AccountStatus;
import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.exception.AppException;
import com.transfer.lucidplus.request.LoginRequest;
import com.transfer.lucidplus.request.RegisterRequest;
import com.transfer.lucidplus.response.GenericResponse;
import com.transfer.lucidplus.response.LoginResponse;
import com.transfer.lucidplus.security.CustomUserDetails;
import com.transfer.lucidplus.security.JwtUtils;
import com.transfer.lucidplus.repository.UserRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OtpService otpService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;
    
    
    @Value("${app.cookie.max.age.minutes}")
    private int cookieExpireDuration;

    @Transactional
    public GenericResponse registerUser(RegisterRequest request) {
        try {
            Optional<User> existingUser = userRepo.findByEmail(request.getEmail().toLowerCase());
            
            if (existingUser.isPresent()) {
                throw new AppException("Email already exists!");
            }

            LocalDateTime now = LocalDateTime.now();
            
            User newUser = User.builder()
                    .name(request.getName())
                    .email(request.getEmail().toLowerCase())
                    .mobile(request.getMobile())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .status(AccountStatus.PENDING.name())
                    .createdAt(now)
                    .modifiedAt(now)
                    .build();

            userRepo.save(newUser);
            
            otpService.sendOtp(request.getEmail().toLowerCase());

            return GenericResponse.builder()
                    .message("User registered successfully. Please verify your email with the OTP sent.")
                    .occuredAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Registration failed for email: {} error: {}", request.getEmail(), e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            if (!AccountStatus.ACTIVE.name().equals(userDetails.getAccountStatus())) {
                throw new AppException("Account is not active. Please verify your email first.");
            }

            String accessToken = jwtUtils.generateToken(userDetails);

            Cookie accessCookie = new Cookie("LuCiDpLuStOkEn", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(cookieExpireDuration);
            response.addCookie(accessCookie);

            return LoginResponse.builder()
                    .name(userDetails.getName())
                    .email(userDetails.getUsername())
                    .mobile(userDetails.getMobile())
                    .accountStatus(userDetails.getAccountStatus())
                    .occuredAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Login failed for email: {} error: {}", request.getEmail(), e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    public GenericResponse logout(HttpServletResponse response) {
    	
        Cookie accessCookie = new Cookie("LuCiDpLuStOkEn", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);
        response.addCookie(accessCookie);

        return GenericResponse.builder()
                .message("Logged out successfully.")
                .occuredAt(LocalDateTime.now())
                .build();
    }
}