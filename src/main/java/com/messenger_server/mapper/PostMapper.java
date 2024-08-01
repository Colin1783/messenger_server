package com.messenger_server.mapper;

import com.messenger_server.domain.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {

	@Select("""
        SELECT p.id, p.title, p.content, p.user_id AS userId, p.created_at AS createdAt, u.username 
        FROM posts p 
        JOIN users u ON p.user_id = u.id 
        WHERE p.id = #{id}
    """)
	Post findById(Long id);

	@Select("""
        SELECT p.id, p.title, p.content, p.user_id AS userId, p.created_at AS createdAt, u.username 
        FROM posts p 
        JOIN users u ON p.user_id = u.id
    """)
	List<Post> findAll();

	@Insert("""
        INSERT INTO posts(title, content, user_id, created_at) 
        VALUES(#{title}, #{content}, #{userId}, #{createdAt})
    """)
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Post post);

	@Delete("DELETE FROM posts WHERE id = #{id}")
	void delete(Long id);

	@Update("UPDATE posts SET title = #{title}, content = #{content} WHERE id = #{id}")
	void update(Post post);
}
