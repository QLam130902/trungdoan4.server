package vn.homthugopy.user.dto;

public class AuthResponseDTO {
	private String token;
	private String username;
	private String fullName;
	private String role;
	private String unitCode;
	
	public AuthResponseDTO(String token, String username, String fullName, String role, String unitCode) {
		this.token = token;
		this.username = username;
		this.fullName = fullName;
		this.role = role;
		this.unitCode = unitCode;
	}
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getUnitCode() {
		return unitCode;
	}
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}
}
