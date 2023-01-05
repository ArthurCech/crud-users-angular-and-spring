package io.github.arthurcech.dhaulagiri;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import io.github.arthurcech.dhaulagiri.constants.FileConstant;

@SpringBootApplication
public class DhaulagiriApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhaulagiriApplication.class, args);
		new File(FileConstant.USER_FOLDER).mkdirs();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
		corsConfiguration.setAllowedHeaders(
				Arrays.asList("Origin", "Access-Control-Allow-Origin", "Content-Type", "Accept",
						"access_token", "Authorization", "Origin, Accept", "X-Requested-With",
						"Access-Control-Request-Method", "Access-Control-Request-Headers"));
		corsConfiguration.setExposedHeaders(
				Arrays.asList("Origin", "Content-Type", "Accept", "access_token", "Authorization",
						"Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
		corsConfiguration
				.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

}
