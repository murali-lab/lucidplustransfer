package com.transfer.lucidplus.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private String referenceId;
    private String senderName;
    private String senderEmail;
    private String receiverName;
    private String receiverEmail;
    private BigDecimal amount;
    private String status;
    private String remark;
    private LocalDateTime createdAt;
}