package com.outlookmail.mailapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailappApplication.class, args);
	}

}
