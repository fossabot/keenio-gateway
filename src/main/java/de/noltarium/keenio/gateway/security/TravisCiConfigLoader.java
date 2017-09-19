package de.noltarium.keenio.gateway.security;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Profile("production")
public class TravisCiConfigLoader implements PublicKeyLoader {

	private RestTemplate template;

	public TravisCiConfigLoader(@Autowired RestTemplate template) {
		this.template = template;
	}

	@Override
	public PublicKey loadPublicKey() {
		Map config = loadCurrentConfigFromTravisCi();
		String public_key = (String) ((Map) ((Map) config.get("notifications")).get("webhook")).get("public_key");

		try {
			return convertToPublicKey(public_key);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			log.error("can`t read tavisci public key", e);
			throw new RuntimeException(e);
		}
	}

	private Map loadCurrentConfigFromTravisCi() {
		Map travisConfig = template.getForObject("https://api.travis-ci.org/config", Map.class);
		Map config = (Map) travisConfig.get("config");
		return config;
	}

	private PublicKey convertToPublicKey(String public_key) throws NoSuchAlgorithmException, InvalidKeySpecException {

		// remove comment lines
		public_key = public_key.replace("-----BEGIN PUBLIC KEY-----\n", "");
		public_key = public_key.replace("-----END PUBLIC KEY-----\n", "");

		byte[] keyByteArray = Base64.decodeBase64(public_key);

		X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(keyByteArray);
		log.debug(pubKeySpec.getFormat());
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey pubKey = keyFactory.generatePublic(pubKeySpec);

		return pubKey;
	}

}
