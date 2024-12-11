package com.goldroad.goldroad.global.config;


import com.goldroad.goldroad.global.Exception.ApiException;
import com.goldroad.goldroad.global.dto.GlobalResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<GlobalResponseDto> ApiException(ApiException e) {

		HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

		if(e.getHttpStatus() != null) {
			httpStatus = e.getHttpStatus();
		}

		GlobalResponseDto globalResponseDto = GlobalResponseDto.builder()
			.message(e.getMessage())
			.statusCode(httpStatus.value())
			.build();

		return new ResponseEntity<>(globalResponseDto, httpStatus);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<GlobalResponseDto> Exception(AuthenticationException e) {

		String message;

		if(e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
			message = "아이디나 비밀번호가 맞지 않습니다. 다시 확인해 주십시오.";
		}
		else if(e instanceof DisabledException) {
			message = "계정이 비활성화 되었습니다. 관리자에게 문의하세요.";
		}
		else if(e instanceof CredentialsExpiredException) {
			message = "비밀번호 유효기간이 만료 되었습니다. 관리자에게 문의하세요.";
		}
		else {
			message = "알 수 없는 이유로 로그인에 실패하였습니다. 관리자에게 문의하세요.";
		}

		GlobalResponseDto globalResponseDto = GlobalResponseDto.builder()
			.message(message)
			.statusCode(HttpStatus.UNAUTHORIZED.value())
			.build();

		return new ResponseEntity<>(globalResponseDto, HttpStatus.UNAUTHORIZED);
	}
}
