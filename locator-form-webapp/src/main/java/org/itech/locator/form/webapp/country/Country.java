package org.itech.locator.form.webapp.country;

import org.itech.locator.form.webapp.bean.ValueHolder;

import lombok.Data;

@Data
public class Country implements ValueHolder {

	private String value;
	private String label;
}
