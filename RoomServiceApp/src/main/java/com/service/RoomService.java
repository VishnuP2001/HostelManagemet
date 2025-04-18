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

import jakarta.transaction.Transactional;

@Service
public class RoomService {
	
	private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
	
	@Autowired
	RoomRepository roomRepository;
	
	@Autowired
	ModelMapper modelMapper;
	
	@Autowired
	HostelClient hostelClient;

	public List<RoomDTO> getAllAvailableRooms(Integer roomId, Long hostelId) throws Exception {

		List<Room> rooms = new ArrayList<>();
		List<RoomDTO> roomDtos = new ArrayList<>();
		try {

			if (hostelId != null && roomId != null) {
				logger.info("hostelId{} roomId{}", hostelId, roomId);
				Room r  = roomRepository.findRoomByRoomIdAndHostelId(roomId, hostelId).orElse(null);
				logger.info("room is{}", r);
				rooms.add(r);
			} else if (hostelId != null) {
				logger.info("hostelId is {}", hostelId);
				rooms = roomRepository.findByRoomId_HostelIdAndStatus(hostelId, Status.AVAILABLE);
				logger.info("room for{}", rooms);
			}else {
		        throw new IllegalArgumentException("Hostel Id cannot not be null.");
		    }
			logger.info("rooms are{}",rooms);
			if(rooms.isEmpty()) {
				throw new Exception("rooms are empty for given hostelId"+hostelId);
			}
//			if (!rooms.isEmpty()) {
//				logger.info("entering rooms{}",rooms);
//				 roomDtos = rooms.stream().map(room -> {
//		                RoomDTO roomDTO = new RoomDTO();
//		                roomDTO.setRoomId(room.getRoomId().getRoomId());
//		                roomDTO.setHostelId(room.getRoomId().getHostelId());
//		                roomDTO.setPrice(room.getPrice());
//		                roomDTO.setRoomCapacity(room.getRoomCapacity());
//		                roomDTO.setStatus(room.getStatus().toString());
//		                return roomDTO;
//		            }).collect(Collectors.toList());
			
			if(!rooms.isEmpty()) {
				roomDtos = rooms.stream().map(room -> {
					RoomDTO roomDTO = new RoomDTO();
					roomDTO = modelMapper.map(room, RoomDTO.class);
					return roomDTO;
				}).collect(Collectors.toList());
			}
			return roomDtos;
		} catch (Exception ex) {
			logger.info("entering into error{}",ex.getMessage());
			throw new Exception("Rooms are not available for the particular Hostel" + hostelId);
		}

	}
	
	public String addAllRooms(List<RoomDTO> roomDTOs) {

		List<errordtls> err = new ArrayList<>();
		Map<Long,Integer> map = new HashMap<>();
		List<Room> rooms = roomDTOs.stream().map(dto -> {
			if(hostelClient.hostelExisits(dto.getHostelId())) {
			if(dto.getRoomId() != null ) {
				if((map.containsKey(dto.getHostelId()) && dto.getRoomId()<=map.get(dto.getHostelId())) || 
						roomRepository.exisitsByRoomId_RoomIdAndRoomId_HostelId(dto.getRoomId(), dto.getHostelId())) {
					err.add(new errordtls("Err4","roomid should be greater/roomid already exisits" ,dto.getRoomId().toString()));
				}else  map.put(dto.getHostelId(),dto.getRoomId());
			}else {
			map.putIfAbsent(dto.getHostelId(),roomRepository.findMaxRoomIdByHostelId(dto.getHostelId()).orElse(0));
			map.put(dto.getHostelId(),map.get(dto.getHostelId())+1);
			if (dto.getRoomCapacity() <= 0 || map.get(dto.getHostelId()) > dto.getRoomCapacity() ) {
				err.add(new errordtls("ERR_CAPACITY", "Room capacity must be greater than 0 Or sufficient rooms has been filled", dto.getHostelId().toString()));
				return null;
			}	 
			}
			RoomId roomId = new RoomId(map.get(dto.getHostelId()), dto.getHostelId());
			Room room = modelMapper.map(dto, Room.class);
			room.setRoomId(roomId);
			return room;
			}else {
				err.add(new errordtls("Error2", "hostelId is not valid", dto.getHostelId().toString()));
				return null;
			}

		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!err.isEmpty()) {
			throw new ResourceNotFoundException(err);
		}
		roomRepository.saveAll(rooms);

		return "Rooms added successfully";

	}
	
	public String updateRooms(List<RoomDTO> roomDTOs) {
		
		List<Room> rooms = roomDTOs.stream().map(dto ->{
			  Room existingRoom = roomRepository.findRoomByRoomIdAndHostelId(dto.getRoomId(), dto.getHostelId())
					  .orElseThrow(() -> new IllegalStateException("rooms are not available for"+dto.getRoomId()));
			RoomId roomId = new RoomId(dto.getRoomId(), dto.getHostelId());
			existingRoom.setRoomId(roomId);
			existingRoom.setPrice(dto.getPrice());
			existingRoom.setRoomCapacity(dto.getRoomCapacity());
			existingRoom.setStatus(dto.getStatus()!= null ? Status.valueOf(dto.getStatus()) : Status.AVAILABLE);
			return existingRoom;
			
		}).collect(Collectors.toList());
		roomRepository.saveAll(rooms);
		
		return "Rooms updated Successfully";
		
	}

	
	public String deleteRoomByRoomId(Integer roomID, Long hostelId) throws Exception {
		List<Room> rooms = new ArrayList<>();
		try {
			Boolean hostelFlag = hostelClient.hostelExisits(hostelId);
			logger.info("hostelFlag{}", hostelFlag);
			System.out.println(hostelFlag);
			if (hostelFlag) {
				if (roomID != null && hostelId != null) {
					Room room = roomRepository.findRoomByRoomIdAndHostelId(roomID, hostelId)
							.orElseThrow(() -> new IllegalStateException("rooms are not available for" + roomID));
					rooms.add(room);
				} else if (hostelId != null) {
					rooms = roomRepository.findByRoomId_HostelId(hostelId);
				} else {
					throw new IllegalStateException("roomId and hostelName cannot be null");
				}
				roomRepository.deleteAll(rooms);
			}else {
				throw new Exception("HostelId is not exisits in hostel database" + hostelId);
			}
			return "Rooms are deleted successfully";
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}

	}
	
	/***
	@Transactional
	public String updateHostels(Map<String,String> hstlMap) {
		logger.info("Entering into updateHostels {}", hstlMap);
//		List<Room> rooms = hstlMap.entrySet().stream().map(entry ->{
//			if(roomRepository.existsByRoomId_HostelName(entry.getKey())) {
//				List<Room> exisistRooms = roomRepository.findByRoomId_HostelName(entry.getKey());
//				logger.info("exisit rooms are{}",exisistRooms);
//				List<Room> updateRoom = exisistRooms.stream().map(room -> {
//					RoomId roomId = new RoomId(room.getRoomId().getRoomId(),entry.getValue());
//					room.setRoomId(roomId);
//					return room;
//				}).collect(Collectors.toList());
//				return updateRoom;
//			}
//			return new ArrayList<Room>(); //returing empty room if no hostelName exisits
//		}).flatMap(List::stream).collect(Collectors.toList());
//		logger.info("rooms are {} ", rooms.toString());
//		
//	   List<RoomDTO> roomDTOs = rooms.stream().map(r -> modelMapper.map(r, RoomDTO.class)).collect(Collectors.toList());
		
		hstlMap.forEach((oldname, newname) ->{
			if(roomRepository.existsByRoomId_HostelName(oldname)) {
				roomRepository.updateHostelNameInRooms(oldname, newname);
			}
		});
		
	return "rooms are updated with given hostelNames";
	}
 ***/
}
