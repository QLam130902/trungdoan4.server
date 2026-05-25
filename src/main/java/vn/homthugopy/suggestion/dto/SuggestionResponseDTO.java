package vn.homthugopy.suggestion.dto;

import java.time.LocalDateTime;

public class SuggestionResponseDTO {
	
	private Long id;
	private String trackingCode;
	private String body;
	private String suggestedBy;
	private String handledBy;
	private String response;
	private LocalDateTime suggestAt;
	private LocalDateTime handledAt;
	private String status;
	private String contactPhone; // SĐT người gửi muốn được liên hệ
	private String handlerPhone; // SĐT cán bộ xử lý (trả về cho Frontend hiển thị)
	private String unitCode; // Mã đơn vị nhận góp ý

	public SuggestionResponseDTO() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getContactPhone() { return contactPhone; }
	public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

	public String getHandlerPhone() { return handlerPhone; }
	public void setHandlerPhone(String handlerPhone) { this.handlerPhone = handlerPhone; }

	public String getUnitCode() { return unitCode; }
	public void setUnitCode(String unitCode) { this.unitCode = unitCode; }
}
