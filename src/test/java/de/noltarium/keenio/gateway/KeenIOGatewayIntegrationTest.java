package de.noltarium.keenio.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import de.noltarium.keenio.gateway.security.TestPublicKeyLoader;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test", "log" })
public class KeenIOGatewayIntegrationTest {
	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	TestPublicKeyLoader keyLoader;

	@Test
	public void askForHealthNotAuthenticatedRequestButIsUnsecureURL() {
		HttpEntity<?> entity = new HttpEntity<Object>("");
		ResponseEntity<String> response = restTemplate.exchange("/health", HttpMethod.GET, entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void validRequest() throws Exception {

		HttpHeaders headers = prepareRequestHeaders();
		String payload = loadTravisCIWebhookPayload();
		appendSignatureFromPayloadToHeaders(headers, payload);
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("payload", payload);
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange("/build", HttpMethod.POST, entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void missingSignatureHeader() {

		HttpHeaders headers = prepareRequestHeaders();
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("payload", "");
		HttpEntity<Object> entity = new HttpEntity<Object>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange("/build", HttpMethod.POST, entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void invalidSignatureHeaderStructure() {

		HttpHeaders headers = prepareRequestHeaders();
		headers.add("Signature", "fooodasdas");
		HttpEntity<String> entity = new HttpEntity<String>("foo", headers);
		ResponseEntity<String> response = restTemplate.exchange("/build", HttpMethod.POST, entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void invalidContentNotMatchWithSignature() throws Exception {

		HttpHeaders headers = prepareRequestHeaders();
		String payload = loadTravisCIWebhookPayload();
		appendSignatureFromPayloadToHeaders(headers, payload);

		// change the payload, the sha check shoud not match
		payload += "_changed";

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("payload", payload);
		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		ResponseEntity<String> response = restTemplate.exchange("/build", HttpMethod.POST, entity, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	private void appendSignatureFromPayloadToHeaders(HttpHeaders headers, String payload)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		byte[] data = payload.getBytes("UTF-8");
		Signature mySig = Signature.getInstance("SHA1withRSA");
		mySig.initSign(keyLoader.loadPrivate());
		mySig.update(data);
		byte[] sigBytes = mySig.sign();
		headers.add("Signature", Base64.encodeBase64String(sigBytes));
	}

	private String loadTravisCIWebhookPayload() throws IOException {
		Resource resource = new ClassPathResource("travis-webhook-payload.json");
		String payload = FileUtils.readFileToString(resource.getFile(), "UTF-8");
		return payload;
	}

	private HttpHeaders prepareRequestHeaders() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		return headers;
	}
}
