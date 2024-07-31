// ChatRoomMapper.java
package com.messenger_server.mapper;

import com.messenger_server.domain.ChatRoom;
import com.messenger_server.domain.Message;
import com.messenger_server.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatRoomMapper {
	@Insert("INSERT INTO chat_rooms(name) VALUES(#{name})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(ChatRoom chatRoom);

	@Select("SELECT * FROM chat_rooms WHERE id = #{id}")
	@Results({
					@Result(property = "id", column = "id"),
					@Result(property = "name", column = "name"),
					@Result(property = "users", column = "id", many = @Many(select = "findUsersByChatRoomId")),
					@Result(property = "latestMessage", column = "id", one = @One(select = "findLatestMessageByChatRoomId"))
	})
	ChatRoom findById(Long id);

	@Select("SELECT * FROM chat_rooms")
	@Results({
					@Result(property = "id", column = "id"),
					@Result(property = "name", column = "name"),
					@Result(property = "users", column = "id", many = @Many(select = "findUsersByChatRoomId")),
					@Result(property = "latestMessage", column = "id", one = @One(select = "findLatestMessageByChatRoomId"))
	})
	List<ChatRoom> findAll();

	@Insert("INSERT INTO chat_room_users(chat_room_id, user_id) VALUES(#{chatRoomId}, #{userId})")
	void addUserToChatRoom(@Param("chatRoomId") Long chatRoomId, @Param("userId") Long userId);

	@Select("SELECT u.* FROM users u INNER JOIN chat_room_users cru ON u.id = cru.user_id WHERE cru.chat_room_id = #{chatRoomId}")
	List<User> findUsersByChatRoomId(Long chatRoomId);

	@Select("SELECT cr.* FROM chat_rooms cr " +
					"INNER JOIN chat_room_users cru1 ON cr.id = cru1.chat_room_id " +
					"INNER JOIN chat_room_users cru2 ON cr.id = cru2.chat_room_id " +
					"WHERE cru1.user_id = #{userId} AND cru2.user_id = #{friendId}")
	ChatRoom findChatRoomByUserIds(@Param("userId") Long userId, @Param("friendId") Long friendId);

	@Select("SELECT * FROM messages WHERE chat_room_id = #{chatRoomId} ORDER BY created_at DESC LIMIT 1")
	Message findLatestMessageByChatRoomId(Long chatRoomId);

	@Select("SELECT cr.* FROM chat_rooms cr " +
					"JOIN chat_room_users cru ON cr.id = cru.chat_room_id " +
					"WHERE cru.user_id = #{userId}")
	List<ChatRoom> findChatRoomsByUserId(Long userId);

	@Select("SELECT username FROM users WHERE id = #{userId}")
	String findUsernameById(@Param("userId") Long userId);  // 사용자 이름을 가져오는 메서드 추가
}
