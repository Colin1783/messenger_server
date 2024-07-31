package com.messenger_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messenger_server.domain.FriendRequest;
import com.messenger_server.domain.Friendship;
import com.messenger_server.mapper.FriendRequestMapper;
import com.messenger_server.mapper.FriendshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class FriendRequestService {

	@Autowired
	private FriendRequestMapper friendRequestMapper;

	@Autowired
	private FriendshipMapper friendshipMapper;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final Logger logger = Logger.getLogger(FriendRequestService.class.getName());

	public void sendFriendRequest(Long requesterId, Long recipientId) {
		FriendRequest friendRequest = new FriendRequest();
		friendRequest.setRequesterId(requesterId);
		friendRequest.setRecipientId(recipientId);
		friendRequest.setStatus("PENDING");
		friendRequest.setCreatedAt(Timestamp.from(Instant.now()));
		friendRequestMapper.insert(friendRequest);

		logger.info("Friend request sent from user " + requesterId + " to user " + recipientId);

		// Redis에 메시지 발행
		notificationService.sendNotification("friendRequests", "Friend request from user " + requesterId + " to user " + recipientId);

		// WebSocket을 통해 알림 전송
		messagingTemplate.convertAndSend("/topic/friendRequests", "Friend request from user " + requesterId + " to user " + recipientId);
	}

	public List<FriendRequest> getPendingRequests(Long recipientId) {
		return friendRequestMapper.findPendingRequests(recipientId);
	}

	public void respondToFriendRequest(Long requestId, String status) {
		friendRequestMapper.updateStatus(requestId, status);
		if ("ACCEPTED".equals(status)) {
			FriendRequest request = friendRequestMapper.findById(requestId);
			if (request.getRequesterId() == null || request.getRecipientId() == null) {
				throw new IllegalStateException("Invalid friend request data: " + request);
			}
			friendshipMapper.insert(new Friendship(request.getRequesterId(), request.getRecipientId()));
			friendshipMapper.insert(new Friendship(request.getRecipientId(), request.getRequesterId())); // 친구 수락한 쪽도 친구 목록에 추가

			logger.info("Friend request accepted between user " + request.getRequesterId() + " and user " + request.getRecipientId());

			// 친구 목록 갱신 알림 전송
			String notificationMessage = createJsonMessage(Map.of(
							"type", "friendRequestAccepted",
							"requesterId", String.valueOf(request.getRequesterId()),
							"recipientId", String.valueOf(request.getRecipientId())
			));
			logger.info("Sending friend request accepted notification: " + notificationMessage);
			messagingTemplate.convertAndSend("/topic/friendRequests/" + request.getRequesterId(), notificationMessage);
			messagingTemplate.convertAndSend("/topic/friendRequests/" + request.getRecipientId(), notificationMessage);

		}
		// 친구 요청 삭제
		friendRequestMapper.delete(requestId);
	}

	private String createJsonMessage(Map<String, String> keyValues) {
		try {
			return objectMapper.writeValueAsString(keyValues);
		} catch (Exception e) {
			throw new RuntimeException("Error creating JSON message", e);
		}
	}

	public void removeFriend(Long userId, Long friendId) {
		friendshipMapper.deleteFriendship(userId, friendId);
		logger.info("Friendship removed between user " + userId + " and user " + friendId);
	}
}
