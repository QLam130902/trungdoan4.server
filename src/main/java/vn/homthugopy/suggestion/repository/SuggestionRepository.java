package vn.homthugopy.suggestion.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.homthugopy.suggestion.entity.Suggestion;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
	Optional<Suggestion> findByTrackingCode(String trackingCode);
	List<Suggestion> findByIsDeletedFalseOrderBySuggestAtDesc();
	
	// Đếm số lượng góp ý được tạo ra sau một mốc thời gian (dùng để đếm theo ngày)
	long countBySuggestAtAfter(LocalDateTime date);

	// Phân trang + lọc theo trạng thái + khoảng thời gian + danh sách đơn vị
	@Query("SELECT s FROM Suggestion s WHERE s.isDeleted = false " +
	       "AND (:status IS NULL OR s.status = :status) " +
	       "AND (:from IS NULL OR s.suggestAt >= :from) " +
	       "AND (:to IS NULL OR s.suggestAt <= :to) " +
	       "AND (:unitCodes IS NULL OR s.unitCode IN :unitCodes) " +
	       "ORDER BY s.suggestAt DESC")
	Page<Suggestion> findPagedFiltered(
		@Param("status") String status,
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to,
		@Param("unitCodes") List<String> unitCodes,
		Pageable pageable
	);

	// Lấy danh sách đầy đủ (không phân trang) theo khoảng thời gian và đơn vị — dùng cho xuất Excel và thống kê
	@Query("SELECT s FROM Suggestion s WHERE s.isDeleted = false " +
	       "AND (:from IS NULL OR s.suggestAt >= :from) " +
	       "AND (:to IS NULL OR s.suggestAt <= :to) " +
	       "AND (:unitCodes IS NULL OR s.unitCode IN :unitCodes) " +
	       "ORDER BY s.suggestAt ASC")
	List<Suggestion> findByDateRange(
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to,
		@Param("unitCodes") List<String> unitCodes
	);
}
