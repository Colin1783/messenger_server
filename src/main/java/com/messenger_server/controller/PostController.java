package com.messenger_server.controller;

import com.messenger_server.domain.Post;
import com.messenger_server.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
public class PostController {

	@Autowired
	private PostService postService;

	@GetMapping
	public List<Post> getAllPosts() {
		return postService.getAllPosts();
	}

	@GetMapping("/{id}")
	public Post getPostById(@PathVariable Long id) {
		return postService.getPostById(id);
	}

	@PostMapping
	public void createPost(@RequestBody Post post) {
		postService.createPost(post);
	}

	@PutMapping("/{id}")
	public void updatePost(@PathVariable Long id, @RequestBody Post post) {
		post.setId(id);
		postService.updatePost(post);
	}

	@DeleteMapping("/{id}")
	public void deletePost(@PathVariable Long id) {
		postService.deletePost(id);
	}
}
