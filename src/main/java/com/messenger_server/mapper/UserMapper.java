package com.messenger_server.mapper;

import com.messenger_server.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
	@Select("SELECT * FROM users WHERE username = #{username}")
	User findByUsername(String username);

	@Insert("INSERT INTO users(username, password, email, name, birthdate, cellphone, created_at, role) VALUES(#{username}, #{password}, #{email}, #{name}, #{birthdate}, #{cellphone}, #{createdAt}, #{role})")
	void save(User user);
}
