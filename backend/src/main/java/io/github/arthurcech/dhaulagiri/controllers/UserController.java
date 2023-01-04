package io.github.arthurcech.dhaulagiri.controllers;

import static io.github.arthurcech.dhaulagiri.constants.SecurityConstant.TOKEN_HEADER;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.entities.UserPrincipal;
import io.github.arthurcech.dhaulagiri.services.UserService;
import io.github.arthurcech.dhaulagiri.utils.JwtTokenProvider;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private final UserService service;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public UserController(UserService service, AuthenticationManager authenticationManager,
			JwtTokenProvider jwtTokenProvider) {
		this.service = service;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@GetMapping(value = "/home")
	public String home() {
		return "Hello World!";
	}

	@PostMapping(value = "/login")
	public ResponseEntity<User> login(@RequestBody User user) {
		authenticate(user.getUsername(), user.getPassword());
		User loginUser = service.findByUsername(user.getUsername()).get();
		UserPrincipal userPrincipal = new UserPrincipal(loginUser);
		HttpHeaders headers = getTokenHeader(userPrincipal);
		return ResponseEntity.ok().headers(headers).body(loginUser);
	}

	@PostMapping(value = "/register")
	public ResponseEntity<User> register(@RequestBody User user) {
		User newUser = service.register(user.getFirstName(), user.getLastName(), user.getUsername(),
				user.getEmail());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(uri).body(newUser);
	}

	private HttpHeaders getTokenHeader(UserPrincipal userPrincipal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

}
