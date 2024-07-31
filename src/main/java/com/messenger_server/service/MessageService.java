package com.messenger_server.service;

import com.messenger_server.domain.Message;
import com.messenger_server.mapper.MessageMapper;
import com.messenger_server.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

	@Autowired
	private MessageMapper messageMapper;

	@Autowired
	private UserMapper userMapper;  // UserMapper 추가


	public Message saveMessage(Long chatRoomId, Long senderId, String content) {
		Message message = new Message();
		message.setChatRoomId(chatRoomId);
		message.setSenderId(senderId);
		message.setContent(content);
		message.setCreatedAt(LocalDateTime.now());
		messageMapper.save(message);

		// senderId를 통해 username을 조회하여 설정
		String username = userMapper.findUsernameById(senderId);
		message.setUsername(username);

		return message;
	}

	public List<Message> findMessagesByChatRoomId(Long chatRoomId) {
		return messageMapper.findByChatRoomId(chatRoomId);
	}
}
