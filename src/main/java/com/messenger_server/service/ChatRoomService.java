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

	public ChatRoom createChatRoom() {
		ChatRoom chatRoom = new ChatRoom();
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

	public List<ChatRoom> findChatRoomsByUserId(Long userId) {
		List<ChatRoom> chatRooms = chatRoomMapper.findChatRoomsByUserId(userId);
		return chatRooms.stream().peek(chatRoom -> {
			Message latestMessage = messageMapper.findLatestMessageByChatRoomId(chatRoom.getId());
			chatRoom.setLatestMessage(latestMessage);
			// 현재 사용자를 제외한 다른 사용자 정보 설정
			User otherUser = chatRoomMapper.findOtherUserInChatRoom(chatRoom.getId(), userId);
			chatRoom.setOtherUser(otherUser);
		}).collect(Collectors.toList());
	}

	public ChatRoom startChatWithFriend(Long userId, Long friendId) {
		ChatRoom chatRoom = chatRoomMapper.findChatRoomByUserIds(userId, friendId);
		if (chatRoom == null) {
			chatRoom = createChatRoom();  // 채팅방 생성
			addUserToChatRoom(chatRoom.getId(), userId);
			addUserToChatRoom(chatRoom.getId(), friendId);
		}
		return chatRoom;
	}

	public void deleteChatRoom(Long chatRoomId) {
		// 채팅방에 속한 모든 메시지 삭제
		messageMapper.deleteMessagesByChatRoomId(chatRoomId);
		// 채팅방 삭제
		chatRoomMapper.deleteChatRoom(chatRoomId);
	}
}
