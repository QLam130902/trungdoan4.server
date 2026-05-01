package vn.homthugopy.user.dto;

public class UserRequestDTO {
	private String username;
	private String password;
	private String fullName;
	private String rank;
	private String position;
	private String role; // ROLE_ADMIN, ROLE_OFFICER
	private String phone; // Số điện thoại
	
	// Getters and Setters
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public String getFullName() { return fullName; }
	public void setFullName(String fullName) { this.fullName = fullName; }
	public String getRank() { return rank; }
	public void setRank(String rank) { this.rank = rank; }
	public String getPosition() { return position; }
	public void setPosition(String position) { this.position = position; }
	public String getRole() { return role; }
	public void setRole(String role) { this.role = role; }
	public String getPhone() { return phone; }
	public void setPhone(String phone) { this.phone = phone; }
}
