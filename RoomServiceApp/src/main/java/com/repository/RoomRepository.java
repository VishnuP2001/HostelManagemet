package com.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.entity.Room;
import com.entity.RoomId;
import com.entity.Status;

public interface RoomRepository extends JpaRepository<Room, RoomId> {

	List<Room> findByRoomId_RoomIdAndRoomId_HostelNameAndStatus(Integer roomId, String hostelName,Status status);
	
	List<Room> findByRoomId_HostelNameAndStatus(String hostelName, Status status);
	
	@Query("SELECT MAX(r.roomId.roomId) FROM Room r WHERE r.roomId.hostelName = :hostelName")
	Optional<Integer> findMaxRoomIdByHostel(@Param("hostelName") String hostelName);

	boolean existsByRoomId_HostelName(String hostelName);
	
	@Query("SELECT r FROM Room r WHERE r.roomId.roomId = :roomId AND r.roomId.hostelName = :hostelName")
    Optional<Room> findRoomByRoomIdAndHostelName(@Param("roomId") int roomId, @Param("hostelName") String hostelName);
	
	List<Room> findByRoomId_HostelName(String hostelName);

}
