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

		// Get the username for the requester
		String requesterUsername = friendRequestMapper.findPendingRequests(recipientId).stream()
						.filter(req -> req.getRequesterId().equals(requesterId))
						.findFirst()
						.map(FriendRequest::getRequesterUsername)
						.orElse("Unknown");

		logger.info("Friend request sent from user " + requesterId + " to user " + recipientId);

		String notificationMessage = createJsonMessage(Map.of(
						"type", "friendRequest",
						"requesterId", String.valueOf(requesterId),
						"recipientId", String.valueOf(recipientId),
						"status", "PENDING",
						"requesterUsername", requesterUsername
		));
		logger.info("Publishing friend request event: " + notificationMessage);
		eventPublisher.publishEvent(new FriendRequestEvent(this, notificationMessage, recipientId));
	}

	public List<FriendRequest> getPendingRequests(Long recipientId) {
		return friendRequestMapper.findPendingRequests(recipientId);
	}

	public void respondToFriendRequest(Long requesterId, Long recipientId, String status, String requesterUsername) {
		logger.info("Attempting to respond to friend request from user " + requesterId + " to user " + recipientId + " with status " + status);

		List<FriendRequest> pendingRequests = friendRequestMapper.findPendingRequests(recipientId);
		logger.info("Pending requests for user " + recipientId + ": " + pendingRequests);

		FriendRequest request = pendingRequests.stream()
						.filter(req -> req.getRequesterId().equals(requesterId))
						.findFirst()
						.orElse(null);

		if (request == null) {
			logger.warning("No pending friend request found for requester " + requesterId + " and recipient " + recipientId);
			throw new IllegalStateException("No pending friend request found.");
		}

		logger.info("Updating status for friend request " + request.getId() + " to " + status);
		friendRequestMapper.updateStatus(request.getId(), status);

		if ("ACCEPTED".equals(status)) {
			if (request.getRequesterId() == null || request.getRecipientId() == null) {
				logger.warning("Invalid friend request data: " + request);
				throw new IllegalStateException("Invalid friend request data: " + request);
			}
			friendshipMapper.insert(new Friendship(request.getRequesterId(), request.getRecipientId()));
			friendshipMapper.insert(new Friendship(request.getRecipientId(), request.getRequesterId()));

			logger.info("Friend request accepted between user " + request.getRequesterId() + " and user " + request.getRecipientId());

			String notificationMessage = createJsonMessage(Map.of(
							"type", "friendRequestAccepted",
							"requesterId", String.valueOf(request.getRequesterId()),
							"recipientId", String.valueOf(request.getRecipientId()),
							"requesterUsername", requesterUsername
			));
			logger.info("Publishing friend request accepted event: " + notificationMessage);
			eventPublisher.publishEvent(new FriendRequestEvent(this, notificationMessage, request.getRecipientId()));
		}

		logger.info("Deleting friend request " + request.getId());
		friendRequestMapper.delete(request.getId());
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
