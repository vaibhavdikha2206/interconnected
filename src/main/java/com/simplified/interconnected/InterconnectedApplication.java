package com.simplified.interconnected;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class InterconnectedApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterconnectedApplication.class, args);
	}

}
