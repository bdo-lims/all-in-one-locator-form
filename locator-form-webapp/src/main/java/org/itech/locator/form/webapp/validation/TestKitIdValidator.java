package org.itech.locator.form.webapp.validation;

import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.itech.locator.form.webapp.config.ValidationConfigProperties;
import org.itech.locator.form.webapp.validation.annotation.TestKitId;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestKitIdValidator implements ConstraintValidator<TestKitId, String> {

	@Autowired
	private ValidationConfigProperties validationConfigProperties;
	private Pattern testKitIdPattern;

	@PostConstruct
	public void initialize() {
		testKitIdPattern = Pattern.compile(validationConfigProperties.getTestKitIdRegex());
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		if (StringUtils.isAllEmpty(value)) {
			return true;
		}
		return testKitIdPattern.matcher(value).matches();
	}

}
