package com.messenger_server.event;

import org.springframework.context.ApplicationEvent;

public class FriendRequestEvent extends ApplicationEvent {
	private final String message;

	public FriendRequestEvent(Object source, String message) {
		super(source);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
