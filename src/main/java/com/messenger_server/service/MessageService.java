package com.messenger_server.service;

import com.messenger_server.domain.Message;
import com.messenger_server.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class MessageService {

	@Autowired
	private MessageMapper messageMapper;

	public Message saveMessage(Long chatRoomId, Long senderId, String content) {
		Message message = new Message();
		message.setChatRoomId(chatRoomId);
		message.setSenderId(senderId);
		message.setContent(content);
		message.setCreatedAt(Timestamp.from(Instant.now()));
		messageMapper.save(message);
		return message;
	}

	public List<Message> findMessagesByChatRoomId(Long chatRoomId) {
		return messageMapper.findByChatRoomId(chatRoomId);
	}
}
