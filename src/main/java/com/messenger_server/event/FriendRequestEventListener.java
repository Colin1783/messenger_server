package com.messenger_server.event;

import com.messenger_server.controller.FriendRequestController;
import com.messenger_server.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FriendRequestEventListener {

	@Autowired
	private FriendRequestController friendRequestController;

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@EventListener
	public void handleFriendRequestEvent(FriendRequestEvent event) {
		Long recipientId = event.getRecipientId();
		String username = customUserDetailsService.loadUserByUserId(recipientId).getUsername();
		friendRequestController.sendNotification(username, event.getMessage());
	}
}
