package com.kinde.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity(securedEnabled = true)
public class KindeClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(KindeClientApplication.class, args);
	}

}
