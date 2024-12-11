package com.goldroad.goldroad.global.security;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class RefreshToken {

	@Id
	private String email;

	private String refreshToken;

	public RefreshToken() {
	}

	public RefreshToken(String email, String refreshToken) {
		this.email = email;
		this.refreshToken = refreshToken;
	}

	public RefreshToken updateToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}
}
