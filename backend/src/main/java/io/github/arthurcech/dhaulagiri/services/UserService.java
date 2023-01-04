package io.github.arthurcech.dhaulagiri.services;

import java.util.List;
import java.util.Optional;

import io.github.arthurcech.dhaulagiri.entities.User;

public interface UserService {

	User register(String firstName, String lastName, String username, String email);

	List<User> getUsers();

	Optional<User> findByUsername(String username);

	Optional<User> findByEmail(String email);

//	User addNewUser(String firstName, String lastName, String username, String email, String role,
//			boolean isNonLocked, boolean isActive, MultipartFile profileImage);
//
//	User updateUser(String currentUsername, String newFirstName, String newLastName,
//			String newUsername, String newEmail, String role, boolean isNonLocked, boolean isActive,
//			MultipartFile profileImage);
//
//	void deleteUser(String username);
//
//	void resetPassword(String email);
//
//	User updateProfileImage(String username, MultipartFile profileImage);

	void setLastLoginDate(User user);

}
