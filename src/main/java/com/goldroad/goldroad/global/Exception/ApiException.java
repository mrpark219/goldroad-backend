package com.goldroad.goldroad.global.Exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiException extends RuntimeException {

	private HttpStatus httpStatus;

	public ApiException() {
		super();
	}

	public ApiException(String message) {
		super(message);
	}

	public ApiException(String message, HttpStatus status) {
		super(message);
		this.httpStatus = status;
	}
}
