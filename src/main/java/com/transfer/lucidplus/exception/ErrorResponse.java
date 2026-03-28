package com.transfer.lucidplus.exception;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
	
	private int statusCode;
	
	private String errorMsg;
	
	private LocalDateTime occuredAt;

}

