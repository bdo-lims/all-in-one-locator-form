package org.itech.locator.form.webapp.api.dto;

import javax.validation.constraints.NotBlank;

import org.itech.locator.form.webapp.validation.annotation.Included;
import org.itech.locator.form.webapp.validation.annotation.OneOf;
import org.itech.locator.form.webapp.validation.annotation.TestKitId;

import lombok.Data;

@Data
public class HealthDeskDTO extends LocatorFormDTO {

	@TestKitId
	public String testKitId;

	@OneOf(resourcePath = "healthOffices.js")
	public String healthOffice;

	@NotBlank
	@Included(resourcePath = "localities.js")
	public String locality;
}
