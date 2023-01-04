package io.github.arthurcech.dhaulagiri.filters;

import static io.github.arthurcech.dhaulagiri.constants.SecurityConstant.FORBIDDEN_MESSAGE;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.arthurcech.dhaulagiri.entities.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {

	@Override
	public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e)
			throws IOException {
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		HttpResponse httpResponse = new HttpResponse(status.value(), status,
				status.getReasonPhrase().toUpperCase(), FORBIDDEN_MESSAGE);

		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setStatus(status.value());

		OutputStream outputStream = res.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

}
