package org.itech.locator.form.webapp.api.dto;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.itech.locator.form.webapp.validation.annotation.OneOf;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocatorFormDTO extends Traveller {
	
	public enum Title {
		
		MR,
		MRS,
		MS,
		MISS,
		DR,
		OTHER;
		
		@Override
		@JsonValue
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public enum VisitPurpose {
		
		CITIZEN,
		CREW_FOR_RESIDENT,
		BUSINESS,
		STUDY,
		WEDDING,
		VISIT_HOLIDAY,
		SPORT,
		SPOUSE_OF_MAURITIAN,
		RESIDENT_PERMIT_HOLDER,
		OCCUPATION_PERMIT_HOLDER;
		
		@Override
		@JsonValue
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public enum TravellerType {
		
		RESIDENT,
		NONRESIDENT;
		
		@Override
		@JsonValue
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public enum Stage {
		
		PASSENGER,
		PIO,
		HEALTH_DESK;
		
		@Override
		@JsonValue
		public String toString() {
			return this.name().toLowerCase();
		}
	}
	
	public String taskId;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean vaccinated;
	
	@OneOf(resourcePath = "vaccines.js")
	private String firstVaccineName;
	
	@OneOf(resourcePath = "vaccines.js")
	private String secondVaccineName;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfFirstDose;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfSecondDose;
	
	@NotNull
	private TravellerType travellerType;
	
	@NotBlank
	@Size(max = 255)
	private String airlineName;
	
	@NotBlank
	@Size(max = 255)
	private String flightNumber;
	
	@NotNull
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate arrivalDate;
	
	@JsonFormat(pattern = "HH:mm")
	private LocalTime arrivalTime;
	
	@NotNull
	private Title title;
	
	private Integer lengthOfStay;
	
	@Valid
	private List<@OneOf(resourcePath = "countries.js") String> countriesVisited;
	
	@Size(max = 255)
	private String portOfEmbarkation;
	
	@Size(max = 255)
	private String profession;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean hadCovidBefore;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean fever;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean soreThroat;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean jointPain;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean cough;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean breathingDifficulties;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean rash;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean smellOrTaste;
	
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean contact;
	
	@NotNull
	private VisitPurpose visitPurpose;
	
	@Size(max = 21)
	private String mobilePhone;
	
	@Size(max = 21)
	private String fixedPhone;
	
	@Size(max = 21)
	private String businessPhone;
	
	@NotBlank
	@Size(max = 255)
	@Email
	private String confirmEmail;
	
	@NotBlank
	@Size(max = 255)
	@Email
	private String email;
	
	@Size(max = 255)
	private String nationalID;
	
	@Valid
	private List<@OneOf(resourcePath = "nationality.js") String> passengerNationality;
	
	@Valid
	private Address permanentAddress;
	
	@Valid
	private Address temporaryAddress;
	
	@Valid
	private Contact emergencyContact;
	
	@Valid
	private Contact contactPerson;
	
	@Valid
	private Traveller[] familyTravelCompanions;
	
	@Valid
	private Traveller[] nonFamilyTravelCompanions;
	
	@NotNull
	@JsonSerialize(using = StringBooleanSerializer.class)
	private Boolean acceptedTerms;
	
	private Boolean hotelSearchCheck;
	
	private Boolean flightNumberCheck;
	
	private Boolean finalized;
	
	private Stage stage;
	
	public static class StringBooleanSerializer extends JsonSerializer<Boolean> {
		
		@Override
		public void serialize(Boolean bool, JsonGenerator generator, SerializerProvider provider)
		        throws IOException, JsonProcessingException {
			generator.writeString(bool == null ? "false" : bool.toString());
		}
	}
}
