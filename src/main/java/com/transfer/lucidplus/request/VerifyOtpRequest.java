package com.transfer.lucidplus.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequest {
	
	@NotNull(message = "Email is required")
	@Email
	private String email;
	
	@NotBlank(message = "OTP is required")
	private String otp;
	
}
