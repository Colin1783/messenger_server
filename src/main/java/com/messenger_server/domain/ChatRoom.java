package com.messenger_server.domain;

import lombok.Data;

import java.util.List;

@Data
public class ChatRoom {
	private Long id;
	private String name;
	private List<User> users;
	private Message latestMessage;
}
