package com.transfer.lucidplus.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.constants.TransactionStatus;
import com.transfer.lucidplus.entity.Account;
import com.transfer.lucidplus.entity.Transaction;
import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.exception.AppException;
import com.transfer.lucidplus.repository.AccountRepository;
import com.transfer.lucidplus.repository.TransactionRepository;
import com.transfer.lucidplus.request.TransferRequest;
import com.transfer.lucidplus.response.GenericResponse;
import com.transfer.lucidplus.response.TransactionResponse;
import com.transfer.lucidplus.security.CustomUserDetails;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TransactionService {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private TransactionRepository transactionRepo;

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Transactional
    public GenericResponse transfer(TransferRequest request, CustomUserDetails userDetails) {
        try {
            User sender = userService.fetchUserByEmail(userDetails.getUsername());
            Account senderAccount = accountService.getAccountByUser(sender);
            Account receiverAccount = accountService.getAccountByAccountNumber(request.getReceiverAccountNumber());

            if (senderAccount.getAccountNumber().equals(receiverAccount.getAccountNumber())) {
                throw new AppException("Cannot transfer to your own account");
            }

            if (senderAccount.getBalance().compareTo(request.getAmount()) < 0) {
                throw new AppException("Insufficient balance. Available: " + senderAccount.getBalance());
            }

            senderAccount.setBalance(senderAccount.getBalance().subtract(request.getAmount()));
            senderAccount.setModifiedAt(LocalDateTime.now());
            accountRepo.save(senderAccount);

            receiverAccount.setBalance(receiverAccount.getBalance().add(request.getAmount()));
            receiverAccount.setModifiedAt(LocalDateTime.now());
            accountRepo.save(receiverAccount);

            Transaction transaction = Transaction.builder()
                    .sender(sender)
                    .receiver(receiverAccount.getUser())
                    .amount(request.getAmount())
                    .status(TransactionStatus.SUCCESS.name())
                    .referenceId("TXN-" + UUID.randomUUID().toString().toUpperCase())
                    .remark(request.getRemark())
                    .createdAt(LocalDateTime.now())
                    .build();
            transactionRepo.save(transaction);

            return GenericResponse.builder()
                    .message("Transaction completed successfully. Reference ID: " + transaction.getReferenceId())
                    .occuredAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Transaction failed for email: {} error: {}", userDetails.getUsername(), e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    public List<TransactionResponse> getTransactionHistory(long userId) {
        try {
        	
            return transactionRepo.findAllByUserId(userId)
                    .stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to fetch transactions for userId: {} error: {}", userId, e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .referenceId(t.getReferenceId())
                .senderName(t.getSender().getName())
                .senderEmail(t.getSender().getEmail())
                .receiverName(t.getReceiver().getName())
                .receiverEmail(t.getReceiver().getEmail())
                .amount(t.getAmount())
                .status(t.getStatus())
                .remark(t.getRemark())
                .createdAt(t.getCreatedAt())
                .build();
    }
}