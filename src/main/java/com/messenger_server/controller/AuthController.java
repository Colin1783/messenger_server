package com.messenger_server.controller;

import com.messenger_server.domain.User;
import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.service.UserService;
import com.messenger_server.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		userService.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
		try {
			// 사용자의 인증을 시도합니다.
			Authentication authentication = authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
			);

			// 인증된 사용자의 정보를 가져옵니다.

			final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
			final String jwt = jwtUtil.generateToken(userDetails);

			// JWT 토큰을 생성합니다.
			return ResponseEntity.ok(new AuthResponse(jwt));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
		}
	}
}

@Data
class AuthRequest {
	private String username;
	private String password;

	// getters and setters
}

@Data
class AuthResponse {
	private String jwt;

	public AuthResponse(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}
}
