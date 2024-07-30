package com.messenger_server.mapper;

import com.messenger_server.domain.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {
	@Insert("INSERT INTO messages(chat_room_id, sender_id, content, timestamp) VALUES(#{chatRoomId}, #{senderId}, #{content}, #{timestamp})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Message message);

	@Select("SELECT * FROM messages WHERE chat_room_id = #{chatRoomId}")
	List<Message> findByChatRoomId(Long chatRoomId);

	@Select("SELECT * FROM messages WHERE chat_room_id = #{chatRoomId} ORDER BY timestamp DESC LIMIT 1")
	Message findLatestMessageByChatRoomId(Long chatRoomId);
}
