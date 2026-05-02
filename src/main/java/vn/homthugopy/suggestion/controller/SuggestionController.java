package vn.homthugopy.suggestion.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import vn.homthugopy.suggestion.dto.ReplyRequestDTO;
import vn.homthugopy.suggestion.dto.StatsDTO;
import vn.homthugopy.suggestion.dto.SuggestionRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionResponseDTO;
import vn.homthugopy.suggestion.service.SuggestionService;

@RestController
public class SuggestionController {
	
	private final SuggestionService suggestionService;

	public SuggestionController(SuggestionService suggestionService) {
		this.suggestionService = suggestionService;
	}

	// API Lấy danh sách toàn bộ góp ý (giữ nguyên tương thích cũ)
	@GetMapping("/suggestions")
	public ResponseEntity<List<SuggestionResponseDTO>> getSuggestions() {
		List<SuggestionResponseDTO> listSuggestion = this.suggestionService.getAllSuggestions();
		return ResponseEntity.ok().body(listSuggestion);
	}

	// === MỚI: API Phân trang + Lọc ===
	@GetMapping("/suggestions/paged")
	public ResponseEntity<Page<SuggestionResponseDTO>> getPagedSuggestions(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "ALL") String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
	) {
		Page<SuggestionResponseDTO> result = this.suggestionService.getPagedSuggestions(page, size, status, from, to);
		return ResponseEntity.ok(result);
	}

	// === MỚI: API Thống kê cho Dashboard ===
	@GetMapping("/suggestions/stats")
	public ResponseEntity<StatsDTO> getStats(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
	) {
		StatsDTO stats = this.suggestionService.getStats(from, to);
		return ResponseEntity.ok(stats);
	}

	// === MỚI: API Xuất dữ liệu (cho Excel) ===
	@GetMapping("/suggestions/export")
	public ResponseEntity<List<SuggestionResponseDTO>> getExportData(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
	) {
		List<SuggestionResponseDTO> data = this.suggestionService.getExportData(from, to);
		return ResponseEntity.ok(data);
	}

	// API Lấy chi tiết một góp ý bằng ID
	@GetMapping("/suggestion/{id}")
	public ResponseEntity<SuggestionResponseDTO> getSuggestionById(@PathVariable Long id) {
		SuggestionResponseDTO suggestionData = this.suggestionService.getSuggestionById(id);
		if (suggestionData == null) {
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
		SuggestionResponseDTO createdSuggestion = this.suggestionService.createSuggestion(reqDTO);
		
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
