package com.messenger_server.mapper;

import com.messenger_server.domain.User;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.util.List;

@Mapper
public interface UserMapper {
	@Select("SELECT * FROM users WHERE username = #{username}")
	User findByUsername(String username);

	@Insert("INSERT INTO users(username, password, email, name, birthdate, cellphone, created_at, role, login_status, last_logged_in) VALUES(#{username}, #{password}, #{email}, #{name}, #{birthdate}, #{cellphone}, #{createdAt}, #{role}, #{loginStatus}, #{lastLoggedIn})")
	void save(User user);

	@Update("UPDATE users SET login_status = #{loginStatus}, last_logged_in = #{lastLoggedIn} WHERE username = #{username}")
	void updateLoginStatusAndLastLoggedIn(String username, boolean loginStatus, Timestamp lastLoggedIn);

	@Select("""
					SELECT * FROM users 
					         WHERE username 
					                   LIKE CONCAT('%', #{query}, '%') OR name LIKE CONCAT('%', #{query}, '%')
					""")
	List<User> searchUsers(String query);

	@Select("SELECT username FROM users WHERE id = #{id}")
	String findUsernameById(Long id);

	@Select("SELECT * FROM users WHERE id = #{id}")
	User findById(Long id);

	@Update("UPDATE users SET email = #{email}, name = #{name}, birthdate = #{birthdate}, cellphone = #{cellphone}, password = #{password} WHERE id = #{id}")
	void update(User user);
}
