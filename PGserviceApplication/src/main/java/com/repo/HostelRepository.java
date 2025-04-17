package com.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.Entity.Hostel;
import com.Entity.Status;

public interface HostelRepository extends JpaRepository<Hostel, Long> {
	
	List<Hostel> findByLocationAndPriceLessThanEqualAndStatus(String location, double price, Status status);
	
	List<Hostel> findByLocationAndStatus(String location, Status status);
	
	List<Hostel> findBypriceLessThanEqualAndStatus(double price, Status status);
	
	void deleteByHostelName(String hostelName);
	
	boolean existsByHostelName(String hostelName);

}
