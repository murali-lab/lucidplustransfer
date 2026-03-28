package com.transfer.lucidplus.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transfer.lucidplus.request.TransferRequest;
import com.transfer.lucidplus.response.GenericResponse;
import com.transfer.lucidplus.response.TransactionResponse;
import com.transfer.lucidplus.security.CustomUserDetails;
import com.transfer.lucidplus.service.TransactionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/transaction")
@Slf4j
@Tag(name = "Transaction", description = "Transfer and transaction history APIs")
public class TransactionController {

	@Autowired
	private TransactionService transactionService;

	@PostMapping("/transfer")
	public ResponseEntity<GenericResponse> transfer(@Valid @RequestBody TransferRequest request,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("Transfer request from email: {} amount: {}", userDetails.getUsername(), request.getAmount());
		return ResponseEntity.ok(transactionService.transfer(request, userDetails));
	}

	@GetMapping("/history/{userId}")
	public ResponseEntity<List<TransactionResponse>> getHistory(
			@PathVariable(name = "userId") long userId, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		log.info("Transaction history request for userId: {} by this userId", userId, userDetails.getId());
		return ResponseEntity.ok(transactionService.getTransactionHistory(userId));
	}
}