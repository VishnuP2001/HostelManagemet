package com.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@FeignClient(name = "RoomServiceApp" , url = "http://localhost:8081/room")
public interface RoomClient {
	
	
	 @DeleteMapping("/delete")
	    public ResponseEntity<String> deleteRoom(
	           @Valid @RequestParam(required = false) Integer roomId,
	            @Valid @RequestParam String hostelName) throws Exception;
	

}
