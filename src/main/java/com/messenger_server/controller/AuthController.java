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

	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;
	private final JwtUtil jwtUtil;
	private final UserService userService;

	@Autowired
	public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	// 회원가입
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody User user) {
		userService.save(user);
		return ResponseEntity.ok("User registered successfully");
	}

	// 로그인
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
		// 인증 처리
		try {
			Authentication authentication = authenticationManager.authenticate(
							new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
			);
			// 인증 성공 시 JWT 토큰 생성
			UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
			String jwt = jwtUtil.generateToken(userDetails);

			// 로그인 상태 업데이트
			userService.updateLoginStatusAndLastLoggedIn(authRequest.getUsername(), true);

			// JWT 토큰 생성
			return ResponseEntity.ok(new AuthResponse(jwt));
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed");
		}
	}

	// 로그아웃
	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		// 토큰에서 username 추출
		String username = jwtUtil.extractUsername(token);
		userService.logout(username);
		return ResponseEntity.ok("Logged out successfully");
	}
}

@Data
class AuthRequest {
	private String username;
	private String password;
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
