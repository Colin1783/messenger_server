package com.messenger_server.service;

import com.messenger_server.domain.User;
import com.messenger_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
		userMapper.save(user);
	}

	public User findByUsername(String username) {
		return userMapper.findByUsername(username);
	}
}
