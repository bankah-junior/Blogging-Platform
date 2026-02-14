package com.amalitech.SpringBootBloggingApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBootBloggingAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootBloggingAppApplication.class, args);
	}

}
