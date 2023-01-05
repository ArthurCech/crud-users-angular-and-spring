package io.github.arthurcech.dhaulagiri.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import io.github.arthurcech.dhaulagiri.entities.User;

public interface UserService {

	User register(String firstName, String lastName, String username, String email);

	List<User> getUsers();

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

	User addNewUser(String firstName, String lastName, String username, String email, String role,
			boolean isNonLocked, boolean isActive, MultipartFile profileImage) throws IOException;

	User updateUser(String currentUsername, String newFirstName, String newLastName,
			String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive,
			MultipartFile profileImage) throws IOException;

	void deleteUser(String username) throws IOException;

	void resetPassword(String email);

	User updateProfileImage(String username, MultipartFile profileImage) throws IOException;

	void setLastLoginDate(User user);

}
