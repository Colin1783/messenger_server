package com.messenger_server.service;

import com.messenger_server.domain.User;
import com.messenger_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserMapper userMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userMapper.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), new ArrayList<>());
	}

	public UserDetails loadUserByUserId(Long userId) throws UsernameNotFoundException {
		String username = userMapper.findUsernameById(userId);
		if (username == null) {
			throw new UsernameNotFoundException("User not found");
		}
		return loadUserByUsername(username);
	}
}
