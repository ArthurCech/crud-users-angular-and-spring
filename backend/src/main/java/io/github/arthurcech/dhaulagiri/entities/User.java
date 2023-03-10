package io.github.arthurcech.dhaulagiri.entities;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false, updatable = false)
	@JsonProperty(access = Access.WRITE_ONLY)
	private Long id;
	private String userId;
	private String firstName;
	private String lastName;
	private String username;
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;
	private String email;
	private String profileImageUrl;
	private Date lastLoginDate;
	private Date lastLoginDateDisplay;
	private Date joinDate;
	private String role;
	private String[] authorities;
	private boolean isActive;
	private boolean isNotLocked;

	public User() {
	}

	public User(Long id, String userId, String firstName, String lastName, String username,
			String password, String email, String profileImageUrl, Date lastLoginDate,
			Date lastLoginDateDisplay, Date joinDate, String role, String[] authorities,
			boolean isActive, boolean isNotLocked) {
		this.id = id;
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.username = username;
		this.password = password;
		this.email = email;
		this.profileImageUrl = profileImageUrl;
		this.lastLoginDate = lastLoginDate;
		this.lastLoginDateDisplay = lastLoginDateDisplay;
		this.joinDate = joinDate;
		this.role = role;
		this.authorities = authorities;
		this.isActive = isActive;
		this.isNotLocked = isNotLocked;
	}

	public User(Builder builder) {
		this.id = builder.id;
		this.userId = builder.userId;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
		this.username = builder.username;
		this.password = builder.password;
		this.email = builder.email;
		this.profileImageUrl = builder.profileImageUrl;
		this.lastLoginDate = builder.lastLoginDate;
		this.lastLoginDateDisplay = builder.lastLoginDateDisplay;
		this.joinDate = builder.joinDate;
		this.role = builder.role;
		this.authorities = builder.authorities;
		this.isActive = builder.isActive;
		this.isNotLocked = builder.isNotLocked;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getProfileImageUrl() {
		return profileImageUrl;
	}

	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public Date getLastLoginDate() {
		return lastLoginDate;
	}

	public void setLastLoginDate(Date lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}

	public Date getLastLoginDateDisplay() {
		return lastLoginDateDisplay;
	}

	public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
		this.lastLoginDateDisplay = lastLoginDateDisplay;
	}

	public Date getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(Date joinDate) {
		this.joinDate = joinDate;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String[] getAuthorities() {
		return authorities;
	}

	public void setAuthorities(String[] authorities) {
		this.authorities = authorities;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public boolean isNotLocked() {
		return isNotLocked;
	}

	public void setNotLocked(boolean isNotLocked) {
		this.isNotLocked = isNotLocked;
	}

	public static class Builder {
		private Long id;
		private String userId;
		private String firstName;
		private String lastName;
		private String username;
		private String password;
		private String email;
		private String profileImageUrl;
		private Date lastLoginDate;
		private Date lastLoginDateDisplay;
		private Date joinDate;
		private String role;
		private String[] authorities;
		private boolean isActive;
		private boolean isNotLocked;

		public Builder() {
		}

		public Builder setId(Long id) {
			this.id = id;
			return this;
		}

		public Builder setUserId(String userId) {
			this.userId = userId;
			return this;
		}

		public Builder setFirstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder setLastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder setUsername(String username) {
			this.username = username;
			return this;
		}

		public Builder setPassword(String password) {
			this.password = password;
			return this;
		}

		public Builder setEmail(String email) {
			this.email = email;
			return this;
		}

		public Builder setProfileImageUrl(String profileImageUrl) {
			this.profileImageUrl = profileImageUrl;
			return this;
		}

		public Builder setLastLoginDate(Date lastLoginDate) {
			this.lastLoginDate = lastLoginDate;
			return this;
		}

		public Builder setLastLoginDateDisplay(Date lastLoginDateDisplay) {
			this.lastLoginDateDisplay = lastLoginDateDisplay;
			return this;
		}

		public Builder setJoinDate(Date joinDate) {
			this.joinDate = joinDate;
			return this;
		}

		public Builder setRole(String role) {
			this.role = role;
			return this;
		}

		public Builder setAuthorities(String[] authorities) {
			this.authorities = authorities;
			return this;
		}

		public Builder setActive(boolean isActive) {
			this.isActive = isActive;
			return this;
		}

		public Builder setNotLocked(boolean isNotLocked) {
			this.isNotLocked = isNotLocked;
			return this;
		}

		public User build() {
			return new User(this);
		}
	}

}
