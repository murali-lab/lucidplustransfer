package com.transfer.lucidplus.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.lucidplus.response.AccountResponse;
import com.transfer.lucidplus.security.CustomUserDetails;
import com.transfer.lucidplus.service.AccountService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/account")
@Slf4j
@Tag(name = "Account", description = "Account and balance APIs")
public class AccountController {

	@Autowired
	private AccountService accountService;

	@GetMapping("/me")
	public ResponseEntity<AccountResponse> getMyAccount(@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("Account fetch request for email: {}", userDetails.getUsername());
		return ResponseEntity.ok(accountService.getMyAccount(userDetails));
	}
}