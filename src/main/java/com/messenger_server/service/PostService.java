package com.messenger_server.service;

import com.messenger_server.domain.Post;
import com.messenger_server.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class PostService {

	@Autowired
	private PostMapper postMapper;

	public List<Post> getAllPosts() {
		return postMapper.findAll();
	}

	public Post getPostById(Long id) {
		return postMapper.findById(id);
	}

	public void createPost(Post post) {
		post.setCreatedAt(new Timestamp(System.currentTimeMillis()));
		postMapper.save(post);
	}

	public void updatePost(Post post) {
		postMapper.update(post);
	}

	public void deletePost(Long id) {
		postMapper.delete(id);
	}
}
