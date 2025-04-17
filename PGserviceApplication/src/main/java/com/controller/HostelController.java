package com.controller;

import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.DTO.HostelDTO;
import com.service.HostelService;

import java.util.List;

@RestController
@RequestMapping("/api/hostels")
public class HostelController {
	
	private static final Logger logger = LoggerFactory.getLogger(HostelController.class);

	    @Autowired
	    private HostelService hostelService;

	    @GetMapping("/get")
	    public ResponseEntity<List<HostelDTO>> getHostels(
	            @RequestParam(required = false) String location,
	            @RequestParam(required = false) Double price) {

	        List<HostelDTO> hostelList = hostelService.getHostelsByLocation(location, price);
	        return ResponseEntity.ok(hostelList);
	    }

	    @PostMapping
	    public ResponseEntity<String> addHostels(@Valid @RequestBody List<HostelDTO> hostelDTOs) {
	    	logger.info("adding hostel{}");
	        String message = hostelService.addHostels(hostelDTOs);
	        return ResponseEntity.ok(message);
	    }

	    
	    @PutMapping
	    public ResponseEntity<String> updateHostels( @Valid @RequestBody List<HostelDTO> hostelDTOs) throws Exception {
	        String message = hostelService.updateHostel(hostelDTOs);
	        return ResponseEntity.ok(message);
	    }
	    
	    @DeleteMapping("/delete")
	    public ResponseEntity<String> deleteHostel(@RequestParam String hostelName) {
	    	try {
	            String message = hostelService.deleteHostel(hostelName);
	            return ResponseEntity.ok(message);
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
	        }
	    }
	    
	    @GetMapping("/exisits")
	    public boolean hostelExisits(@RequestParam String hostelName) {
	    	return hostelService.hostelExsists(hostelName);
	    }
	    
	    
	  

}
