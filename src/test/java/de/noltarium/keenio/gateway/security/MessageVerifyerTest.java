package de.noltarium.keenio.gateway.security;

import static org.junit.Assert.assertTrue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

import org.junit.Test;

public class MessageVerifyerTest {

	@Test
	public void test() throws Exception {

		// generate a key pair for the test
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(512);
		KeyPair genKeyPair = keyGen.genKeyPair();

		String originalMessage = "hi this is Visruth here";

		byte[] data = originalMessage.getBytes("UTF-8");
		Signature mySig = Signature.getInstance("SHA1withRSA");
		mySig.initSign(genKeyPair.getPrivate());
		mySig.update(data);
		byte[] sigBytes = mySig.sign();

		// DigestUtils.sha1
		assertTrue(MessageVerifyer.verify(genKeyPair.getPublic(), originalMessage, sigBytes));

	}

}
