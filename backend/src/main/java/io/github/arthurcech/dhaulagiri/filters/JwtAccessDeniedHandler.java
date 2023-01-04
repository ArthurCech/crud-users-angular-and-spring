package io.github.arthurcech.dhaulagiri.filters;

import static io.github.arthurcech.dhaulagiri.constants.SecurityConstant.ACCESS_DENIED_MESSAGE;

import java.io.IOException;
import java.io.OutputStream;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.arthurcech.dhaulagiri.entities.HttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	@Override
	public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e)
			throws IOException {
		HttpStatus status = HttpStatus.FORBIDDEN;
		HttpResponse httpResponse = new HttpResponse(status.value(), status,
				status.getReasonPhrase().toUpperCase(), ACCESS_DENIED_MESSAGE);

		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setStatus(status.value());

		OutputStream outputStream = res.getOutputStream();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(outputStream, httpResponse);
		outputStream.flush();
	}

}
