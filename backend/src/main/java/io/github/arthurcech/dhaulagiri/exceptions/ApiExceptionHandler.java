package io.github.arthurcech.dhaulagiri.exceptions;

import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.ACCOUNT_DISABLED;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.ACCOUNT_LOCKED;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.ERROR_PROCESSING_FILE;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.INCORRECT_CREDENTIALS;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.INTERNAL_SERVER_ERROR_MSG;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.METHOD_IS_NOT_ALLOWED;
import static io.github.arthurcech.dhaulagiri.constants.ExceptionConstant.NOT_ENOUGH_PERMISSION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.io.IOException;
import java.util.Objects;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.auth0.jwt.exceptions.TokenExpiredException;

import io.github.arthurcech.dhaulagiri.entities.HttpResponse;
import io.github.arthurcech.dhaulagiri.exceptions.entities.EmailExistException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.EmailNotFoundException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UserNotFoundException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UsernameExistException;
import jakarta.persistence.NoResultException;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(DisabledException.class)
	public ResponseEntity<HttpResponse> handleDisabledException() {
		return createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<HttpResponse> handleBadCredentialsException() {
		return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<HttpResponse> handleAccessDeniedException() {
		return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<HttpResponse> handleLockedException() {
		return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<HttpResponse> handleTokenExpiredException(TokenExpiredException e) {
		return createHttpResponse(UNAUTHORIZED, e.getMessage());
	}

	@ExceptionHandler(EmailExistException.class)
	public ResponseEntity<HttpResponse> handleEmailExistException(EmailExistException e) {
		return createHttpResponse(BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(UsernameExistException.class)
	public ResponseEntity<HttpResponse> handleUsernameExistException(UsernameExistException e) {
		return createHttpResponse(BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(EmailNotFoundException.class)
	public ResponseEntity<HttpResponse> handleEmailNotFoundException(EmailNotFoundException e) {
		return createHttpResponse(BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException e) {
		return createHttpResponse(BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<HttpResponse> handleHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException e) {
		HttpMethod method = Objects.requireNonNull(e.getSupportedHttpMethods()).iterator().next();
		return createHttpResponse(METHOD_NOT_ALLOWED, METHOD_IS_NOT_ALLOWED.formatted(method));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<HttpResponse> handleException() {
		return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
	}

	@ExceptionHandler(NoResultException.class)
	public ResponseEntity<HttpResponse> handleNoResultException(NoResultException e) {
		return createHttpResponse(NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler(IOException.class)
	public ResponseEntity<HttpResponse> handleIOException() {
		return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
	}

	private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
	}

}
