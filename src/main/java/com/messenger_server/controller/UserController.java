package com.messenger_server.controller;

import com.messenger_server.domain.UpdateUserRequest;
import com.messenger_server.domain.User;
import com.messenger_server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@GetMapping("/search")
	public List<User> searchUsers(@RequestParam String query) {
		return userService.searchUsers(query);
	}

	@PutMapping("/update")
	public void updateUser(@RequestBody UpdateUserRequest request) {
		userService.updateUser(request);
	}
}

