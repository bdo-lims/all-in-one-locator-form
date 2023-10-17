package org.itech.locator.form.webapp.api.dto;

import java.time.LocalDate;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.itech.locator.form.webapp.databind.CapitalizeDeserializer;
import org.itech.locator.form.webapp.validation.annotation.OneOf;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

@Data
public class Traveller {

	public enum Sex {

		MALE,
		FEMALE,
		OTHER,
		UNKNOWN;

		@Override
		@JsonValue
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	private String subTaskId;

	private String serviceRequestId;

	private String patientId;

	private String specimenId;

	private String questionnaireResponseId;

	@NotBlank
	@Size(max = 255)
	@JsonDeserialize(using = CapitalizeDeserializer.class)
	private String lastName;

	@NotBlank
	@Size(max = 255)
	@JsonDeserialize(using = CapitalizeDeserializer.class)
	private String firstName;

	@Size(max = 3)
	@JsonDeserialize(using = CapitalizeDeserializer.class)
	private String middleInitial;

	@Size(max = 15)
	private String seatNumber;

	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;

	@NotNull
	private Sex sex;

	@Size(max = 255)
	private String nationality;

	@OneOf(resourcePath = "countries.js")
	private String countryOfBirth;

	@OneOf(resourcePath = "countries.js")
	private String countryOfPassportIssue;

	@Size(max = 255)
	@JsonDeserialize(using = CapitalizeDeserializer.class)
	private String passportNumber;

	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate passportExpiryDate;

	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	private Date formSubmitionDateTime;
}
