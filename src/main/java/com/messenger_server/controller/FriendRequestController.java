package com.messenger_server.controller;

import com.messenger_server.domain.FriendRequest;
import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.service.FriendRequestService;
import com.messenger_server.util.JwtUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();
	private static final Logger logger = Logger.getLogger(FriendRequestController.class.getName());

	@PostMapping("/send")
	public void sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long recipientId) {
		logger.info("sendFriendRequest called with requesterId: " + requesterId + " and recipientId: " + recipientId);
		friendRequestService.sendFriendRequest(requesterId, recipientId);
	}

	@GetMapping("/pending/{recipientId}")
	public List<FriendRequest> getPendingRequests(@PathVariable Long recipientId) {
		logger.info("getPendingRequests called with recipientId: " + recipientId);
		return friendRequestService.getPendingRequests(recipientId);
	}

	@PostMapping("/respond")
	public void respondToFriendRequest(@RequestBody RespondFriendRequest request) {
		logger.info("respondToFriendRequest called with requesterId: " + request.getRequesterId() + ", recipientId: " + request.getRecipientId() + ", status: " + request.getStatus());
		friendRequestService.respondToFriendRequest(request.getRequesterId(), request.getRecipientId(), request.getStatus(), request.getRequesterUsername());
	}

	@GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeNotifications(@RequestParam String token) {
		String username;
		try {
			username = jwtUtil.extractUsername(token);
			UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
			if (username == null || !jwtUtil.validateToken(token, userDetails)) {
				throw new IllegalArgumentException("Invalid or expired token");
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid or expired token", e);
		}

		logger.info("New SSE connection for username: " + username);
		SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃

		// 기존의 동일한 SSE 연결이 있는지 확인하고 제거
		List<SseEmitter> userEmitters = emitters.computeIfAbsent(username, k -> new ArrayList<>());
		userEmitters.forEach(SseEmitter::complete);
		userEmitters.clear();

		userEmitters.add(emitter);

		emitter.onCompletion(() -> {
			userEmitters.remove(emitter);
			logger.info("SseEmitter completed. Current subscribers for username " + username + ": " + userEmitters.size());
		});
		emitter.onTimeout(() -> {
			userEmitters.remove(emitter);
			logger.info("SseEmitter timed out. Current subscribers for username " + username + ": " + userEmitters.size());
		});
		logger.info("Client subscribed for notifications. Total subscribers for username " + username + ": " + userEmitters.size());
		return emitter;
	}

	public void sendNotification(String username, String message) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		List<SseEmitter> userEmitters = emitters.getOrDefault(username, new ArrayList<>());

		userEmitters.forEach(emitter -> {
			try {
				logger.info("Sending notification to username " + username + ": " + message);
				emitter.send(SseEmitter.event().name("notification").data(message));
			} catch (IOException e) {
				deadEmitters.add(emitter);
				logger.warning("Failed to send notification: " + e.getMessage());
			} catch (IllegalStateException e) {
				deadEmitters.add(emitter);
				logger.warning("Emitter is in illegal state: " + e.getMessage());
			}
		});
		userEmitters.removeAll(deadEmitters);
		logger.info("Total subscribers for username " + username + " after cleanup: " + userEmitters.size());
	}
}

@Data
class RespondFriendRequest {
	private Long id; // 새로운 필드 추가
	private Long requesterId;
	private Long recipientId;
	private String status;
	private String requesterUsername;
}
