package com.messenger_server.controller;

import lombok.Data;

@Data
public class MessageRequest {
	private Long chatRoomId;
	private Long senderId;
	private String content;
}
