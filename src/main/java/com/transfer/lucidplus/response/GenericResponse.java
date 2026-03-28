package com.transfer.lucidplus.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenericResponse {
	private String message;
	private LocalDateTime occuredAt;
}
