package com.messenger_server.mapper;

import com.messenger_server.domain.Event;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EventMapper {

	@Select("SELECT * FROM events WHERE id = #{id}")
	Event findById(Long id);

	@Select("SELECT * FROM events")
	List<Event> findAll();

	@Insert("""
        INSERT INTO events(title, description, start, end, user_id) 
        VALUES(#{title}, #{description}, #{start}, #{end}, #{userId})
    """)
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void save(Event event);

	@Delete("DELETE FROM events WHERE id = #{id}")
	void delete(Long id);

	@Update("""
        UPDATE events 
        SET title = #{title}, description = #{description}, start = #{start}, end = #{end} 
        WHERE id = #{id}
    """)
	void update(Event event);
}
