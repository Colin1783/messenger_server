package com.messenger_server.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Message {
	private Long id;
	private Long chatRoomId;
	private Long senderId;
	private String content;
	private Timestamp timestamp;
}
