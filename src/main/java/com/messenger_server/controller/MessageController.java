package com.messenger_server.controller;

import com.messenger_server.domain.Message;
import com.messenger_server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {

	@Autowired
	private MessageService messageService;

	@PostMapping
	public Message sendMessage(@RequestBody MessageRequest messageRequest) {
		return messageService.saveMessage(messageRequest.getChatRoomId(), messageRequest.getSenderId(), messageRequest.getContent());
	}

	@GetMapping("/chatroom/{chatRoomId}")
	public List<Message> getMessagesByChatRoom(@PathVariable Long chatRoomId) {
		return messageService.findMessagesByChatRoomId(chatRoomId);
	}
}
