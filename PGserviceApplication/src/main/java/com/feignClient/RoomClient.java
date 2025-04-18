package com.feignClient;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@FeignClient(name = "RoomServiceApp" , url = "http://localhost:8081/room")
public interface RoomClient {
	
	
	 @DeleteMapping("/delete")
	    public ResponseEntity<String> deleteRoom(
	           @Valid @RequestParam(required = false) Integer roomId,
	            @Valid @RequestParam Long hostelId) throws Exception;
	

}
