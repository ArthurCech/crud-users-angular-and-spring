package io.github.arthurcech.dhaulagiri.services.impl;

import static io.github.arthurcech.dhaulagiri.constants.FileConstant.DEFAULT_USER_IMAGE_PATH;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.EMAIL_ALREADY_EXISTS;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.USERNAME_ALREADY_EXISTS;
import static io.github.arthurcech.dhaulagiri.constants.ServiceConstant.USER_NOT_FOUND;
import static io.github.arthurcech.dhaulagiri.enums.Role.ROLE_USER;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.github.arthurcech.dhaulagiri.constants.ServiceConstant;
import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.entities.UserPrincipal;
import io.github.arthurcech.dhaulagiri.exceptions.entities.EmailExistException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UserNotFoundException;
import io.github.arthurcech.dhaulagiri.exceptions.entities.UsernameExistException;
import io.github.arthurcech.dhaulagiri.repositories.UserRepository;
import io.github.arthurcech.dhaulagiri.services.UserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository repository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository repository, BCryptPasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException(
						ServiceConstant.USER_NOT_FOUND.formatted(username)));
		user.setLastLoginDateDisplay(user.getLastLoginDate());
		user.setLastLoginDate(new Date());
		repository.save(user);
		return new UserPrincipal(user);
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

}
