package com.messenger_server.domain;

import lombok.Data;
import java.sql.Timestamp;

@Data
public class Event {
	private Long id;
	private String title;
	private String description;
	private Timestamp start;
	private Timestamp end;
	private Long userId;
	private Timestamp createdAt;
}
