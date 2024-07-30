package com.messenger_server.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {
	private Long id;
	private Long chatRoomId;
	private Long senderId;
	private String content;
	private LocalDateTime createdAt;  // 변경된 부분
	private String username;  // 추가
}
