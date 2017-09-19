package de.noltarium.keenio.gateway.security;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.PublicKey;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(MockitoJUnitRunner.class)
public class TravisCiConfigLoaderTest {

	@InjectMocks
	TravisCiConfigLoader loader;

	@Mock
	private RestTemplate restTemplate;

	@Before
	public void setUp() throws IOException {
		loader = new TravisCiConfigLoader(restTemplate);
		Resource resource = new ClassPathResource("travis-config.json");
		String publicKeyAsString = FileUtils.readFileToString(resource.getFile(), "UTF-8");

		HashMap<String, Object> result = new ObjectMapper().readValue(publicKeyAsString, HashMap.class);

		Mockito.when(restTemplate.getForObject(Mockito.anyString(), Matchers.any(Class.class))).thenReturn(result);

	}

	@Test
	public void convertStringToPublicKey() throws Exception {

		PublicKey key = loader.loadPublicKey();
		assertNotNull(key);
	}

}
