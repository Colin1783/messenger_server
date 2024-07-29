package com.messenger_server.domain;

import lombok.Data;

import java.time.LocalDate;
import java.sql.Timestamp;

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
	private boolean loginStatus;
	private Timestamp lastLoggedIn;
}
