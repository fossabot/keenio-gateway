package de.noltarium.keenio.gateway.controller;

import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@RequestMapping(method = RequestMethod.POST)
	public void getExecutionTasks(@RequestBody LinkedHashMap<String, Object> analisticData) {		
		analyticsStorage.pushData(analisticData);		
	}

}
