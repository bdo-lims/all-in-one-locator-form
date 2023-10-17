package org.itech.locator.form.webapp.security.recaptcha.service;

public interface RecaptchaService {

	public enum RecaptchaVerifyResult {
		VALID("Recaptcha is valid"), MISSING_INPUT_SECRET("The secret parameter is missing"),
		INVALID_INPUT_SECRET("The secret parameter is invalid or malformed"),
		MISSING_INPUT_RESPONSE("The response parameter is missing"),
		INVALID_INPUT_RESPONSE("The response parameter is invalid or malformed"),
		BAD_REQUEST("The request is invalid or malformed");

		private String message;

		RecaptchaVerifyResult(String message) {
			this.message = message;
		}

		public String getMessage() {
			return message;
		}
	}

	boolean verifyRecaptcha(String ip, String recaptchaResponse);

}
