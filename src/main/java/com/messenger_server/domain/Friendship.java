package com.messenger_server.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
public class Friendship {
	private Long id;
	private Long userId;
	private Long friendId;
	private Timestamp createdAt;

	public Friendship(Long userId, Long friendId) {
		this.userId = userId;
		this.friendId = friendId;
		this.createdAt = new Timestamp(System.currentTimeMillis());
	}
}
