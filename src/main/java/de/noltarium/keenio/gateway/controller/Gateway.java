package de.noltarium.keenio.gateway.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.noltarium.keenio.gateway.services.AnalyticsStorage;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/build")
@Slf4j
public class Gateway {

	@Autowired
	private AnalyticsStorage analyticsStorage;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	@PreAuthorize("hasRole('ROLE_TRAVISCI')")
	public void receiveTravisCINotification(@RequestBody MultiValueMap<String, Object> analisticData)
			throws JsonParseException, JsonMappingException, IOException {
		log.debug("receive new travis ci notification");

		// get the travis ci payload from requst
		List<Object> payload = analisticData.get("payload");

		// convert the json payload to a generic hash map
		String object = (String) payload.get(0);

		object = object.replaceAll("\"\\.", "\"");

		HashMap<String, Object> result = new ObjectMapper().readValue(object, HashMap.class);

		log.debug("convert payload");
		analyticsStorage.pushData(result);
	}

}
