package com.messenger_server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/chat")
public class ChatController {

	@Autowired
	private ReactiveRedisTemplate<String, String> redisTemplate;

	private static final String CHANNEL = "chat";

	@PostMapping("/message")
	public Mono<Void> sendMessage(@RequestBody String message) {
		return redisTemplate.convertAndSend(CHANNEL, message).then();
	}

	@GetMapping("/messages")
	public Flux<String> getMessages() {
		return redisTemplate.listenToChannel(CHANNEL)
						.map(message -> message.getMessage());
	}
}
