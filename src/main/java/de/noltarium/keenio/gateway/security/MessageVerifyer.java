package de.noltarium.keenio.gateway.security;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class MessageVerifyer {

	public static boolean verify(PublicKey publicKey, String data, byte[] decoded) {
		try {
			Signature signature = Signature.getInstance("SHA1withRSA");
			signature.initVerify(publicKey);
			signature.update(data.getBytes());
			boolean isSigValid = signature.verify(decoded);
			return isSigValid;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			throw new RuntimeException(e);
		}

	}

}
