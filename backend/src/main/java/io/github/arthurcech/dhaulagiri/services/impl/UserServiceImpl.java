package io.github.arthurcech.dhaulagiri.services.impl;

import static io.github.arthurcech.dhaulagiri.constants.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.DOT;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.FORWARD_SLASH;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.JPG_EXTENSION;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.NOT_AN_IMAGE_FILE;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.USER_FOLDER;
import static io.github.arthurcech.dhaulagiri.constants.FileConstant.USER_IMAGE_PATH;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.EMAIL_ALREADY_EXISTS;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.USERNAME_ALREADY_EXISTS;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.USER_NOT_FOUND;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.USER_NOT_FOUND_BY_EMAIL;
import static io.github.arthurcech.dhaulagiri.enums.Role.ROLE_USER;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.IMAGE_GIF_VALUE;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.arthurcech.dhaulagiri.constants.ServiceConstant;
import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.entities.UserPrincipal;
import io.github.arthurcech.dhaulagiri.enums.Role;
import io.github.arthurcech.dhaulagiri.exceptions.entities.EmailExistException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.EmailNotFoundException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.NotAnImageFileException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UserNotFoundException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UsernameExistException;
import io.github.arthurcech.dhaulagiri.repositories.UserRepository;
import io.github.arthurcech.dhaulagiri.services.EmailService;
import io.github.arthurcech.dhaulagiri.services.LoginAttemptService;
import io.github.arthurcech.dhaulagiri.services.UserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository repository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final LoginAttemptService loginAttemptService;
	private final EmailService emailService;

	public UserServiceImpl(UserRepository repository, BCryptPasswordEncoder passwordEncoder,
			LoginAttemptService loginAttemptService, EmailService emailService) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
		this.loginAttemptService = loginAttemptService;
		this.emailService = emailService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						ServiceConstant.USER_NOT_FOUND.formatted(username)));
		validateLoginAttempt(user);
		repository.save(user);
		return new UserPrincipal(user);
	}

	@Override
	public void setLastLoginDate(User user) {
		user.setLastLoginDateDisplay(user.getLastLoginDate());
		user.setLastLoginDate(new Date());
		repository.save(user);
	}

	@Override
	public User register(String firstName, String lastName, String username, String email) {
		validateUsernameAndEmail(null, username, email);

		String password = generatePassword();

		User user = new User.Builder().setUserId(generateUserId()).setFirstName(firstName)
				.setLastName(lastName).setUsername(username).setEmail(email).setJoinDate(new Date())
				.setPassword(encodePassword(password)).setActive(true).setNotLocked(true)
				.setRole(ROLE_USER.name()).setAuthorities(ROLE_USER.getAuthorities())
				.setProfileImageUrl(getTemporaryProfileImageUrl(username)).build();

		repository.save(user);

		emailService.sendNewPasswordEmail(firstName, password, email);

		return user;
	}

	@Override
	public List<User> getUsers() {
		return repository.findAll();
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return repository.findByUsername(username);
	}

	@Override
	public Optional<User> findByEmail(String email) {
		return repository.findByEmail(email);
	}

	@Override
	public User addNewUser(String firstName, String lastName, String username, String email,
			String role, boolean isNonLocked, boolean isActive, MultipartFile profileImage)
			throws IOException {
		validateUsernameAndEmail(null, username, email);

		String password = generatePassword();

		User user = new User.Builder().setUserId(generateUserId()).setFirstName(firstName)
				.setLastName(lastName).setUsername(username).setEmail(email).setJoinDate(new Date())
				.setPassword(encodePassword(password)).setActive(isActive).setNotLocked(isNonLocked)
				.setRole(getRoleEnumName(role).name())
				.setAuthorities(getRoleEnumName(role).getAuthorities())
				.setProfileImageUrl(getTemporaryProfileImageUrl(username)).build();

		repository.save(user);

		saveProfileImage(user, profileImage);

		return user;
	}

	@Override
	public User updateUser(String currentUsername, String newFirstName, String newLastName,
			String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive,
			MultipartFile profileImage) throws IOException {
		User currentUser = validateUsernameAndEmail(currentUsername, newUsername, newEmail);

		currentUser.setFirstName(newFirstName);
		currentUser.setLastName(newLastName);
		currentUser.setUsername(newUsername);
		currentUser.setEmail(newEmail);
		currentUser.setActive(isActive);
		currentUser.setNotLocked(isNonLocked);
		currentUser.setRole(getRoleEnumName(role).name());
		currentUser.setAuthorities(getRoleEnumName(role).getAuthorities());

		repository.save(currentUser);

		saveProfileImage(currentUser, profileImage);

		return currentUser;
	}

	@Override
	public void deleteUser(String username) throws IOException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND.formatted(username)));
		Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
		FileUtils.deleteDirectory(new File(userFolder.toString()));
		repository.delete(user);
	}

	@Override
	public void resetPassword(String email) {
		User user = repository.findByEmail(email).orElseThrow(
				() -> new EmailNotFoundException(USER_NOT_FOUND_BY_EMAIL.formatted(email)));

		String password = generatePassword();
		user.setPassword(encodePassword(password));

		repository.save(user);

		emailService.sendNewPasswordEmail(user.getFirstName(), password, user.getEmail());
	}

	@Override
	public User updateProfileImage(String username, MultipartFile profileImage) throws IOException {
		User user = validateUsernameAndEmail(username, null, null);
		saveProfileImage(user, profileImage);
		return user;
	}

	private User validateUsernameAndEmail(String currentUsername, String newUsername,
			String newEmail) {
		Optional<User> userByNewUsername = findByUsername(newUsername);
		Optional<User> userByNewEmail = findByEmail(newEmail);

		if (StringUtils.isNotBlank(currentUsername)) {
			User currentUser = findByUsername(currentUsername).orElseThrow(
					() -> new UserNotFoundException(USER_NOT_FOUND.formatted(currentUsername)));

			if (userByNewUsername.isPresent()
					&& !currentUser.getId().equals(userByNewUsername.get().getId())) {
				throw new UsernameExistException(USERNAME_ALREADY_EXISTS.formatted(newUsername));
			}
			if (userByNewEmail.isPresent()
					&& !currentUser.getId().equals(userByNewEmail.get().getId())) {
				throw new EmailExistException(EMAIL_ALREADY_EXISTS.formatted(newEmail));
			}

			return currentUser;
		}

		if (userByNewUsername.isPresent()) {
			throw new UsernameExistException(USERNAME_ALREADY_EXISTS.formatted(newUsername));
		}
		if (userByNewEmail.isPresent()) {
			throw new EmailExistException(EMAIL_ALREADY_EXISTS.formatted(newEmail));
		}

		return null;
	}

	private String generateUserId() {
		return RandomStringUtils.randomNumeric(10);
	}

	private String generatePassword() {
//		return RandomStringUtils.randomAlphanumeric(10);
		return "1";
	}

	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	private String getTemporaryProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
	}

	private void validateLoginAttempt(User user) {
		if (user.isNotLocked()) {
			if (loginAttemptService.userExceededAttempts(user.getUsername())) {
				user.setNotLocked(false);
			} else {
				user.setNotLocked(true);
			}
		} else {
			loginAttemptService.removeUserFromLoginCache(user.getUsername());
		}
	}

	private Role getRoleEnumName(String role) {
		return Role.valueOf(role.toUpperCase());
	}

	private void saveProfileImage(User user, MultipartFile profileImage) throws IOException {
		if (profileImage != null) {
			validateFileType(profileImage);

			Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath()
					.normalize();

			if (!Files.exists(userFolder)) {
				Files.createDirectories(userFolder);
			}

			Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));

			Files.copy(profileImage.getInputStream(),
					userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);

			user.setProfileImageUrl(setProfileImageUrl(user.getUsername()));

			repository.save(user);
		}
	}

	private String setProfileImageUrl(String username) {
		return ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION)
				.toUriString();
	}

	private void validateFileType(MultipartFile image) {
		List<String> types = Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE);

		if (!types.contains(image.getContentType())) {
			throw new NotAnImageFileException(image.getOriginalFilename() + NOT_AN_IMAGE_FILE);
		}
	}

}
