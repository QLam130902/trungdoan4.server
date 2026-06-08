package vn.homthugopy.suggestion.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.homthugopy.suggestion.dto.ReplyRequestDTO;
import vn.homthugopy.suggestion.dto.StatsDTO;
import vn.homthugopy.suggestion.dto.SuggestionRequestDTO;
import vn.homthugopy.suggestion.dto.SuggestionResponseDTO;
import vn.homthugopy.suggestion.entity.Suggestion;
import vn.homthugopy.suggestion.repository.SuggestionRepository;
import vn.homthugopy.unit.entity.Unit;
import vn.homthugopy.unit.repository.UnitRepository;
import vn.homthugopy.user.entity.User;
import vn.homthugopy.user.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class SuggestionService {

	private final SuggestionRepository suggestionRepository;
	private final UserRepository userRepository;
	private final UnitRepository unitRepository;

	public SuggestionService(SuggestionRepository suggestionRepository, UserRepository userRepository, UnitRepository unitRepository) {
		this.suggestionRepository = suggestionRepository;
		this.userRepository = userRepository;
		this.unitRepository = unitRepository;
	}

	// Lấy danh sách mã đơn vị được phép xem
	private List<String> getAccessibleUnitCodes(User user) {
		if (user == null) {
			return List.of();
		}
		// Admin cấp Trung đoàn xem được tất cả
		if ("ROLE_ADMIN".equals(user.getRole()) || "TRUNG_DOAN_4".equals(user.getUnitCode())) {
			return null;
		}

		List<String> codes = new ArrayList<>();
		codes.add(user.getUnitCode());

		// Nếu là cán bộ Tiểu đoàn, lấy thêm tất cả các Đại đội trực thuộc
		if (user.getUnitCode() != null && user.getUnitCode().startsWith("TD")) {
			List<Unit> childUnits = unitRepository.findByParentCode(user.getUnitCode());
			for (Unit child : childUnits) {
				codes.add(child.getCode());
			}
		}
		return codes;
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
		dto.setContactPhone(entity.getContactPhone());
		dto.setUnitCode(entity.getUnitCode());

		// Tra cứu SĐT cán bộ xử lý nếu đã có người xử lý
		if (entity.getHandlerId() != null) {
			userRepository.findById(entity.getHandlerId()).ifPresent(handler -> {
				dto.setHandlerPhone(handler.getPhone());
			});
		}

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

	// === MỚI: Phân trang + lọc ===
	public Page<SuggestionResponseDTO> getPagedSuggestions(int page, int size, String status,
			LocalDateTime from, LocalDateTime to) {
		Pageable pageable = PageRequest.of(page, size);
		String filterStatus = "ALL".equals(status) ? null : status;

		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(currentUsername).orElse(null);
		List<String> unitCodes = getAccessibleUnitCodes(user);

		Page<Suggestion> pagedEntities;
		if (unitCodes == null) {
			// Admin: xem tất cả đơn vị
			pagedEntities = this.suggestionRepository.findPagedFilteredAll(filterStatus, from, to, pageable);
		} else {
			// Officer: chỉ xem đơn vị được phân quyền
			pagedEntities = this.suggestionRepository.findPagedFilteredByUnits(filterStatus, from, to, unitCodes, pageable);
		}
		return pagedEntities.map(this::mapToDTO);
	}

	// === MỚI: Thống kê cho Dashboard ===
	public StatsDTO getStats(LocalDateTime from, LocalDateTime to) {
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(currentUsername).orElse(null);
		List<String> unitCodes = getAccessibleUnitCodes(user);

		List<Suggestion> allInRange;
		if (unitCodes == null) {
			allInRange = this.suggestionRepository.findByDateRangeAll(from, to);
		} else {
			allInRange = this.suggestionRepository.findByDateRangeByUnits(from, to, unitCodes);
		}
		
		long totalCount = allInRange.size();
		long resolvedCount = allInRange.stream().filter(s -> "RESOLVED".equals(s.getStatus())).count();
		long pendingCount = allInRange.stream().filter(s -> "PENDING".equals(s.getStatus())).count();

		// Tạo breakdown theo ngày — gom nhóm theo ngày
		Map<String, long[]> dailyMap = new LinkedHashMap<>();
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		for (Suggestion s : allInRange) {
			String dateKey = s.getSuggestAt().format(fmt);
			dailyMap.computeIfAbsent(dateKey, k -> new long[]{0, 0});
			long[] counts = dailyMap.get(dateKey);
			counts[0]++; // total
			if ("RESOLVED".equals(s.getStatus())) {
				counts[1]++; // resolved
			}
		}

		List<StatsDTO.DailyCount> dailyBreakdown = new ArrayList<>();
		for (Map.Entry<String, long[]> entry : dailyMap.entrySet()) {
			dailyBreakdown.add(new StatsDTO.DailyCount(entry.getKey(), entry.getValue()[0], entry.getValue()[1]));
		}

		return new StatsDTO(totalCount, resolvedCount, pendingCount, dailyBreakdown);
	}

	// === MỚI: Lấy dữ liệu xuất Excel (toàn bộ, không phân trang) ===
	public List<SuggestionResponseDTO> getExportData(LocalDateTime from, LocalDateTime to) {
		String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(currentUsername).orElse(null);
		List<String> unitCodes = getAccessibleUnitCodes(user);

		List<Suggestion> suggestions;
		if (unitCodes == null) {
			suggestions = this.suggestionRepository.findByDateRangeAll(from, to);
		} else {
			suggestions = this.suggestionRepository.findByDateRangeByUnits(from, to, unitCodes);
		}
		return suggestions.stream()
				.map(this::mapToDTO)
				.collect(Collectors.toList());
	}
	
	// Tạo mới một góp ý
	public SuggestionResponseDTO createSuggestion(SuggestionRequestDTO requestDTO) {
		Suggestion newSuggestion = new Suggestion();
		newSuggestion.setBody(requestDTO.getBody());
		newSuggestion.setSuggestedBy(requestDTO.getSuggestedBy());
		newSuggestion.setHandledBy(requestDTO.getHandledBy());
		newSuggestion.setUnitCode(requestDTO.getUnitCode());
		
		// Khởi tạo các giá trị mặc định cho luồng xử lí
		newSuggestion.setSuggestAt(LocalDateTime.now());
		newSuggestion.setStatus("PENDING"); // trạng thái ban đầu là Đang chờ xử lý
		
		// Tự động sinh mã tra cứu theo ngày tháng (VD: 280426A)
		LocalDate today = LocalDate.now();
		String datePrefix = today.format(DateTimeFormatter.ofPattern("ddMMyy"));
		long countToday = this.suggestionRepository.countBySuggestAtAfter(today.atStartOfDay());
		
		// Chuyển đổi countToday thành chữ cái (0 -> A, 25 -> Z, 26 -> AA...)
		String suffix = "";
		long num = countToday;
		do {
			long remainder = num % 26;
			suffix = (char) ('A' + remainder) + suffix;
			num = (num / 26) - 1;
		} while (num >= 0);
		
		newSuggestion.setTrackingCode(datePrefix + suffix);
		newSuggestion.setContactPhone(requestDTO.getContactPhone()); // Lưu SĐT liên hệ nếu có
		
		// Nếu người dùng muốn được liên hệ, tự động gán cán bộ mặc định (tuannvt) để tra cứu SĐT
		if (requestDTO.getContactPhone() != null && !requestDTO.getContactPhone().isBlank()) {
			userRepository.findByUsername("tuannvt").ifPresent(officer -> {
				newSuggestion.setHandlerId(officer.getId());
				newSuggestion.setHandledBy(officer.getRank() + " " + officer.getFullName());
			});
		}
		
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
			
			// Lấy thông tin cán bộ đang đăng nhập từ SecurityContext
			String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
			Optional<User> currentUserOpt = userRepository.findByUsername(currentUsername);
			if (currentUserOpt.isPresent()) {
				User currentUser = currentUserOpt.get();
				s.setHandlerId(currentUser.getId());
				s.setHandledBy(currentUser.getRank() + " " + currentUser.getFullName());
			}
			
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
