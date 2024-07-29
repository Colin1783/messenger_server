package com.messenger_server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class MessageController {

	@GetMapping("/api/messages")
	public Mono<String> getMessages() {
		return Mono.just("List of messages");
	}
}