package com.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.validation.Valid;

@FeignClient(name = "PGserviceApplication" , url = "http://localhost:8080/myapp/api/hostels")
public interface HostelClient {
	
	
	 @GetMapping("/exisits")
	    public boolean hostelExisits(@RequestParam String hostelName);
	

}
