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
@Profile("keenio")
public class KeenIOConnectionServiceImpl implements AnalyticsStorage {

	@Value("keenio.project")
	String projectId;

	@Value("keenio.readkey")
	String readkey;

	@Value("keenio.writekey")
	String writekey;
	
	@Value("keenio.defaultEventCollection")
	String defaultEventCollection;
	

	@PostConstruct
	public void setupKeenIOClient() {
		log.debug("init keen client");
		KeenClient client = new JavaKeenClientBuilder().build();
		KeenProject project = new KeenProject(projectId, writekey, readkey);
		client.setDefaultProject(project);
		KeenClient.initialize(client);
	}

	@Override
	public void pushData(Map<String, Object> content) {
		pushData(defaultEventCollection, content);
	}
	
	public void pushData(String eventCollection, Map<String, Object> content) {
		KeenClient.client().addEvent(eventCollection, content);
	}

}
