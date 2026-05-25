package vn.homthugopy.suggestion.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "suggestions")
public class Suggestion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// Mã tra cứu ứng với mỗi góp ý (Được custom tạo ra Backend và gửi về Frontend)
	@Column(unique = true)
	private String trackingCode;

	// Nội dung góp ý
	@Lob
	private String body;

	// Tên người góp ý (ẩn danh)
	private String suggestedBy;

	// Cán bộ xử lý (Tên hiển thị)
	private String handledBy;
	
	// ID của cán bộ xử lý
	private Long handlerId;
	
	// ID của cán bộ được phân công (Dự phòng cho tính năng sau này)
	private Long assignedToId;

	// Nội dung phản hồi
	@Column(columnDefinition = "TEXT")
	private String response;

	// Thời gian góp ý
	private LocalDateTime suggestAt;

	// Thời gian phản hồi
	private LocalDateTime handledAt;

	// Trạng thái góp ý
	private String status; // PENDING, RESOLVED

	// Cờ đánh dấu đã xóa (soft delete)
	private boolean isDeleted;

	// Số điện thoại muốn được đơn vị liên lạc (tùy chọn, chỉ dùng khi gửi công khai)
	private String contactPhone;

	@Column(name = "unit_code")
	private String unitCode; // Đơn vị nhận góp ý

	// Constructor
	public Suggestion() {
	}

	// Getter & Setter
	public Long getId() {
		return id;
	}

	public String getTrackingCode() {
		return trackingCode;
	}

	public void setTrackingCode(String trackingCode) {
		this.trackingCode = trackingCode;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSuggestedBy() {
		return suggestedBy;
	}

	public void setSuggestedBy(String suggestedBy) {
		this.suggestedBy = suggestedBy;
	}

	public String getHandledBy() {
		return handledBy;
	}

	public void setHandledBy(String handledBy) {
		this.handledBy = handledBy;
	}

	public Long getHandlerId() {
		return handlerId;
	}

	public void setHandlerId(Long handlerId) {
		this.handlerId = handlerId;
	}

	public Long getAssignedToId() {
		return assignedToId;
	}

	public void setAssignedToId(Long assignedToId) {
		this.assignedToId = assignedToId;
	}

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}

	public LocalDateTime getSuggestAt() {
		return suggestAt;
	}

	public void setSuggestAt(LocalDateTime suggestAt) {
		this.suggestAt = suggestAt;
	}

	public LocalDateTime getHandledAt() {
		return handledAt;
	}

	public void setHandledAt(LocalDateTime handledAt) {
		this.handledAt = handledAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getContactPhone() {
		return contactPhone;
	}

	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

}
