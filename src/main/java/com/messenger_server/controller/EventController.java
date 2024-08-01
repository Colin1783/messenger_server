package com.messenger_server.controller;

import com.messenger_server.domain.Event;
import com.messenger_server.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

	@Autowired
	private EventService eventService;

	@GetMapping
	public List<Event> getAllEvents() {
		return eventService.getAllEvents();
	}

	@GetMapping("/{id}")
	public Event getEventById(@PathVariable Long id) {
		return eventService.getEventById(id);
	}

	@PostMapping
	public void createEvent(@RequestBody Event event) {
		eventService.createEvent(event);
	}

	@PutMapping("/{id}")
	public void updateEvent(@PathVariable Long id, @RequestBody Event event) {
		event.setId(id);
		eventService.updateEvent(event);
	}

	@DeleteMapping("/{id}")
	public void deleteEvent(@PathVariable Long id) {
		eventService.deleteEvent(id);
	}
}
