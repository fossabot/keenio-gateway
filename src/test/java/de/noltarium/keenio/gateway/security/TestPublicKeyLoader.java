package de.noltarium.keenio.gateway.security;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("test")
public class TestPublicKeyLoader implements PublicKeyLoader {

	private KeyPair genKeyPair;

	public TestPublicKeyLoader() throws NoSuchAlgorithmException {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		genKeyPair = keyGen.genKeyPair();
	}

	@Override
	public PublicKey loadPublicKey() {
		return genKeyPair.getPublic();
	}

	public PrivateKey loadPrivate() {
		return genKeyPair.getPrivate();
	}
}
