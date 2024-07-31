package com.messenger_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.messenger_server.domain.FriendRequest;
import com.messenger_server.domain.Message;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class WebSocketController {

	private final JwtUtil jwtUtil;
	private final CustomUserDetailsService userDetailsService;
	private static final Logger logger = Logger.getLogger(WebSocketController.class.getName());
	private final ObjectMapper objectMapper;

	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private static final String CHANNEL = "chat";

	public WebSocketController(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
		this.jwtUtil = jwtUtil;
		this.userDetailsService = userDetailsService;
		this.objectMapper = new ObjectMapper();
		this.objectMapper.registerModule(new JavaTimeModule());
	}

	@MessageMapping("/initialConnect")
	public void handleInitialConnect(Map<String, String> payload) {
		logger.info("Received message: " + payload);
		String token = payload.get("token");
		try {
			if (token != null && token.startsWith("Bearer ")) {
				String jwtToken = token.substring(7);
				String username = jwtUtil.extractUsername(jwtToken);
//				logger.info("Extracted username: " + username);

				if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
					UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					logger.info("Loaded user details: " + userDetails);

					if (jwtUtil.validateToken(jwtToken, userDetails)) {
						UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
										userDetails, null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authentication);
						logger.info("User authenticated: " + username);
						String welcomeMessage = createJsonMessage(Map.of("message", "Welcome, " + username));
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
		String errorMessage = createJsonMessage(Map.of("message", "Authentication failed"));
		messagingTemplate.convertAndSend("/topic/messages", errorMessage);
	}

	@MessageMapping("/chat")
	public void handleChatMessage(String payload) {
		logger.info("Received chat message: " + payload);

		try {
			Message messageObj = objectMapper.readValue(payload, Message.class);

			Long chatRoomId = messageObj.getChatRoomId();
			String message = messageObj.getContent();
			Long sender = messageObj.getSenderId();
			String username = messageObj.getUsername();

			// 디버깅을 위한 로그 추가
			logger.info("chatRoomId: " + chatRoomId);
			logger.info("message: " + message);
			logger.info("sender: " + sender);
			logger.info("username: " + username);

			if (message != null && !message.isEmpty()) {
				String createdAt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String chatMessage = createJsonMessage(Map.of(
								"chatRoomId", String.valueOf(chatRoomId),  // String으로 변환
								"content", message,
								"senderId", String.valueOf(sender),  // String으로 변환
								"username", username,
								"created_at", createdAt
				));
				logger.info("Publishing chat message: " + chatMessage);
				redisTemplate.convertAndSend(CHANNEL, chatMessage).subscribe();
				// 메시지 브로드캐스트
				messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, chatMessage);
			} else {
				logger.warning("Chat message content is null or empty");
			}
		} catch (Exception e) {
			logger.severe("Error handling chat message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@MessageMapping("/friendRequests")
	public void handleFriendRequestMessage(String payload) {
		logger.info("Received friend request message: " + payload);

		try {
			FriendRequest friendRequest = objectMapper.readValue(payload, FriendRequest.class);
			String notificationMessage = createJsonMessage(Map.of(
							"type", "friendRequest",
							"requesterId", String.valueOf(friendRequest.getRequesterId()),
							"recipientId", String.valueOf(friendRequest.getRecipientId()),
							"status", friendRequest.getStatus()
			));
			logger.info("Publishing friend request message: " + notificationMessage);
			messagingTemplate.convertAndSend("/topic/friendRequests/" + friendRequest.getRecipientId(), notificationMessage);
		} catch (Exception e) {
			logger.severe("Error handling friend request message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private String createJsonMessage(Map<String, String> keyValues) {
		try {
			return objectMapper.writeValueAsString(keyValues);
		} catch (Exception e) {
			logger.severe("Error creating JSON message: " + e.getMessage());
			return "{}";
		}
	}
}
