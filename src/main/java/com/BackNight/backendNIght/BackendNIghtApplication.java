package com.BackNight.backendNIght;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.BackNight.backendNIght.ws")
public class 	BackendNIghtApplication {
	public static void main(String[] args) {
		SpringApplication.run(BackendNIghtApplication.class, args);
	}
}
