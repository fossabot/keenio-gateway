package de.noltarium.keenio.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class RunnerApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RunnerApplication.class, args);
	}

}
