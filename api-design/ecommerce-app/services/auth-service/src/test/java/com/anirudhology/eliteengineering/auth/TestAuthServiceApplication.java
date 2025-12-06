package com.anirudhology.eliteengineering.auth;

import org.springframework.boot.SpringApplication;

public class TestAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
