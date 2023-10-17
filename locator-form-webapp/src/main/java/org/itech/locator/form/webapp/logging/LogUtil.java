package org.itech.locator.form.webapp.logging;

import org.owasp.encoder.Encode;

public class LogUtil {

	public static String sanitizeUntrustedInputMessageForLog(String message) {
		message = message.replace('\n', '_').replace('\r', '_').replace('\t', '_');
		message = Encode.forHtml(message);
		return message;
	}

}
