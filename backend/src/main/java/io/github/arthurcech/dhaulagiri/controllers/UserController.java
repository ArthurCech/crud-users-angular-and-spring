package io.github.arthurcech.dhaulagiri.controllers;

import static io.github.arthurcech.dhaulagiri.constants.ControllerConstant.EMAIL_SENT;
import static io.github.arthurcech.dhaulagiri.constants.ControllerConstant.USER_DELETED_SUCCESSFULLY;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.FORWARD_SLASH;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.TEMP_PROFILE_IMAGE_BASE_URL;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.USER_FOLDER;
import static io.github.arthurcech.dhaulagiri.constants.SecurityConstant.TOKEN_HEADER;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.arthurcech.dhaulagiri.constants.ServiceConstant;
import io.github.arthurcech.dhaulagiri.entities.HttpResponse;
import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.entities.UserPrincipal;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UserNotFoundException;
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
		service.setLastLoginDate(loginUser);
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

	@PostMapping(value = "/add")
	public ResponseEntity<User> addNewUser(@RequestParam String firstName,
			@RequestParam String lastName, @RequestParam String username,
			@RequestParam String email, @RequestParam String role, @RequestParam String isActive,
			@RequestParam String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws IOException {
		User newUser = service.addNewUser(firstName, lastName, username, email, role,
				Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive), profileImage);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(uri).body(newUser);
	}

	@PutMapping(value = "/update")
	public ResponseEntity<User> update(@RequestParam String currentUsername,
			@RequestParam String firstName, @RequestParam String lastName,
			@RequestParam String username, @RequestParam String email, @RequestParam String role,
			@RequestParam String isActive, @RequestParam String isNonLocked,
			@RequestParam(value = "profileImage", required = false) MultipartFile profileImage)
			throws IOException {
		User updatedUser = service.updateUser(currentUsername, firstName, lastName, username, email,
				role, Boolean.parseBoolean(isNonLocked), Boolean.parseBoolean(isActive),
				profileImage);
		return ResponseEntity.ok(updatedUser);
	}

	@GetMapping(value = "/find/{username}")
	public ResponseEntity<User> getUser(@PathVariable String username) {
		User user = service.findByUsername(username).orElseThrow(() -> new UserNotFoundException(
				ServiceConstant.USER_NOT_FOUND.formatted(username)));
		return ResponseEntity.ok(user);
	}

	@GetMapping(value = "/list")
	public List<User> getAllUsers() {
		return service.getUsers();
	}

	@GetMapping(value = "/resetpassword/{email}")
	public ResponseEntity<HttpResponse> resetPassword(@PathVariable String email) {
		service.resetPassword(email);
		return response(OK, EMAIL_SENT + email);
	}

	@DeleteMapping(value = "/delete/{username}")
	public ResponseEntity<HttpResponse> deleteUser(@PathVariable String username)
			throws IOException {
		service.deleteUser(username);
		return response(OK, USER_DELETED_SUCCESSFULLY);
	}

	@PostMapping(value = "/update-profile-image")
	public ResponseEntity<User> updateProfileImage(@RequestParam String username,
			@RequestParam MultipartFile profileImage) throws IOException {
		User user = service.updateProfileImage(username, profileImage);
		return ResponseEntity.ok(user);
	}

	@GetMapping(value = "/image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
	public byte[] getProfileImage(@PathVariable String username, @PathVariable String fileName)
			throws IOException {
		return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
	}

	@GetMapping(value = "/image/profile/{username}", produces = IMAGE_JPEG_VALUE)
	public byte[] getTempProfileImage(@PathVariable("username") String username)
			throws IOException {
		URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		try (InputStream inputStream = url.openStream()) {
			int bytesRead;

			byte[] chunk = new byte[1024];

			while ((bytesRead = inputStream.read(chunk)) > 0) {
				byteArrayOutputStream.write(chunk, 0, bytesRead);
			}
		}

		return byteArrayOutputStream.toByteArray();
	}

	private HttpHeaders getTokenHeader(UserPrincipal userPrincipal) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(TOKEN_HEADER, jwtTokenProvider.generateJwtToken(userPrincipal));
		return headers;
	}

	private void authenticate(String username, String password) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(username, password));
	}

	private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
		return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
				httpStatus.getReasonPhrase().toUpperCase(), message), httpStatus);
	}

}
