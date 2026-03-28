package com.transfer.lucidplus.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String name;
    private String email;
    private String mobile;
    private String accountStatus;
    private LocalDateTime occuredAt;
}