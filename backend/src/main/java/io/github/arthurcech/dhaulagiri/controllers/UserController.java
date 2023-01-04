package io.github.arthurcech.dhaulagiri.controllers;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserController {

	private final UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

	@GetMapping(value = "/home")
	public String home() {
		return "Hello World!";
	}

	@PostMapping(value = "/register")
	public ResponseEntity<User> register(@RequestBody User user) {
		User newUser = service.register(user.getFirstName(), user.getLastName(), user.getUsername(),
				user.getEmail());
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(uri).body(newUser);
	}

}
