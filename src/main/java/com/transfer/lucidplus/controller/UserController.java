package com.transfer.lucidplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.security.CustomUserDetails;
import com.transfer.lucidplus.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
@Tag(name = "User", description = "User profile APIs")
public class UserController {

    @Autowired
    private UserService userService;	

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("Profile request for email: {}", userDetails.getUsername());
        return ResponseEntity.ok(userService.getProfile(userDetails));
    }
}