package com.anirudhology.eliteengineering.payments;

import org.springframework.boot.SpringApplication;

public class TestPaymentsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(Application::main).with(TestcontainersConfiguration.class).run(args);
	}

}
