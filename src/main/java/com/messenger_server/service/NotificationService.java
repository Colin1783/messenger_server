package com.messenger_server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

	@Autowired
	private ReactiveRedisTemplate<String, Object> redisTemplate;

	public void sendNotification(String topic, String message) {
		redisTemplate.convertAndSend(topic, message).subscribe();
	}
}
