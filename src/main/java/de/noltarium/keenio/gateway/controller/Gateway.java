package de.noltarium.keenio.gateway.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.noltarium.keenio.gateway.services.AnalyticsStorage;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/build")
@Slf4j
public class Gateway {

	@Autowired
	private AnalyticsStorage analyticsStorage;

	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
			MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public void receiveTravisCINotification(@RequestBody LinkedHashMap<String, Object> analisticData) {
		analyticsStorage.pushData(analisticData);
	}

}
