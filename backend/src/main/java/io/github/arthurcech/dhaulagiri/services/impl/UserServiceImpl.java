package io.github.arthurcech.dhaulagiri.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.arthurcech.dhaulagiri.constants.ServiceConstant;
import io.github.arthurcech.dhaulagiri.entities.User;
import io.github.arthurcech.dhaulagiri.entities.UserPrincipal;
import io.github.arthurcech.dhaulagiri.repositories.UserRepository;
import io.github.arthurcech.dhaulagiri.services.UserService;
import jakarta.transaction.Transactional;

@Service
@Transactional
@Qualifier("userDetailsService")
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository repository;

	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
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

}
