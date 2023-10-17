package org.itech.locator.form.webapp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.itech.locator.form.webapp.country.Country;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocatorFormUtil {

    private static Country[] countries;

    private static Country[] getCountries() {
		if (countries == null) {
			ObjectMapper mapper = new ObjectMapper();
			ClassLoader cLoader = LocatorFormUtil.class.getClassLoader();
			try {
				countries = mapper.readValue(cLoader.getResourceAsStream("countries.js"), Country[].class);
			} catch (IOException e) {
				log.error("could not parse countries file, using values instead of label");
				countries = new Country[0];
			}
		}
		return countries;
	}

	public static String getCountryLabelForValue(String value) {
		if (value == null) {
			return null;
		}
		for (Country country : getCountries()) {
			if (country.getValue().equals(value)) {
				return country.getLabel();
			}
		}
		return value;
	}
    
}
