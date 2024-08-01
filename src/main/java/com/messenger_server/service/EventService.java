package com.messenger_server.service;

import com.messenger_server.domain.Event;
import com.messenger_server.mapper.EventMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

	@Autowired
	private EventMapper eventMapper;

	public List<Event> getAllEvents() {
		return eventMapper.findAll();
	}

	public Event getEventById(Long id) {
		return eventMapper.findById(id);
	}

	public void createEvent(Event event) {
		eventMapper.save(event);
	}

	public void updateEvent(Event event) {
		eventMapper.update(event);
	}

	public void deleteEvent(Long id) {
		eventMapper.delete(id);
	}
}
