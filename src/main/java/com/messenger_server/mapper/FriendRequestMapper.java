package com.messenger_server.mapper;

import com.messenger_server.domain.FriendRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FriendRequestMapper {

	@Insert("INSERT INTO friend_requests (requester_id, recipient_id, status) VALUES (#{requesterId}, #{recipientId}, #{status})")
	@Options(useGeneratedKeys = true, keyProperty = "id")
	void insert(FriendRequest friendRequest);

	@Select("SELECT * FROM friend_requests WHERE recipient_id = #{recipientId} AND status = 'PENDING'")
	List<FriendRequest> findPendingRequests(Long recipientId);

	@Update("UPDATE friend_requests SET status = #{status} WHERE id = #{id}")
	void updateStatus(@Param("id") Long id, @Param("status") String status);

	@Select("SELECT * FROM friend_requests WHERE id = #{id}")
	FriendRequest findById(Long id);
}
