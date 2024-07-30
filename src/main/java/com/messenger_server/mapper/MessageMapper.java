package com.messenger_server.mapper;

import com.messenger_server.domain.Message;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface MessageMapper {
	@Insert("INSERT INTO messages(chat_room_id, sender_id, content, created_at) VALUES(#{chatRoomId}, #{senderId}, #{content}, #{createdAt})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Message message);

	@Select("SELECT m.*, u.username as username FROM messages m JOIN users u ON m.sender_id = u.id WHERE m.chat_room_id = #{chatRoomId} ORDER BY m.created_at ASC")
	@Results({
					@Result(property = "username", column = "username"),
					@Result(property = "chatRoomId", column = "chat_room_id"),
					@Result(property = "createdAt", column = "created_at", javaType = LocalDateTime.class),
					@Result(property = "senderId", column = "sender_id"),
					@Result(property = "createdAt", column = "created_at")
	})
	List<Message> findByChatRoomId(Long chatRoomId);

	@Select("SELECT * FROM messages WHERE chat_room_id = #{chatRoomId} ORDER BY created_at DESC LIMIT 1")
	Message findLatestMessageByChatRoomId(Long chatRoomId);
}
