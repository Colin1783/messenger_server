package com.messenger_server.service;

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

	public void sendFriendRequest(Long requesterId, Long recipientId) {
		FriendRequest friendRequest = new FriendRequest();
		friendRequest.setRequesterId(requesterId);
		friendRequest.setRecipientId(recipientId);
		friendRequest.setStatus("PENDING");
		friendRequest.setCreatedAt(Timestamp.from(Instant.now()));
		friendRequestMapper.insert(friendRequest);

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
			friendshipMapper.insert(new Friendship(request.getRequesterId(), request.getRecipientId()));
		}
	}
}
