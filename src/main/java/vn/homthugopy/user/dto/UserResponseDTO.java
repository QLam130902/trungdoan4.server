package vn.homthugopy.user.dto;

import vn.homthugopy.user.entity.User;

public class UserResponseDTO {
	private Long id;
	private String username;
	private String fullName;
	private String rank;
	private String position;
	private String role;
	
	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.fullName = user.getFullName();
		this.rank = user.getRank();
		this.position = user.getPosition();
		this.role = user.getRole();
	}
	
	// Getters
	public Long getId() { return id; }
	public String getUsername() { return username; }
	public String getFullName() { return fullName; }
	public String getRank() { return rank; }
	public String getPosition() { return position; }
	public String getRole() { return role; }
}
