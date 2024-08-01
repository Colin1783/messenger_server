package com.messenger_server.controller;

import com.messenger_server.domain.FriendRequest;
import com.messenger_server.service.FriendRequestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	private final List<SseEmitter> emitters = new ArrayList<>();
	private static final Logger logger = Logger.getLogger(FriendRequestController.class.getName());

	@PostMapping("/send")
	public void sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long recipientId) {
		friendRequestService.sendFriendRequest(requesterId, recipientId);
	}

	@GetMapping("/pending/{recipientId}")
	public List<FriendRequest> getPendingRequests(@PathVariable Long recipientId) {
		return friendRequestService.getPendingRequests(recipientId);
	}

	@PostMapping("/respond")
	public void respondToFriendRequest(@RequestBody RespondFriendRequest request) {
		friendRequestService.respondToFriendRequest(request.getRequestId(), request.getStatus());
	}

	@GetMapping(value = "/notifications", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter subscribeNotifications(@RequestParam String token) {
		logger.info("New SSE connection with token: " + token);
		SseEmitter emitter = new SseEmitter(30 * 60 * 1000L); // 30분 타임아웃
		emitters.add(emitter);
		emitter.onCompletion(() -> {
			emitters.remove(emitter);
			logger.info("SseEmitter completed. Current subscribers: " + emitters.size());
		});
		emitter.onTimeout(() -> {
			emitters.remove(emitter);
			logger.info("SseEmitter timed out. Current subscribers: " + emitters.size());
		});
		logger.info("Client subscribed for notifications. Total subscribers: " + emitters.size());
		return emitter;
	}

	public void sendNotification(String message) {
		List<SseEmitter> deadEmitters = new ArrayList<>();
		emitters.forEach(emitter -> {
			try {
				logger.info("Sending notification to client: " + message);
				emitter.send(SseEmitter.event().name("notification").data(message));
			} catch (IOException e) {
				deadEmitters.add(emitter);
				logger.warning("Failed to send notification: " + e.getMessage());
			} catch (IllegalStateException e) {
				deadEmitters.add(emitter);
				logger.warning("Emitter is in illegal state: " + e.getMessage());
			}
		});
		emitters.removeAll(deadEmitters);
		logger.info("Total subscribers after cleanup: " + emitters.size());
	}
}

@Data
class RespondFriendRequest {
	private Long requestId;
	private String status;
}
