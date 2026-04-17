package vn.homthugopy.suggestion.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import vn.homthugopy.suggestion.dto.ReplyRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionResponseDTO;
import vn.homthugopy.suggestion.service.SuggestionService;

@RestController
public class SuggestionController {
	
	private final SuggestionService suggestionService;

	public SuggestionController(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	// API Lấy danh sách toàn bộ góp ý
	@GetMapping("/suggestions")
	public ResponseEntity<List<SuggestionResponseDTO>> getSuggestions() {
		List<SuggestionResponseDTO> listSuggestion = this.suggestionService.getAllSuggestions();
		return ResponseEntity.ok().body(listSuggestion);
	}

	// API Lấy chi tiết một góp ý bằng ID
	@GetMapping("/suggestion/{id}")
	public ResponseEntity<SuggestionResponseDTO> getSuggestionById(@PathVariable Long id) {
		SuggestionResponseDTO suggestionData = this.suggestionService.getSuggestionById(id);
		if (suggestionData == null) {
			// Sửa lỗi: Nếu không thấy dữ liệu thì trả về mã 404 Not Found
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(suggestionData);
	}

	// API Tra cứu một góp ý bằng mã Tracking Code
	@GetMapping("/suggestions/lookup/{trackingCode}")
	public ResponseEntity<SuggestionResponseDTO> getSuggestionByTrackingCode(@PathVariable String trackingCode) {
		SuggestionResponseDTO suggestionData = this.suggestionService.getSuggestionByTrackingCode(trackingCode);
		if (suggestionData == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().body(suggestionData);
	}

	// API Thêm mới một góp ý
	@PostMapping("/suggestions")
	public ResponseEntity<SuggestionResponseDTO> createSuggestion(@RequestBody SuggestionRequestDTO reqDTO) {
		// Gọi tầng Service để thực hiện thêm vào CSDL và sinh trackingCode
		SuggestionResponseDTO createdSuggestion = this.suggestionService.createSuggestion(reqDTO);
		
		// Trả về mã 201 Created cùng URL chứa ID mới tạo theo chuẩn REST
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(createdSuggestion.getId())
				.toUri();
				
		return ResponseEntity.created(location).body(createdSuggestion);
	}

	// API Phản hồi (Admin)
	@PutMapping("/suggestions/{id}/reply")
	public ResponseEntity<SuggestionResponseDTO> replySuggestion(@PathVariable Long id, @RequestBody ReplyRequestDTO req) {
		SuggestionResponseDTO updated = this.suggestionService.replySuggestion(id, req);
		if (updated == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(updated);
	}

	// API Xóa mềm (Admin)
	@DeleteMapping("/suggestions/{id}")
	public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
		boolean isDeleted = this.suggestionService.softDeleteSuggestion(id);
		if (!isDeleted) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.noContent().build();
	}

}
