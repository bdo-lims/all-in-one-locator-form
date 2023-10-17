package org.itech.locator.form.webapp.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.DatatypeConverter;

public class SecurityUtil {

	private static String bytesToHex(byte[] hash) {
		return DatatypeConverter.printHexBinary(hash);
	}

	public static String getSHA256Hash(String valueToHash) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(valueToHash.getBytes());
		return bytesToHex(md.digest());
	}
}
