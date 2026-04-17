package vn.homthugopy.suggestion.dto;

public class SuggestionRequestDTO {
	
	private String body;
	private String suggestedBy;
	private String handledBy;

	public SuggestionRequestDTO() {
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
}
