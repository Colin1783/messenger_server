package com.messenger_server.controller;

import com.messenger_server.domain.FriendRequest;
import com.messenger_server.service.FriendRequestService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

	@Autowired
	private FriendRequestService friendRequestService;

	@PostMapping("/send")
	public void sendFriendRequest(@RequestParam Long requesterId, @RequestParam Long recipientId) {
		friendRequestService.sendFriendRequest(requesterId, recipientId);
	}

	@GetMapping("/pending/{recipientId}")
	public List<FriendRequest> getPendingRequests(@PathVariable Long recipientId) {
		return friendRequestService.getPendingRequests(recipientId);
	}

	@PostMapping("/respond")
	public void respondToFriendRequest(@RequestBody RespondFriendRequest request) {
		friendRequestService.respondToFriendRequest(request.getRequestId(), request.getStatus());
	}
}

@Data
class RespondFriendRequest {
	private Long requestId;
	private String status;
}
