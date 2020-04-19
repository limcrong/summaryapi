package com.summary.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SummaryapiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SummaryapiApplication.class, args);
	}


}
