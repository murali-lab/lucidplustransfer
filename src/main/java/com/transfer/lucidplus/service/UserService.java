package com.transfer.lucidplus.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.transfer.lucidplus.entity.User;
import com.transfer.lucidplus.exception.AppException;
import com.transfer.lucidplus.exception.NotFoundException;
import com.transfer.lucidplus.repository.UserRepository;
import com.transfer.lucidplus.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	@Autowired
	private UserRepository userRepo;

	public User getProfile(CustomUserDetails userDetails) {
		try {
			return userRepo.findByEmail(userDetails.getUsername())
					.orElseThrow(() -> new NotFoundException("User not found"));
		} catch (Exception e) {
			log.error("Failed to fetch profile for email: {} error: {}", userDetails.getUsername(), e.getMessage());
			throw new AppException(e.getMessage());
		}
	}

	public User fetchUserByEmail(String email) {
		return userRepo.findByEmail(email)
				.orElseThrow(() -> new NotFoundException("User not found for email: " + email));
	}

	public User fetchUserById(long id) {
		return userRepo.findById(id).orElseThrow(() -> new NotFoundException("User not found for id: " + id));
	}
}