package com.goldroad.goldroad.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Authority extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "authority_id")
	private Long id;

	private String name;

	private String description;
}
