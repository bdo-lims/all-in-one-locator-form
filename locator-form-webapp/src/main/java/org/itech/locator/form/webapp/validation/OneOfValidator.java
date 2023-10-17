package org.itech.locator.form.webapp.validation;

import java.io.IOException;
import java.util.Arrays;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.itech.locator.form.webapp.bean.ValueLabelPair;
import org.itech.locator.form.webapp.validation.annotation.OneOf;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OneOfValidator implements ConstraintValidator<OneOf, String> {
	ValueLabelPair[] pairs;

	@Override
	public void initialize(OneOf oneOf) {
		ObjectMapper mapper = new ObjectMapper();
		ClassLoader cLoader = this.getClass().getClassLoader();
		try {
			mapper.configure(Feature.ALLOW_COMMENTS, true);
			pairs = mapper.readValue(cLoader.getResourceAsStream(oneOf.resourcePath()), ValueLabelPair[].class);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return StringUtils.isEmpty(value) || Arrays.stream(pairs).anyMatch(e -> value.equals(e.getValue()));
	}

}
