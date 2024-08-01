package com.messenger_server.mapper;

import com.messenger_server.domain.FriendRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendRequestMapper {

	@Insert("INSERT INTO friend_requests (requester_id, recipient_id, status) VALUES (#{requesterId}, #{recipientId}, #{status})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(FriendRequest friendRequest);

	@Select("""
            SELECT fr.*, u.username as requesterUsername 
            FROM friend_requests fr 
            JOIN users u ON fr.requester_id = u.id 
            WHERE fr.recipient_id = #{recipientId} AND fr.status = 'PENDING'
            """)
	@Results({
					@Result(property = "id", column = "id"),
					@Result(property = "requesterId", column = "requester_id"),
					@Result(property = "recipientId", column = "recipient_id"),
					@Result(property = "status", column = "status"),
					@Result(property = "createdAt", column = "created_at"),
					@Result(property = "requesterUsername", column = "requesterUsername")
	})
	List<FriendRequest> findPendingRequests(Long recipientId);

	@Update("UPDATE friend_requests SET status = #{status} WHERE id = #{id}")
	void updateStatus(@Param("id") Long id, @Param("status") String status);

	@Select("SELECT * FROM friend_requests WHERE id = #{id}")
	@Results({
					@Result(property = "id", column = "id"),
					@Result(property = "requesterId", column = "requester_id"),
					@Result(property = "recipientId", column = "recipient_id"),
					@Result(property = "status", column = "status"),
					@Result(property = "createdAt", column = "created_at")
	})
	FriendRequest findById(Long id);

	@Delete("DELETE FROM friend_requests WHERE id = #{id}")
	void delete(Long id);
}
