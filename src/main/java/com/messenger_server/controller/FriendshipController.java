package com.messenger_server.controller;

import com.messenger_server.domain.User;
import com.messenger_server.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendshipController {

	@Autowired
	private FriendshipService friendshipService;

	@PostMapping
	public void addFriend(@RequestParam Long userId, @RequestParam Long friendId) {
		friendshipService.addFriend(userId, friendId);
	}

	@GetMapping("/{userId}")
	public List<User> getFriends(@PathVariable Long userId) {
		return friendshipService.getFriends(userId);
	}
}