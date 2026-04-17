package vn.homthugopy.todo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	@GetMapping("/")
	public ResponseEntity<String> index() {
		return ResponseEntity.ok().body("Chào mừng thủ trưởng và các đồng chí đến với Sáng kiến Hòm thư Số");
	}

}
