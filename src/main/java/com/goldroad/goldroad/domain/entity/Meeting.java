package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Meeting extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "meeting_id")
	private Long id;

	private String title;

	private String summary;

	private String activity;

	private String preferredTime;

	private String keyword;

	public Meeting(String title, String summary, String activity, String preferredTime, String keyword) {
		this.title = title;
		this.summary = summary;
		this.activity = activity;
		this.preferredTime = preferredTime;
		this.keyword = keyword;
	}
}
