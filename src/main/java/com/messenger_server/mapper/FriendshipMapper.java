package com.messenger_server.mapper;

import com.messenger_server.domain.Friendship;
import com.messenger_server.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendshipMapper {

	@Insert("INSERT INTO friendships (user_id, friend_id) VALUES (#{userId}, #{friendId})")
	void insert(Friendship friendship);

	@Select("SELECT * FROM friendships WHERE user_id = #{userId}")
	List<Friendship> findAllByUserId(Long userId);

	@Select("SELECT u.* FROM users u INNER JOIN friendships f ON u.id = f.friend_id WHERE f.user_id = #{userId}")
	List<User> findFriendsByUserId(Long userId);

	@Select("SELECT * FROM friendships WHERE user_id = #{userId} AND friend_id = #{friendId}")
	Friendship isFriend(@Param("userId") Long userId, @Param("friendId") Long friendId);

	@Delete("DELETE FROM friendships WHERE user_id = #{userId} AND friend_id = #{friendId}")
	void deleteFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId);
}
