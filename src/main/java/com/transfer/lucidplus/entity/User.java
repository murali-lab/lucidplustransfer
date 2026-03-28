package com.transfer.lucidplus.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private long id;
	
	@Column(name="name", length = 50, nullable = false)
	private String name;
	
	@Column(name="mobile", length = 15, nullable= false)
	private String mobile;
	
	@Column(name = "email", length = 75, nullable = false, unique = true)
	private String email;
	
	@JsonIgnore
	@Column(name="password", nullable = false)
	private String password;
	
	@Column(name = "status", nullable = false)
	private String status;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "modified_at", nullable = false)
	private LocalDateTime modifiedAt;


}
