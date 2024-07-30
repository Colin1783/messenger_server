package com.messenger_server.service;

import com.messenger_server.domain.Friendship;
import com.messenger_server.domain.User;
import com.messenger_server.mapper.FriendshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendshipService {

	@Autowired
	private FriendshipMapper friendshipMapper;

	public void addFriend(Long userId, Long friendId) {
		Friendship friendship = new Friendship();
		friendship.setUserId(userId);
		friendship.setFriendId(friendId);
		friendshipMapper.insert(friendship);
	}

	public List<User> getFriends(Long userId) {
		return friendshipMapper.findFriendsByUserId(userId);
	}
}