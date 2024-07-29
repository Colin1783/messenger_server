package com.messenger_server.mapper;

import com.messenger_server.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.sql.Timestamp;


@Mapper
public interface UserMapper {
	@Select("SELECT * FROM users WHERE username = #{username}")
	User findByUsername(String username);

	@Insert("INSERT INTO users(username, password, email, name, birthdate, cellphone, created_at, role, login_status, last_logged_in) VALUES(#{username}, #{password}, #{email}, #{name}, #{birthdate}, #{cellphone}, #{createdAt}, #{role}, #{loginStatus}, #{lastLoggedIn})")
	void save(User user);

	@Update("UPDATE users SET login_status = #{loginStatus}, last_logged_in = #{lastLoggedIn} WHERE username = #{username}")
	void updateLoginStatusAndLastLoggedIn(String username, boolean loginStatus, Timestamp lastLoggedIn);
}
