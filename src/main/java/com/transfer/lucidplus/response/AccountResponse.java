package com.transfer.lucidplus.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
    private String accountNumber;
    private BigDecimal balance;
    private String ownerName;
    private String ownerEmail;
    private LocalDateTime createdAt;
}