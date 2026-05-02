package vn.homthugopy.user.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.homthugopy.user.dto.UserRequestDTO;
import vn.homthugopy.user.dto.UserResponseDTO;
import vn.homthugopy.user.entity.User;
import vn.homthugopy.user.repository.UserRepository;

@RestController
@RequestMapping("/users")
public class UserController {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
		List<UserResponseDTO> users = userRepository.findAll().stream()
				.map(UserResponseDTO::new)
				.collect(Collectors.toList());
		return ResponseEntity.ok(users);
	}

	@PostMapping
	public ResponseEntity<?> createUser(@RequestBody UserRequestDTO request) {
		if (userRepository.existsByUsername(request.getUsername())) {
			return ResponseEntity.badRequest().body("Tài khoản đã tồn tại!");
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setFullName(request.getFullName());
		user.setRank(request.getRank());
		user.setPosition(request.getPosition());
		user.setRole(request.getRole());
		user.setPhone(request.getPhone()); // Số điện thoại liên hệ

		User savedUser = userRepository.save(user);
		return ResponseEntity.ok(new UserResponseDTO(savedUser));
	}

	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserRequestDTO request) {
		return userRepository.findById(id).map(user -> {
			// Chỉ đổi mật khẩu nếu có gửi mật khẩu mới
			if (request.getPassword() != null && !request.getPassword().isEmpty()) {
				user.setPassword(passwordEncoder.encode(request.getPassword()));
			}
			user.setFullName(request.getFullName());
			user.setRank(request.getRank());
			user.setPosition(request.getPosition());
			user.setRole(request.getRole());
			user.setPhone(request.getPhone());
			
			User updatedUser = userRepository.save(user);
			return ResponseEntity.ok(new UserResponseDTO(updatedUser));
		}).orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteUser(@PathVariable Long id) {
		// Lấy thông tin người dùng đang đăng nhập từ SecurityContext
		String currentUsername = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName();
		
		return userRepository.findById(id).map(userToDelete -> {
			// Kiểm tra nếu người dùng đang xóa chính mình
			if (userToDelete.getUsername().equals(currentUsername)) {
				return ResponseEntity.badRequest().body("Bạn không thể tự xóa tài khoản của chính mình!");
			}
			
			userRepository.delete(userToDelete);
			return ResponseEntity.ok("Đã xóa cán bộ thành công!");
		}).orElse(ResponseEntity.notFound().build());
	}
}
