package com.messenger_server.domain;

import lombok.Data;

@Data
public class ChatRoom {
	private Long id;
	private String createdAt;
	private User otherUser;
	private Message latestMessage;
}
