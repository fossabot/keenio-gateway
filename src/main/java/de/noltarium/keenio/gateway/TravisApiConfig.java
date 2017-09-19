package de.noltarium.keenio.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TravisApiConfig {
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
}
