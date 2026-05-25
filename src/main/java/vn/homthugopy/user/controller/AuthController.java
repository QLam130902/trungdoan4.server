package vn.homthugopy.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.homthugopy.config.CustomUserDetailsService;
import vn.homthugopy.config.JwtService;
import vn.homthugopy.user.dto.AuthResponseDTO;
import vn.homthugopy.user.dto.LoginRequestDTO;
import vn.homthugopy.user.entity.User;
import vn.homthugopy.user.repository.UserRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final UserRepository userRepository;

	public AuthController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService,
			JwtService jwtService, UserRepository userRepository) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtService = jwtService;
		this.userRepository = userRepository;
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
			);
			
			UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
			
			// Lấy thông tin user để trả về Frontend
			User user = userRepository.findByUsername(request.getUsername()).get();

			String token = jwtService.generateToken(userDetails, user.getUnitCode());
			
			AuthResponseDTO response = new AuthResponseDTO(
					token, 
					user.getUsername(), 
					user.getFullName(), 
					user.getRole(),
					user.getUnitCode()
			);
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Tài khoản hoặc mật khẩu không chính xác!");
		}
	}
}
