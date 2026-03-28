package com.transfer.lucidplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.lucidplus.request.SendOtpRequest;
import com.transfer.lucidplus.request.VerifyOtpRequest;
import com.transfer.lucidplus.response.GenericResponse;
import com.transfer.lucidplus.service.OtpService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otp")
@Validated
@Slf4j
@Tag(name = "OTP", description = "OTP send and verify APIs")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<GenericResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        log.info("Send OTP request for email: {}", request.getEmail());
        otpService.sendOtp(request.getEmail());
        return ResponseEntity.ok(GenericResponse.builder()
                .message("OTP sent successfully to " + request.getEmail())
                .occuredAt(java.time.LocalDateTime.now())
                .build());
    }

    @PostMapping("/verify")
    public ResponseEntity<GenericResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        log.info("Verify OTP request for email: {}", request.getEmail());
        return ResponseEntity.ok(otpService.verifyOtp(request));
    }
}