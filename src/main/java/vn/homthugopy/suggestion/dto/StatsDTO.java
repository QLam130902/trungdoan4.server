package vn.homthugopy.suggestion.dto;

import java.util.List;

public class StatsDTO {
	
	private long totalCount;
	private long resolvedCount;
	private long pendingCount;
	private List<DailyCount> dailyBreakdown;

	public StatsDTO() {
	}

	public StatsDTO(long totalCount, long resolvedCount, long pendingCount, List<DailyCount> dailyBreakdown) {
		this.totalCount = totalCount;
		this.resolvedCount = resolvedCount;
		this.pendingCount = pendingCount;
		this.dailyBreakdown = dailyBreakdown;
	}

	// Lớp con chứa dữ liệu theo từng ngày
	public static class DailyCount {
		private String date; // "2026-05-01"
		private long total;
		private long resolved;

		public DailyCount() {
		}

		public DailyCount(String date, long total, long resolved) {
			this.date = date;
			this.total = total;
			this.resolved = resolved;
		}

		public String getDate() { return date; }
		public void setDate(String date) { this.date = date; }
		public long getTotal() { return total; }
		public void setTotal(long total) { this.total = total; }
		public long getResolved() { return resolved; }
		public void setResolved(long resolved) { this.resolved = resolved; }
	}

	// Getters & Setters
	public long getTotalCount() { return totalCount; }
	public void setTotalCount(long totalCount) { this.totalCount = totalCount; }
	public long getResolvedCount() { return resolvedCount; }
	public void setResolvedCount(long resolvedCount) { this.resolvedCount = resolvedCount; }
	public long getPendingCount() { return pendingCount; }
	public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
	public List<DailyCount> getDailyBreakdown() { return dailyBreakdown; }
	public void setDailyBreakdown(List<DailyCount> dailyBreakdown) { this.dailyBreakdown = dailyBreakdown; }
}
