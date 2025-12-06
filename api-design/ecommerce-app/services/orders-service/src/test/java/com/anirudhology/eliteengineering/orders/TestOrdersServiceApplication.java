package com.anirudhology.eliteengineering.orders;

import org.springframework.boot.SpringApplication;

public class TestOrdersServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
