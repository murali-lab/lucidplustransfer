package com.transfer.lucidplus.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.transfer.lucidplus.entity.Account;
import com.transfer.lucidplus.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
    Optional<Account> findByUser(User user);
    
    Optional<Account> findByAccountNumber(String accountNumber);
}