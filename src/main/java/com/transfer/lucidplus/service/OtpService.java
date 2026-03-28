package com.transfer.lucidplus.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.constants.AccountStatus;
import com.transfer.lucidplus.constants.OtpStatus;
import com.transfer.lucidplus.entity.Otp;
import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.exception.AppException;
import com.transfer.lucidplus.exception.NotFoundException;
import com.transfer.lucidplus.repository.OtpRepository;
import com.transfer.lucidplus.repository.UserRepository;
import com.transfer.lucidplus.request.VerifyOtpRequest;
import com.transfer.lucidplus.response.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OtpService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepository otpRepo;

    @Lazy
    @Autowired
    private AccountService accountService;

    @Value("${app.otp.expiration.duration.minutes}")
    private int otpExpirationMinutes;

    @Value("${app.otp.lockout.duration.minutes}")
    private int otpLockoutMinutes;

    @Value("${app.otp.max.attempt.count}")
    private int otpMaxAttempt;

    private static final SecureRandom secureRandom = new SecureRandom();

    @Async
    public void sendOtp(String email) {
        try {
            fetchUser(email);
            expirePreviousOtp(email);
            Otp otp = createAndSaveOtp(email);
            emailService.sendEmail(email, "OTP", otp.getOtpCode(), otpExpirationMinutes);
        } catch (Exception e) {
            log.error("Error during send otp for email: {} and error: {}", email, e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    public GenericResponse verifyOtp(VerifyOtpRequest request) {
        try {
            Otp otp = otpRepo.findTopByEmailOrderByCreatedAtDesc(request.getEmail());
            if (otp == null) throw new AppException("No OTP found for this email");

            checkIfLocked(otp);
            checkIfExpired(otp);
            validateOtpCode(request.getOtp(), otp);

            otp.setStatus(OtpStatus.VERIFIED.name());
            otpRepo.save(otp);

            User user = fetchUser(request.getEmail());
            updateUserStatusAndCreateAccount(user); //creates account on activation

            return GenericResponse.builder()
                    .message("OTP verified successfully. Your account is now active.")
                    .occuredAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Error during verify otp for email: {} and error: {}", request.getEmail(), e.getMessage());
            throw new AppException(e.getMessage());
        }
    }

    private User fetchUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found for this email: " + email));
    }

    private void expirePreviousOtp(String email) {
        Otp lastOtp = otpRepo.findTopByEmailOrderByCreatedAtDesc(email);
        if (lastOtp == null) return;

        if (OtpStatus.LOCKED.name().equals(lastOtp.getStatus())) {
            LocalDateTime unlockTime = lastOtp.getLockedAt().plusMinutes(otpLockoutMinutes);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                long remaining = Duration.between(LocalDateTime.now(), unlockTime).toMinutes();
                throw new AppException("You are locked. Try again after " + remaining + " minutes");
            }
        }

        lastOtp.setStatus(OtpStatus.EXPIRED.name());
        lastOtp.setLockedAt(null);
        otpRepo.save(lastOtp);
    }

    private Otp createAndSaveOtp(String email) {
        Otp otp = Otp.builder()
                .email(email)
                .otpCode(generateSixDigitOtp())
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpirationMinutes))
                .status(OtpStatus.ACTIVE.name())
                .noOfAttempt(otpMaxAttempt)
                .createdAt(LocalDateTime.now())
                .build();
        return otpRepo.save(otp);
    }

    private void checkIfLocked(Otp otp) {
        if (!OtpStatus.LOCKED.name().equals(otp.getStatus())) return;

        LocalDateTime unlockTime = otp.getLockedAt().plusMinutes(otpLockoutMinutes);
        if (LocalDateTime.now().isBefore(unlockTime)) {
            long remaining = Duration.between(LocalDateTime.now(), unlockTime).toMinutes();
            throw new AppException("Try again after " + remaining + " minutes");
        }

        otp.setStatus(OtpStatus.EXPIRED.name());
        otp.setLockedAt(null);
        otpRepo.save(otp);
        throw new AppException("OTP expired. Request a new OTP");
    }

    private void checkIfExpired(Otp otp) {
        if (OtpStatus.EXPIRED.name().equals(otp.getStatus())) {
            throw new AppException("OTP expired");
        }
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            otp.setStatus(OtpStatus.EXPIRED.name());
            otpRepo.save(otp);
            throw new AppException("OTP expired");
        }
    }

    private void validateOtpCode(String inputOtp, Otp otp) {
        if (inputOtp.equals(otp.getOtpCode())) return;

        int remaining = otp.getNoOfAttempt() - 1;
        otp.setNoOfAttempt(remaining);

        if (remaining <= 0) {
            otp.setStatus(OtpStatus.LOCKED.name());
            otp.setLockedAt(LocalDateTime.now());
            otpRepo.save(otp);
            throw new AppException("Maximum attempts reached. Locked for "
                    + otpLockoutMinutes + " minutes");
        }

        otpRepo.save(otp);
        throw new AppException("Invalid OTP. Remaining attempts: " + remaining);
    }

    private static String generateSixDigitOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }

    private void updateUserStatusAndCreateAccount(User user) {
        user.setStatus(AccountStatus.ACTIVE.name());
        user.setModifiedAt(LocalDateTime.now());
        userRepo.save(user);
        accountService.createAccountForUser(user);
    }
}