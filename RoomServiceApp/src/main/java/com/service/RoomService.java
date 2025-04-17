package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.Dto.RoomDTO;
import com.entity.Room;
import com.entity.RoomId;
import com.entity.Status;
import com.exception.ResourceNotFoundException;
import com.exception.errordtls;
import com.feignClient.HostelClient;
import com.repository.RoomRepository;

@Service
public class RoomService {
	
	private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
	
	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	HostelClient hostelClient;

	public List<RoomDTO> getAllAvailableRooms(Integer roomId, String hostelName) throws Exception {

		List<Room> rooms = new ArrayList<>();
		List<RoomDTO> roomDtos = new ArrayList<>();
		try {

			if (hostelName != null && roomId != null) {
				rooms = roomRepository.findByRoomId_RoomIdAndRoomId_HostelNameAndStatus(roomId, hostelName, Status.AVAILABLE);
			} else if (hostelName != null) {
				rooms = roomRepository.findByRoomId_HostelNameAndStatus(hostelName, Status.AVAILABLE);
			}else {
		        throw new IllegalArgumentException("Hostel name must not be null.");
		    }
			if (!rooms.isEmpty()) {
				roomDtos = rooms.stream().map(room ->{
					RoomDTO roomDTO = new RoomDTO();
					roomDTO = modelMapper.map(room, RoomDTO.class);
				roomDTO.setHostelName(room.getRoomId().getHostelName());
				return roomDTO;
				}).collect(Collectors.toList());
			}
			return roomDtos;
		} catch (Exception ex) {
			throw new Exception("Rooms are not available for the particular Hostel" + hostelName);
		}

	}
	
	public String addAllRooms(List<RoomDTO> roomDTOs) {

		List<errordtls> err = new ArrayList<>();
		Map<String,Integer> map = new HashMap<>();
		List<Room> rooms = roomDTOs.stream().map(dto -> {

			map.putIfAbsent(dto.getHostelName(),roomRepository.findMaxRoomIdByHostel(dto.getHostelName()).orElse(0));
			map.put(dto.getHostelName(),map.get(dto.getHostelName())+1);
			if (dto.getRoomCapacity() <= 0 || map.get(dto.getHostelName()) > dto.getRoomCapacity() ) {
				err.add(new errordtls("ERR_CAPACITY", "Room capacity must be greater than 0 Or sufficient rooms has been filled", dto.getHostelName()));
				return null;
			}
			RoomId roomId = new RoomId(map.get(dto.getHostelName()), dto.getHostelName());
			Room room = modelMapper.map(dto, Room.class);
			room.setRoomId(roomId);
			return room;

		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!err.isEmpty()) {
			throw new ResourceNotFoundException(err);
		}
		roomRepository.saveAll(rooms);

		return "Rooms added successfully";

	}
	
	public String updateRooms(List<RoomDTO> roomDTOs) {
		
		List<Room> rooms = roomDTOs.stream().map(dto ->{
			  Room existingRoom = roomRepository.findRoomByRoomIdAndHostelName(dto.getRoomId(), dto.getHostelName())
					  .orElseThrow(() -> new IllegalStateException("rooms are not available for"+dto.getRoomId()));
			RoomId roomId = new RoomId(dto.getRoomId(), dto.getHostelName());
			existingRoom.setRoomId(roomId);
			existingRoom.setPrice(dto.getPrice());
			existingRoom.setRoomCapacity(dto.getRoomCapacity());
			existingRoom.setStatus(dto.getStatus()!= null ? Status.valueOf(dto.getStatus()) : Status.AVAILABLE);
			return existingRoom;
			
		}).collect(Collectors.toList());
		roomRepository.saveAll(rooms);
		
		return "Rooms updated Successfully";
		
	}
	
	public String deleteRoomByRoomId(Integer roomID, String hostelName) throws Exception {
		List<Room> rooms = new ArrayList<>();
		try {
			Boolean hostelFlag = hostelClient.hostelExisits(hostelName);
			logger.info("hostelFlag{}", hostelFlag);
			System.out.println(hostelFlag);
			if (hostelFlag) {
				if (roomID != null && hostelName != null) {
					Room room = roomRepository.findRoomByRoomIdAndHostelName(roomID, hostelName)
							.orElseThrow(() -> new IllegalStateException("rooms are not available for" + roomID));
					rooms.add(room);
				} else if (hostelName != null) {
					rooms = roomRepository.findByRoomId_HostelName(hostelName);
				} else {
					throw new IllegalStateException("roomId and hostelName cannot be null");
				}
				roomRepository.deleteAll(rooms);
			}
			return "Rooms are deleted successfully";
		} catch (Exception ex) {
			throw new Exception("HostelName is not exisits in hostel database" + hostelName);
		}

	}

}
