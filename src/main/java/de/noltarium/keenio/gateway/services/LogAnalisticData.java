package de.noltarium.keenio.gateway.services;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import io.keen.client.java.JavaKeenClientBuilder;
import io.keen.client.java.KeenClient;
import io.keen.client.java.KeenProject;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("log")
public class LogAnalisticData implements AnalyticsStorage {

	@Override
	public void pushData(Map<String, Object> content) {
		log.debug("new data");
	}

}
