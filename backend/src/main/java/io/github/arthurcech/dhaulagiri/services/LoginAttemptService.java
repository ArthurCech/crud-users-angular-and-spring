package io.github.arthurcech.dhaulagiri.services;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class LoginAttemptService {

	private static final int MAX_ATTEMPTS = 5;
	private static final int INCREMENT = 1;

	private LoadingCache<String, Integer> loginCache;

	public LoginAttemptService() {
		loginCache = CacheBuilder.newBuilder().expireAfterWrite(15, TimeUnit.MINUTES)
				.maximumSize(100).build(new CacheLoader<String, Integer>() {
					@Override
					public Integer load(String key) throws Exception {
						return 0;
					}
				});
	}

	public void removeUserFromLoginCache(String username) {
		loginCache.invalidate(username);
	}

	public void addUserToLoginCache(String username) {
		int attempts = 0;
		try {
			attempts = INCREMENT + loginCache.get(username);
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		loginCache.put(username, attempts);
	}

	public boolean userExceededAttempts(String username) {
		try {
			return loginCache.get(username) >= MAX_ATTEMPTS;
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}

}
