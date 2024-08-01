package com.messenger_server.event;

import org.springframework.context.ApplicationEvent;

public class FriendRequestEvent extends ApplicationEvent {
	private final String message;
	private final Long recipientId;

	public FriendRequestEvent(Object source, String message, Long recipientId) {
		super(source);
		this.message = message;
		this.recipientId = recipientId;
	}

	public String getMessage() {
		return message;
	}

	public Long getRecipientId() {
		return recipientId;
	}
}
