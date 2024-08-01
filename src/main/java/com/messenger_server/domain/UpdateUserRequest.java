package com.messenger_server.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
	private Long id;
	private String email;
	private String name;
	private LocalDate birthdate;
	private String cellphone;
	private String currentPassword;
	private String newPassword;

	// getters and setters
}
