package org.itech.locator.form.webapp.summary.security;

import lombok.Data;

@Data
public class SummaryAccessInfo {

	private String id;
	private String pass;

	public SummaryAccessInfo(String id, String pass) {
		this.id = id;
		this.pass = pass;
	}

}
