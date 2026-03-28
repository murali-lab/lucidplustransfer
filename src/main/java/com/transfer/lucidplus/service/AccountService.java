package com.transfer.lucidplus.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.entity.Account;
import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.exception.AppException;
import com.transfer.lucidplus.exception.NotFoundException;
import com.transfer.lucidplus.repository.AccountRepository;
import com.transfer.lucidplus.response.AccountResponse;
import com.transfer.lucidplus.security.CustomUserDetails;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private UserService userService;
    
    private static final String DEFAULT_BALANCE = "1000.00";

    @Transactional
    public Account createAccountForUser(User user) {
        LocalDateTime now = LocalDateTime.now();
        Account account = Account.builder()
                .user(user)
                .accountNumber(generateAccountNumber())
                .balance(new BigDecimal(DEFAULT_BALANCE)) // default balance on activation
                .createdAt(now)
                .modifiedAt(now)
                .build();
        return accountRepo.save(account);
    }

    public AccountResponse getMyAccount(CustomUserDetails userDetails) {
        try {
            User user = userService.fetchUserByEmail(userDetails.getUsername());
            Account account = accountRepo.findByUser(user)
                    .orElseThrow(() -> new NotFoundException("Account not found"));

            return buildAccountResponse(account);
        } catch (Exception e) {
            log.error("Failed to fetch account for email: {} error: {}", userDetails.getUsername(), e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    public Account getAccountByUser(User user) {
        return accountRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Account not found for user: " + user.getEmail()));
    }

    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new NotFoundException("Account not found for account number: " + accountNumber));
    }

    private AccountResponse buildAccountResponse(Account account) {
        return AccountResponse.builder()
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .ownerName(account.getUser().getName())
                .ownerEmail(account.getUser().getEmail())
                .createdAt(account.getCreatedAt())
                .build();
    }

    private synchronized String generateAccountNumber() {
        long timestamp = System.currentTimeMillis();
        int random = new java.util.Random().nextInt(1000);

        return "LP" + timestamp + String.format("%03d", random);
    }
}