package io.github.arthurcech.dhaulagiri.exceptions.entities;

public class EmailExistException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EmailExistException(String message) {
		super(message);
	}

}
