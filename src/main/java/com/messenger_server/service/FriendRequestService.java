package com.messenger_server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.messenger_server.domain.FriendRequest;
import com.messenger_server.domain.Friendship;
import com.messenger_server.event.FriendRequestEvent;
import com.messenger_server.mapper.FriendRequestMapper;
import com.messenger_server.mapper.FriendshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
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
	private ApplicationEventPublisher eventPublisher;

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

		String notificationMessage = createJsonMessage(Map.of(
						"type", "friendRequest",
						"requesterId", String.valueOf(requesterId),
						"recipientId", String.valueOf(recipientId),
						"status", "PENDING"
		));
		logger.info("Publishing friend request event: " + notificationMessage);
		eventPublisher.publishEvent(new FriendRequestEvent(this, notificationMessage));
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
			friendshipMapper.insert(new Friendship(request.getRecipientId(), request.getRequesterId()));

			logger.info("Friend request accepted between user " + request.getRequesterId() + " and user " + request.getRecipientId());

			String notificationMessage = createJsonMessage(Map.of(
							"type", "friendRequestAccepted",
							"requesterId", String.valueOf(request.getRequesterId()),
							"recipientId", String.valueOf(request.getRecipientId())
			));
			logger.info("Publishing friend request accepted event: " + notificationMessage);
			eventPublisher.publishEvent(new FriendRequestEvent(this, notificationMessage));
		}
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
