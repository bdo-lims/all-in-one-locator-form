package org.itech.locator.form.webapp.api.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class Contact {
	
	//@NotBlank
	@Size(max = 255)
	private String lastName;
	
	//@NotBlank
	@Size(max = 255)
	private String firstName;
	
	//@NotBlank
	@Size(max = 255)
	private String address;
	
	//	@NotBlank
	@Size(max = 255)
	private String country;
	
	//@NotBlank
	@Size(max = 21)
	private String mobilePhone;

	@Size(max = 21)
	private String email;
}
