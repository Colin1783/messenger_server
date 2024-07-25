package com.messenger_server.domain;

import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Data
public class User {
	private Long id;
	private String username;
	private String password;
	private String email;
	private String name;
	private LocalDate birthdate;
	private String cellphone;
	private LocalDate createdAt;
	private String role;
}
