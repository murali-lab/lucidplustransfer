package com.transfer.lucidplus.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.transfer.lucidplus.entity.Otp;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long>{

	Otp findTopByEmailOrderByCreatedAtDesc(String email);

	
}
