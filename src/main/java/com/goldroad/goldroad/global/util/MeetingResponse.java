package com.goldroad.goldroad.global.util;

import java.util.List;

public class MeetingResponse {
	private String meetingPurpose;
	private String commonInterests;
	private String meetingFormat;
	private List<String> recommendedActivities;

	// Getter and Setter methods
	public String getMeetingPurpose() {
		return meetingPurpose;
	}

	public void setMeetingPurpose(String meetingPurpose) {
		this.meetingPurpose = meetingPurpose;
	}

	public String getCommonInterests() {
		return commonInterests;
	}

	public void setCommonInterests(String commonInterests) {
		this.commonInterests = commonInterests;
	}

	public String getMeetingFormat() {
		return meetingFormat;
	}

	public void setMeetingFormat(String meetingFormat) {
		this.meetingFormat = meetingFormat;
	}

	public List<String> getRecommendedActivities() {
		return recommendedActivities;
	}

	public void setRecommendedActivities(List<String> recommendedActivities) {
		this.recommendedActivities = recommendedActivities;
	}
}