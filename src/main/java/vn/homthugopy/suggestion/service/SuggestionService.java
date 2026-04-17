package vn.homthugopy.suggestion.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.homthugopy.suggestion.dto.ReplyRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionResponseDTO;
import vn.homthugopy.suggestion.entity.Suggestion;
import vn.homthugopy.suggestion.repository.SuggestionRepository;

@Service
public class SuggestionService {

	private final SuggestionRepository suggestionRepository;

	public SuggestionService(SuggestionRepository suggestionRepository) {
		this.suggestionRepository = suggestionRepository;
	}

	// Chuyển Entity sang DTO
	private SuggestionResponseDTO mapToDTO(Suggestion entity) {
		SuggestionResponseDTO dto = new SuggestionResponseDTO();
		dto.setId(entity.getId());
		dto.setTrackingCode(entity.getTrackingCode());
		dto.setBody(entity.getBody());
		dto.setSuggestedBy(entity.getSuggestedBy());
		dto.setHandledBy(entity.getHandledBy());
		dto.setResponse(entity.getResponse());
		dto.setSuggestAt(entity.getSuggestAt());
		dto.setHandledAt(entity.getHandledAt());
		dto.setStatus(entity.getStatus());
		return dto;
	}

	// Lấy chi tiết 1 góp ý
	public SuggestionResponseDTO getSuggestionById(Long id) {
		Optional<Suggestion> suggestionOptional = this.suggestionRepository.findById(id);
		// Xử lý null sẽ được Controller tiếp nhận và trả về 404
		return suggestionOptional.map(this::mapToDTO).orElse(null);
	}

	// Lấy chi tiết 1 góp ý bằng mã tra cứu (tracking code)
	public SuggestionResponseDTO getSuggestionByTrackingCode(String trackingCode) {
		Optional<Suggestion> suggestionOptional = this.suggestionRepository.findByTrackingCode(trackingCode);
		return suggestionOptional.map(this::mapToDTO).orElse(null);
	}

	// Lấy tất cả (chỉ lấy những cái chưa xóa, sắp xếp mới nhất lên đầu)
	public List<SuggestionResponseDTO> getAllSuggestions() {
		return this.suggestionRepository.findByIsDeletedFalseOrderBySuggestAtDesc().stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	// Tạo mới một góp ý
	public SuggestionResponseDTO createSuggestion(SuggestionRequestDTO requestDTO) {
		Suggestion newSuggestion = new Suggestion();
		newSuggestion.setBody(requestDTO.getBody());
		newSuggestion.setSuggestedBy(requestDTO.getSuggestedBy());
		newSuggestion.setHandledBy(requestDTO.getHandledBy());
		
		// Khởi tạo các giá trị mặc định cho luồng xử lí
		newSuggestion.setSuggestAt(LocalDateTime.now());
		newSuggestion.setStatus("PENDING"); // trạng thái ban đầu là Đang chờ xử lý
		
		// Tự động sinh mã tra cứu (VD: 6 ký tự đầu của UUID)
		String generatedCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
		newSuggestion.setTrackingCode("GY-" + generatedCode);
		
		// Lưu xuống DB
		Suggestion savedEntity = this.suggestionRepository.save(newSuggestion);
		
		// Trả về dữ liệu an toàn cho client hiển thị
		return mapToDTO(savedEntity);
	}

	// Phản hồi góp ý
	public SuggestionResponseDTO replySuggestion(Long id, ReplyRequestDTO reqDTO) {
		Optional<Suggestion> opt = this.suggestionRepository.findById(id);
		if (opt.isPresent()) {
			Suggestion s = opt.get();
			s.setResponse(reqDTO.getResponse());
			s.setStatus("RESOLVED");
			s.setHandledAt(LocalDateTime.now());
			Suggestion saved = this.suggestionRepository.save(s);
			return mapToDTO(saved);
		}
		return null;
	}

	// Xóa mềm góp ý
	public boolean softDeleteSuggestion(Long id) {
		Optional<Suggestion> opt = this.suggestionRepository.findById(id);
		if (opt.isPresent()) {
			Suggestion s = opt.get();
			s.setDeleted(true);
			this.suggestionRepository.save(s);
			return true;
		}
		return false;
	}
}
