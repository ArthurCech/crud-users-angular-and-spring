package io.github.arthurcech.dhaulagiri.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.arthurcech.dhaulagiri.entities.HttpResponse;

@RestController
public class ErrorControllerApi implements ErrorController {

	@RequestMapping("/error")
	public ResponseEntity<HttpResponse> notFound() {
		HttpStatus status = HttpStatus.NOT_FOUND;
		HttpResponse httpResponse = new HttpResponse(status.value(), status,
				status.getReasonPhrase().toUpperCase(), "There is no mapping for this URL");
		return new ResponseEntity<>(httpResponse, status);
	}

}
