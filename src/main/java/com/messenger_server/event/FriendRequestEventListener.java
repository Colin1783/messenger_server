package com.messenger_server.event;

import com.messenger_server.controller.FriendRequestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestEventListener {

	@Autowired
	private FriendRequestController friendRequestController;

	@EventListener
	public void handleFriendRequestEvent(FriendRequestEvent event) {
		friendRequestController.sendNotification(event.getMessage());
	}
}
