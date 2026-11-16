package vn.homthugopy.suggestion.dto;

import java.util.List;

public class StatsDTO {
	
	private long totalCount;
	private long resolvedCount;
	private long pendingCount;
	private long receivedCount;
	private long completedCount;
	private List<DailyCount> dailyBreakdown;

	public StatsDTO() {
	}

	public StatsDTO(long totalCount, long resolvedCount, long pendingCount, List<DailyCount> dailyBreakdown) {
		this.totalCount = totalCount;
		this.resolvedCount = resolvedCount;
		this.pendingCount = pendingCount;
		this.dailyBreakdown = dailyBreakdown;
	}

	public StatsDTO(long totalCount, long resolvedCount, long pendingCount, long receivedCount, long completedCount, List<DailyCount> dailyBreakdown) {
		this.totalCount = totalCount;
		this.resolvedCount = resolvedCount;
		this.pendingCount = pendingCount;
		this.receivedCount = receivedCount;
		this.completedCount = completedCount;
		this.dailyBreakdown = dailyBreakdown;
	}

	// Lớp con chứa dữ liệu theo từng ngày
	public static class DailyCount {
		private String date; // "2026-05-01"
		private long total;
		private long resolved;
		private long received;
		private long completed;

		public DailyCount() {
		}

		public DailyCount(String date, long total, long resolved) {
			this.date = date;
			this.total = total;
			this.resolved = resolved;
		}

		public DailyCount(String date, long total, long resolved, long received, long completed) {
			this.date = date;
			this.total = total;
			this.resolved = resolved;
			this.received = received;
			this.completed = completed;
		}

		public String getDate() { return date; }
		public void setDate(String date) { this.date = date; }
		public long getTotal() { return total; }
		public void setTotal(long total) { this.total = total; }
		public long getResolved() { return resolved; }
		public void setResolved(long resolved) { this.resolved = resolved; }
		public long getReceived() { return received; }
		public void setReceived(long received) { this.received = received; }
		public long getCompleted() { return completed; }
		public void setCompleted(long completed) { this.completed = completed; }
	}

	// Getters & Setters
	public long getTotalCount() { return totalCount; }
	public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
	public long getResolvedCount() { return resolvedCount; }
	public void setResolvedCount(long resolvedCount) { this.resolvedCount = resolvedCount; }
	public long getPendingCount() { return pendingCount; }
	public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
	public long getReceivedCount() { return receivedCount; }
	public void setReceivedCount(long receivedCount) { this.receivedCount = receivedCount; }
	public long getCompletedCount() { return completedCount; }
	public void setCompletedCount(long completedCount) { this.completedCount = completedCount; }
	public List<DailyCount> getDailyBreakdown() { return dailyBreakdown; }
	public void setDailyBreakdown(List<DailyCount> dailyBreakdown) { this.dailyBreakdown = dailyBreakdown; }
}
