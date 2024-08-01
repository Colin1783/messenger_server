package com.messenger_server.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Post {
	private Long id;
	private String title;
	private String content;
	private Long userId;
	private Timestamp createdAt;
	private String username; // 작성자 이름
}
