package de.noltarium.keenio.gateway.util;

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class JSonDotRemoverFromKeyTest {

	@Test
	public void testName() throws Exception {
		Resource resource = new ClassPathResource("travis-webhook-payload.json");
		String publicKeyAsString = FileUtils.readFileToString(resource.getFile(), "UTF-8");

		publicKeyAsString = publicKeyAsString.replaceAll("\"\\.", "\"");

		Resource resourceRemovedDots = new ClassPathResource("travis-webhook-payload-reverted-dots.json");
		String travisPayloadWithoutDots = FileUtils.readFileToString(resourceRemovedDots.getFile(), "UTF-8");

		assertEquals(travisPayloadWithoutDots, publicKeyAsString);

	}

}
