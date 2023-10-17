package org.itech.locator.form.webapp.databind;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class CapitalizeDeserializer extends StdDeserializer<String> {

	private static final long serialVersionUID = -7568566786480622911L;

	public CapitalizeDeserializer() {
		super(String.class);
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String value = p.readValueAs(String.class);
		return StringUtils.isBlank(value) ? value : value.toUpperCase();
	}

}
