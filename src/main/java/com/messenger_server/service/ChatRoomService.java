// ChatRoomService.java
package com.messenger_server.service;

import com.messenger_server.domain.ChatRoom;
import com.messenger_server.domain.Message;
import com.messenger_server.domain.User;
import com.messenger_server.mapper.ChatRoomMapper;
import com.messenger_server.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatRoomService {

	@Autowired
	private ChatRoomMapper chatRoomMapper;

	@Autowired
	private MessageMapper messageMapper;

	public ChatRoom createChatRoom(String name) {
		ChatRoom chatRoom = new ChatRoom();
		chatRoom.setName(name);
		chatRoomMapper.save(chatRoom);
		return chatRoom;
	}

	public void addUserToChatRoom(Long chatRoomId, Long userId) {
		chatRoomMapper.addUserToChatRoom(chatRoomId, userId);
	}

	public ChatRoom findById(Long chatRoomId) {
		return chatRoomMapper.findById(chatRoomId);
	}

	public List<ChatRoom> findAll() {
		return chatRoomMapper.findAll();
	}

	public List<User> getUsersInChatRoom(Long chatRoomId) {
		return chatRoomMapper.findUsersByChatRoomId(chatRoomId);
	}

	public ChatRoom findChatRoomByUserIds(Long userId, Long friendId) {
		return chatRoomMapper.findChatRoomByUserIds(userId, friendId);
	}

	public List<ChatRoom> findChatRoomsByUserId(Long userId) {
		List<ChatRoom> chatRooms = chatRoomMapper.findChatRoomsByUserId(userId);
		return chatRooms.stream().peek(chatRoom -> {
			Message latestMessage = messageMapper.findLatestMessageByChatRoomId(chatRoom.getId());
			chatRoom.setLatestMessage(latestMessage);
		}).collect(Collectors.toList());
	}
}
