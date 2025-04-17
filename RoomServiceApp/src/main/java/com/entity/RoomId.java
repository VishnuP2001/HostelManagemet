package com.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RoomId implements Serializable{
	
	private Integer roomId;
	
	@Column(name = "hostel_name")
	private String hostelName;


	public Integer getRoomId() {
		return roomId;
	}

	public void setRoomId(Integer roomId) {
		this.roomId = roomId;
	}

	public String getHostelName() {
		return hostelName;
	}

	public void setHostelName(String hostelName) {
		this.hostelName = hostelName;
	}

	public RoomId(int roomId, String hostelName) {
		this.roomId = roomId;
		this.hostelName = hostelName;
	}
public RoomId() {
	
}
	@Override
	public String toString() {
		return "RoomId [roomId=" + roomId + ", hostelName=" + hostelName + ", getRoomId()=" + getRoomId()
				+ ", getHostelName()=" + getHostelName() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(hostelName, roomId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RoomId other = (RoomId) obj;
		return Objects.equals(hostelName, other.hostelName) && Objects.equals(roomId, other.roomId);
	}
	
	

}
