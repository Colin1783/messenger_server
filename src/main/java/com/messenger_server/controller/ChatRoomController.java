// ChatRoomController.java
package com.messenger_server.controller;

import com.messenger_server.domain.ChatRoom;
import com.messenger_server.domain.User;
import com.messenger_server.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chatrooms")
public class ChatRoomController {

	@Autowired
	private ChatRoomService chatRoomService;

	@PostMapping
	public ChatRoom createChatRoom(@RequestBody String name) {
		return chatRoomService.createChatRoom(name);
	}

	@PostMapping("/{chatRoomId}/users/{userId}")
	public void addUserToChatRoom(@PathVariable Long chatRoomId, @PathVariable Long userId) {
		chatRoomService.addUserToChatRoom(chatRoomId, userId);
	}

	@GetMapping("/{chatRoomId}")
	public ChatRoom getChatRoom(@PathVariable Long chatRoomId) {
		return chatRoomService.findById(chatRoomId);
	}

	@GetMapping
	public List<ChatRoom> getChatRooms() {
		return chatRoomService.findAll();
	}

	@GetMapping("/{chatRoomId}/users")
	public List<User> getUsersInChatRoom(@PathVariable Long chatRoomId) {
		return chatRoomService.getUsersInChatRoom(chatRoomId);
	}

	@PostMapping("/start-chat")
	public ChatRoom startChatWithFriend(@RequestParam Long userId, @RequestParam Long friendId) {
		return chatRoomService.startChatWithFriend(userId, friendId);
	}

	@GetMapping("/user/{userId}")
	public List<ChatRoom> getChatRoomsByUserId(@PathVariable Long userId) {
		return chatRoomService.findChatRoomsByUserId(userId);
	}
}