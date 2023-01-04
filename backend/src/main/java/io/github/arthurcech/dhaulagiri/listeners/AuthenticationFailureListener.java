package io.github.arthurcech.dhaulagiri.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import io.github.arthurcech.dhaulagiri.services.LoginAttemptService;

@Component
public class AuthenticationFailureListener {

	private final LoginAttemptService loginAttemptService;

	public AuthenticationFailureListener(LoginAttemptService loginAttemptService) {
		this.loginAttemptService = loginAttemptService;
	}

	@EventListener
	public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
		Object principal = event.getAuthentication().getPrincipal();
		if (principal instanceof String) {
			String username = (String) event.getAuthentication().getPrincipal();
			loginAttemptService.addUserToLoginCache(username);
		}
	}

}
