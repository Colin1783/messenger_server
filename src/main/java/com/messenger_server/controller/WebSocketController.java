package com.messenger_server.controller;

import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.logging.Logger;

@Controller
public class WebSocketController {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private static final Logger logger = Logger.getLogger(WebSocketController.class.getName());

	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private static final String CHANNEL = "chat";

	public WebSocketController(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
	}

	@MessageMapping("/initialConnect")
	@SendTo("/topic/messages")
	public Map<String, String> handleInitialConnect(Map<String, String> payload) {
		logger.info("Received message: " + payload);
		String token = payload.get("token");
		try {
			if (token != null && token.startsWith("Bearer ")) {
				String jwtToken = token.substring(7);
				String username = jwtUtil.extractUsername(jwtToken);
				logger.info("Extracted username: " + username);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					logger.info("Loaded user details: " + userDetails);

					if (jwtUtil.validateToken(jwtToken, userDetails)) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
										userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authentication);
						logger.info("User authenticated: " + username);
						return Map.of("message", "Welcome, " + username);
					} else {
						logger.warning("Token validation failed for user: " + username);
					}
				} else {
					logger.warning("Username is null or authentication is not null");
				}
			} else {
				logger.warning("Token is null or does not start with Bearer ");
			}
		} catch (Exception e) {
			logger.severe("Error in handleInitialConnect: " + e.getMessage());
			e.printStackTrace();
		}
		logger.warning("Authentication failed");
		return Map.of("message", "Authentication failed");
	}

	@MessageMapping("/chat")
	public void handleChatMessage(Map<String, String> payload) {
		logger.info("Received chat message: " + payload);
		String message = payload.get("message");
		redisTemplate.convertAndSend(CHANNEL, message).subscribe();
	}

	// Redis를 통해 메시지를 브로드캐스트
	public void broadcastMessage(String message) {
		messagingTemplate.convertAndSend("/topic/messages", message);
	}
}
