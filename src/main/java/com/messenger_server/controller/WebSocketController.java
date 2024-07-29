package com.messenger_server.controller;

import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.util.JwtUtil;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Controller
public class WebSocketController {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private static final Logger logger = Logger.getLogger(WebSocketController.class.getName());

	public WebSocketController(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@MessageMapping("/initialConnect")
	@SendTo("/topic/messages")
	public Map<String, String> handleInitialConnect(Map<String, String> payload) {
		try {
			logger.info("Received initial connection message: " + payload);
			// 여기서는 토큰 검증 로직을 제거하고 기본적인 메시지 응답을 처리합니다.
			return Map.of("message", "Connection established");
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error in handleInitialConnect: ", e);
			return Map.of("message", "Error in connection establishment");
		}
	}
}
