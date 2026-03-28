package com.transfer.lucidplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.lucidplus.request.LoginRequest;
import com.transfer.lucidplus.request.RegisterRequest;
import com.transfer.lucidplus.response.GenericResponse;
import com.transfer.lucidplus.response.LoginResponse;
import com.transfer.lucidplus.service.AuthService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@Validated
@Slf4j
@Tag(name = "Auth", description = "Register and Login APIs")
public class AuthController {

	@Autowired
	private AuthService authService;

	@PostMapping("/register")
	public ResponseEntity<GenericResponse> register(@Valid @RequestBody RegisterRequest request) {
		log.info("Register request for email: {}", request.getEmail());
		return new ResponseEntity<>(authService.registerUser(request), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		log.info("Login request for email: {}", request.getEmail());
		return ResponseEntity.ok(authService.login(request, response));
	}

	@PostMapping("/logout")
	public ResponseEntity<GenericResponse> logout(HttpServletResponse response) {
		return ResponseEntity.ok(authService.logout(response));
	}
}