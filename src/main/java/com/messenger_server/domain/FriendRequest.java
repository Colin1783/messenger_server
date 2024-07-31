package com.messenger_server.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class FriendRequest {
	private Long id;
	private Long requesterId;
	private Long recipientId;
	private String status; // "PENDING", "ACCEPTED", "DECLINED"
	private Timestamp createdAt;
	private String requesterUsername; // Add this field
}
