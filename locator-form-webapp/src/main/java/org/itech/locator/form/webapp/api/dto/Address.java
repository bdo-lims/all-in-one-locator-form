package org.itech.locator.form.webapp.api.dto;

import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class Address {
	
	@Size(max = 255)
	private String hotelName;
	
	@Size(max = 255)
	private String numberAndStreet;
	
	@Size(max = 255)
	private String apartmentNumber;
	
	@Size(max = 255)
	private String city;
	
	@Size(max = 255)
	private String stateProvince;
	
	@Size(max = 255)
	private String country;
	
	@Size(max = 255)
	private String zipPostalCode;
	
	@Size(max = 21)
	private String localPhone;

	@Size(max = 255)
	private String quarantineSite;
}
