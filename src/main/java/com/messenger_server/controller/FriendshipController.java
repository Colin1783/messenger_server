package com.messenger_server.controller;

import com.messenger_server.domain.User;
import com.messenger_server.service.FriendRequestService;
import com.messenger_server.service.FriendshipService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

	@Autowired
	private FriendshipService friendshipService;

	@Autowired
	private FriendRequestService friendRequestService;

	@PostMapping
	public void addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
		friendshipService.addFriend(userId, friendId);
	}

	@PostMapping("/remove")
	public void removeFriend(@RequestBody RemoveFriendRequest request) {
		friendRequestService.removeFriend(request.getUserId(), request.getFriendId());
	}

	@GetMapping("/{userId}")
	public List<User> getFriends(@PathVariable Long userId) {
		return friendshipService.getFriends(userId);
	}


	@Data
	public static class RemoveFriendRequest {
		private Long userId;
		private Long friendId;
	}
}