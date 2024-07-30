package com.messenger_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messenger_server.domain.Message;
import com.messenger_server.service.MessageService;
import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class WebSocketController {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private static final Logger logger = Logger.getLogger(WebSocketController.class.getName());
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Autowired
	private MessageService messageService;

	private static final String CHANNEL = "chat";

	public WebSocketController(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService, MessageService messageService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.messageService = messageService;
	}

	@MessageMapping("/initialConnect")
	public void handleInitialConnect(Map<String, String> payload) {
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
						String welcomeMessage = createJsonMessage("message", "Welcome, " + username);
						messagingTemplate.convertAndSend("/topic/messages", welcomeMessage);
						return;
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
		String errorMessage = createJsonMessage("message", "Authentication failed");
		messagingTemplate.convertAndSend("/topic/messages", errorMessage);
	}

	@MessageMapping("/chat")
	public void handleChatMessage(Map<String, String> payload) {
		logger.info("Received chat message: " + payload);
		String chatRoomId = payload.get("chatRoomId");
		String messageContent = payload.get("content");
		String senderId = payload.get("senderId");

		if (chatRoomId == null || messageContent == null || senderId == null) {
			logger.warning("Chat message payload is missing fields: " + payload);
			return;
		}

		Long chatRoomIdLong = Long.parseLong(chatRoomId);
		Long senderIdLong = Long.parseLong(senderId);

		Message message = messageService.saveMessage(chatRoomIdLong, senderIdLong, messageContent);
		if (message != null) {
			String chatMessage = createJsonMessage("chatRoomId", chatRoomId, "content", messageContent, "senderId", senderId);
			logger.info("Publishing message to Redis: " + chatMessage);
			redisTemplate.convertAndSend(CHANNEL, chatMessage).subscribe();
		} else {
			logger.warning("Failed to save message to database");
		}
	}


	private String createJsonMessage(String... keyValues) {
		if (keyValues.length % 2 != 0) {
			throw new IllegalArgumentException("Invalid key-values pairs");
		}
		StringBuilder jsonBuilder = new StringBuilder("{");
		for (int i = 0; i < keyValues.length; i += 2) {
			if (i > 0) {
				jsonBuilder.append(",");
			}
			jsonBuilder.append("\"").append(keyValues[i]).append("\":\"").append(keyValues[i + 1]).append("\"");
		}
		jsonBuilder.append("}");
		return jsonBuilder.toString();
	}

	// Redis를 통해 메시지를 브로드캐스트
	public void broadcastMessage(String message) {
		if (message != null && !message.isEmpty()) {
			messagingTemplate.convertAndSend("/topic/messages", message);
		} else {
			logger.warning("Broadcast message content is null or empty");
		}
	}
}
