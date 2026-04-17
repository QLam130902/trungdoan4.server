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
}
