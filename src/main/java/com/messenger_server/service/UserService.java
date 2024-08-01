package com.messenger_server.service;

import com.messenger_server.domain.UpdateUserRequest;
import com.messenger_server.domain.User;
import com.messenger_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void save(User user) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreatedAt(LocalDate.now());
		user.setRole("USER"); // 기본 역할 설정
		user.setLoginStatus(false);
		user.setLastLoggedIn(null);
		userMapper.save(user);
	}

	public User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}

	public void updateLoginStatusAndLastLoggedIn(String username, boolean loginStatus) {
		Timestamp lastLoggedIn = loginStatus ? Timestamp.from(Instant.now()) : null;
		userMapper.updateLoginStatusAndLastLoggedIn(username, loginStatus, lastLoggedIn);
	}

	public void logout(String username) {
		updateLoginStatusAndLastLoggedIn(username, false);
	}

	public List<User> searchUsers(String query) {
		return userMapper.searchUsers(query);
	}

	public void updateUser(UpdateUserRequest request) {
		User user = userMapper.findById(request.getId());
		if (user == null) {
			throw new IllegalArgumentException("User not found");
		}
		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect");
		}
		if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(request.getNewPassword()));
		}
		user.setEmail(request.getEmail());
		user.setName(request.getName());
		user.setBirthdate(request.getBirthdate());
		user.setCellphone(request.getCellphone());
		userMapper.update(user);
	}
}
