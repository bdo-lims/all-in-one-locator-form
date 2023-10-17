package org.itech.locator.form.webapp.security.recaptcha.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.itech.locator.form.webapp.security.recaptcha.RecaptchaUtil;
import org.itech.locator.form.webapp.security.recaptcha.service.RecaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RecaptchaServiceImpl implements RecaptchaService {

	@Value("${google.recaptcha.secret}")
	String recaptchaSecret;

	private static final String GOOGLE_RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean verifyRecaptcha(String ip, String recaptchaResponse) {
		Map<String, String> body = new HashMap<>();
		body.put("secret", recaptchaSecret);
		body.put("response", recaptchaResponse);
		body.put("remoteip", ip);
		log.debug("Request body for recaptcha: {}", body);
		ResponseEntity<Map> recaptchaResponseEntity = restTemplateBuilder.build().postForEntity(
				GOOGLE_RECAPTCHA_VERIFY_URL + "?secret={secret}&response={response}&remoteip={remoteip}", body,
				Map.class, body);

		log.debug("Response from recaptcha: {}", recaptchaResponseEntity);
		Map<String, Object> responseBody = recaptchaResponseEntity.getBody();

		boolean recaptchaSucess = (Boolean) responseBody.get("success");
		if (!recaptchaSucess) {
			List<String> errorCodes = (List) responseBody.get("error-codes");

			String errorMessage = errorCodes.stream().map(s -> RecaptchaUtil.RECAPTCHA_ERROR_CODE.get(s))
					.collect(Collectors.joining(", "));

			log.debug(errorMessage);
			return false;
		} else {
			return true;
		}
	}
}
