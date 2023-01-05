package io.github.arthurcech.dhaulagiri;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.arthurcech.dhaulagiri.constants.FileConstant;

@SpringBootApplication
public class DhaulagiriApplication {

	public static void main(String[] args) {
		SpringApplication.run(DhaulagiriApplication.class, args);
		new File(FileConstant.USER_FOLDER).mkdirs();
	}

}
