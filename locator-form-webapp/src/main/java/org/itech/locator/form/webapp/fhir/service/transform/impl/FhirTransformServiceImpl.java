package org.itech.locator.form.webapp.fhir.service.transform.impl;

import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.Address.AddressType;
import org.hl7.fhir.r4.model.Address.AddressUse;
import org.hl7.fhir.r4.model.BooleanType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.r4.model.Bundle.BundleType;
import org.hl7.fhir.r4.model.Bundle.HTTPVerb;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.IntegerType;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Patient.ContactComponent;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.r4.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.r4.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Specimen.SpecimenStatus;
import org.hl7.fhir.r4.model.StringType;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.TimeType;
import org.hl7.fhir.r4.model.Task.TaskRestrictionComponent;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.itech.locator.form.webapp.api.LocatorFormUtil;
import org.itech.locator.form.webapp.api.dto.HealthDeskDTO;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.api.dto.Traveller;
import org.itech.locator.form.webapp.fhir.service.FhirConstants;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.itech.locator.form.webapp.security.SecurityUtil;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FhirTransformServiceImpl implements FhirTransformService {

	@Value("${org.itech.locator.form.loinccodes}")
	private String[] loincCodes;

	@Value("${org.itech.locator.form.filler.id}")
	private String fillerId; // the Organization that Tasks are being referred to

	@Value("${org.itech.locator.form.orderer.id}")
	private String ordererId; // the Organization that Tasks are being sent from

	@Value("${org.itech.locator.form.requester.person.id}")
	private String requesterId; // the Practitioner that Tasks are requested by

	@Value("${org.itech.locator.form.location.id}")
	private String locationId; // the Location that ServiceRequests are coming from

	@Value("${org.itech.locator.form.barcodelength:36}")
	private Integer barcodeLength;

	@Value("${org.openelisglobal.oe.fhir.system:http://openelis-global.org}")
	private String oeFhirSystem;

	@Value("${org.itech.locator.form.fhir.system:https://host.openelis.org/locator-form}")
	private String locatorFormFhirSystem;

	@Value("${org.itech.locator.form.fhir.system.test-kit:https://host.openelis.org/locator-form/test-kit}")
	private String testKitIdSystem;

	@Value("${org.itech.locator.form.questionnaire.id}")
	private String questionnaireId;

	private ObjectMapper objectMapper;

	public FhirTransformServiceImpl(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Override
	public TransactionObjects createTransactionObjects(LocatorFormDTO locatorFormDTO, boolean assignIds,
			TaskStatus status) throws JsonProcessingException {
		TransactionObjects transactionInfo = new TransactionObjects();
		Bundle transactionBundle = new Bundle();
		transactionBundle.setType(BundleType.TRANSACTION);
		transactionInfo.bundle = transactionBundle;

		if (assignIds) {
			locatorFormDTO.setTaskId(UUID.randomUUID().toString());
			locatorFormDTO.setSubTaskId(UUID.randomUUID().toString());
			locatorFormDTO.setServiceRequestId(UUID.randomUUID().toString());
			locatorFormDTO.setPatientId(UUID.randomUUID().toString());
			locatorFormDTO.setSpecimenId(UUID.randomUUID().toString());
			locatorFormDTO.setQuestionnaireResponseId(UUID.randomUUID().toString());
		}

		Task fhirTask = createFhirTask(locatorFormDTO, status);
		transactionBundle.addEntry(createTransactionBundleComponent(fhirTask));
		transactionInfo.task = fhirTask;

		ServiceRequestObjects fhirServiceRequestPatient = createFhirServiceRequestPatient(locatorFormDTO,
				locatorFormDTO, status);
		addServiceRequestPatientPairToTransaction(fhirServiceRequestPatient, transactionInfo);

		QuestionnaireResponse questionnaireResponse = createQuestionareResponse(locatorFormDTO, status);
		transactionBundle.addEntry(createTransactionBundleComponent(questionnaireResponse));
		transactionInfo.questionnaireResponse = questionnaireResponse;

		for (Traveller comp : locatorFormDTO.getFamilyTravelCompanions()) {
			if (assignIds) {
				comp.setServiceRequestId(UUID.randomUUID().toString());
				comp.setPatientId(UUID.randomUUID().toString());
				comp.setSubTaskId(UUID.randomUUID().toString());
				comp.setSpecimenId(UUID.randomUUID().toString());
			}
			fhirServiceRequestPatient = createFhirServiceRequestPatient(locatorFormDTO, comp, status);
			addServiceRequestPatientPairToTransaction(fhirServiceRequestPatient, transactionInfo);

			questionnaireResponse = createQuestionareResponse(locatorFormDTO, status);
			transactionBundle.addEntry(createTransactionBundleComponent(questionnaireResponse));
		}

		for (Traveller comp : locatorFormDTO.getNonFamilyTravelCompanions()) {
			if (assignIds) {
				comp.setServiceRequestId(UUID.randomUUID().toString());
				comp.setPatientId(UUID.randomUUID().toString());
				comp.setSubTaskId(UUID.randomUUID().toString());
				comp.setSpecimenId(UUID.randomUUID().toString());
			}
			fhirServiceRequestPatient = createFhirServiceRequestPatient(locatorFormDTO, comp, status);
			addServiceRequestPatientPairToTransaction(fhirServiceRequestPatient, transactionInfo);

			questionnaireResponse = createQuestionareResponse(locatorFormDTO, status);
			transactionBundle.addEntry(createTransactionBundleComponent(questionnaireResponse));
		}

//		if (transactionInfo.serviceRequestPatientPairs.size() == 1) {
//			transactionInfo.task = transactionInfo.serviceRequestPatientPairs.get(0).task;
//			transactionInfo.task.setPartOf(null);
//		}

		// locatorFormDTO added at end so that any updated values (like
		// serviceRequestId) get added to the
		for (ServiceRequestObjects curServiceRequestObjects : transactionInfo.serviceRequestPatientPairs) {
			curServiceRequestObjects.task.setDescription(objectMapper.writeValueAsString(locatorFormDTO));
		}

		return transactionInfo;
	}

	private Task createFhirTask(LocatorFormDTO locatorFormDTO, TaskStatus status) {
		Task fhirTask = new Task();
		String taskId = locatorFormDTO.getTaskId();
		if (StringUtils.isBlank(taskId)) {
			taskId = UUID.randomUUID().toString();
		}
		fhirTask.setId(taskId);
		locatorFormDTO.setTaskId(taskId);

		fhirTask.setRequester(new Reference(requesterId));

		return fhirTask;
	}

	private void addServiceRequestPatientPairToTransaction(ServiceRequestObjects fhirServiceRequestPatient,
			TransactionObjects transactionInfo) {
		transactionInfo.bundle.addEntry(createTransactionBundleComponent(fhirServiceRequestPatient.serviceRequest));
		transactionInfo.bundle.addEntry(createTransactionBundleComponent(fhirServiceRequestPatient.task));
		transactionInfo.bundle.addEntry(createTransactionBundleComponent(fhirServiceRequestPatient.patient));
		transactionInfo.bundle.addEntry(createTransactionBundleComponent(fhirServiceRequestPatient.specimen));

		fhirServiceRequestPatient.task
				.addPartOf(new Reference(ResourceType.Task + "/" + transactionInfo.task.getIdElement().getIdPart()));
		transactionInfo.serviceRequestPatientPairs.add(fhirServiceRequestPatient);
	}

	private BundleEntryComponent createTransactionBundleComponent(Resource fhirResource) {
		ResourceType resourceType = fhirResource.getResourceType();
		String sourceResourceId = fhirResource.getIdElement().getIdPart();
		if (StringUtils.isNumeric(sourceResourceId)) {
			throw new IllegalArgumentException("id cannot be a number. Numbers are reserved for local entities only");
		}

		BundleEntryComponent transactionComponent = new BundleEntryComponent();
		transactionComponent.setResource(fhirResource);

		transactionComponent.getRequest().setMethod(HTTPVerb.PUT);
		transactionComponent.getRequest().setUrl(resourceType + "/" + sourceResourceId);

		return transactionComponent;
	}

	private Task createSubFhirTask(LocatorFormDTO locatorFormDTO, Traveller comp, TaskStatus status) {
		Task fhirTask = new Task();
		String taskId = comp.getSubTaskId();
		if (StringUtils.isBlank(taskId)) {
			taskId = UUID.randomUUID().toString();
		}
		fhirTask.setId(taskId);
		comp.setSubTaskId(taskId);

		Identifier identifier = new Identifier();
		identifier.setId(taskId);
		identifier.setSystem(locatorFormFhirSystem);
		fhirTask.setStatus(status);
		List<Identifier> identifierList = new ArrayList<>();
		identifierList.add(identifier);

		fhirTask.setIdentifier(identifierList);
		fhirTask.setAuthoredOn(new Date());
		fhirTask.setRequester(new Reference(requesterId));
		fhirTask.setOwner(new Reference(fillerId));
		fhirTask.setRestriction(new TaskRestrictionComponent().addRecipient(new Reference(ordererId)));

		return fhirTask;
	}

	private ServiceRequestObjects createFhirServiceRequestPatient(LocatorFormDTO locatorFormDTO, Traveller comp,
			TaskStatus status) {
		// patient is created here and used for SR subjectRef
		Patient fhirPatient = createFhirPatient(locatorFormDTO, comp);
		// patient is created here and used for SR subjectRef
		Specimen specimen = createSpecimen(locatorFormDTO, comp);
		// patient is created here and used for SR subjectRef
		Task task = createSubFhirTask(locatorFormDTO, comp, status);

		ServiceRequest serviceRequest = new ServiceRequest();
		String serviceRequestId = comp.getServiceRequestId();
		if (StringUtils.isBlank(serviceRequestId)) {
			serviceRequestId = UUID.randomUUID().toString();
		}
		serviceRequest.setId(serviceRequestId);
		serviceRequest.addIdentifier(new Identifier().setSystem(locatorFormFhirSystem).setValue(serviceRequestId));
		comp.setServiceRequestId(serviceRequestId);

		if (locatorFormDTO instanceof HealthDeskDTO) {
			HealthDeskDTO healthDeskDto = (HealthDeskDTO) locatorFormDTO;
			if (StringUtils.isNotBlank(healthDeskDto.getTestKitId())) {
				serviceRequest.addIdentifier(
						new Identifier().setSystem(testKitIdSystem).setValue(healthDeskDto.getTestKitId()));
			}
		}

		CodeableConcept codeableConcept = new CodeableConcept();
		for (String loincCode : loincCodes) {
			codeableConcept.addCoding(new Coding().setCode(loincCode).setSystem("http://loinc.org"));
		}
		serviceRequest.setCode(codeableConcept);

		serviceRequest.setSubject(new Reference(ResourceType.Patient + "/" + fhirPatient.getIdElement().getIdPart()));
		serviceRequest.addSpecimen(new Reference(ResourceType.Specimen + "/" + specimen.getIdElement().getIdPart()));
		serviceRequest.setRequester(new Reference(requesterId));
		serviceRequest.addLocationReference(new Reference(locationId));
		serviceRequest.setAuthoredOn(new Date());

		specimen.addRequest(
				new Reference(ResourceType.ServiceRequest + "/" + serviceRequest.getIdElement().getIdPart()));

		task.addBasedOn(new Reference(ResourceType.ServiceRequest + "/" + serviceRequest.getIdElement().getIdPart()));
		return new ServiceRequestObjects(task, serviceRequest, fhirPatient, specimen);

	}

	private Specimen createSpecimen(LocatorFormDTO locatorFormDTO, Traveller comp) {
		Specimen specimen = new Specimen();
		String specimenId = comp.getSpecimenId();
		if (StringUtils.isBlank(specimenId)) {
			specimenId = UUID.randomUUID().toString();
		}
		specimen.setId(specimenId);
		comp.setSpecimenId(specimenId);

		specimen.setReceivedTime(new Date());
		specimen.setType(new CodeableConcept());
		specimen.setStatus(SpecimenStatus.AVAILABLE);

		return specimen;
	}

	private Patient createFhirPatient(LocatorFormDTO locatorFormDTO, Traveller comp) {
		Patient fhirPatient = new Patient();
		String patientId = comp.getPatientId();
		if (StringUtils.isBlank(patientId)) {
			patientId = UUID.randomUUID().toString();
		}
		fhirPatient.setId(patientId);
		comp.setPatientId(patientId);

		HumanName humanName = new HumanName();
		List<HumanName> humanNameList = new ArrayList<>();
		humanName.setFamily(comp.getLastName());
		humanName.addGiven(comp.getFirstName());
		humanNameList.add(humanName);
		fhirPatient.setName(humanNameList);

		fhirPatient.addIdentifier(new Identifier().setSystem(oeFhirSystem + "/pat_guid").setValue(patientId));
		fhirPatient.addIdentifier(
				(Identifier) new Identifier().setSystem(locatorFormFhirSystem).setValue(patientId).setId(patientId));
		fhirPatient.addIdentifier((Identifier) new Identifier().setSystem("passport").setValue(comp.getPassportNumber())
				.setId(comp.getPassportNumber()));

		if (!StringUtils.isAllBlank(locatorFormDTO.getNationalID()) && comp == locatorFormDTO) {
			fhirPatient.addIdentifier((Identifier) new Identifier().setSystem("http://govmu.org")
					.setValue(locatorFormDTO.getNationalID()).setId(locatorFormDTO.getNationalID()));
		}

		fhirPatient.setBirthDate(Date.from(comp.getDateOfBirth().atStartOfDay(ZoneId.systemDefault()).toInstant()));
		switch (comp.getSex()) {
		case MALE:
			fhirPatient.setGender(AdministrativeGender.MALE);
			break;
		case FEMALE:
			fhirPatient.setGender(AdministrativeGender.FEMALE);
			break;
		case OTHER:
			fhirPatient.setGender(AdministrativeGender.OTHER);
			break;
		case UNKNOWN:
			fhirPatient.setGender(AdministrativeGender.UNKNOWN);
			break;
		}

		fhirPatient.addTelecom().setSystem(ContactPointSystem.SMS).setValue(locatorFormDTO.getMobilePhone());
		fhirPatient.addTelecom().setSystem(ContactPointSystem.PHONE).setValue(locatorFormDTO.getFixedPhone());
		fhirPatient.addTelecom().setSystem(ContactPointSystem.OTHER).setValue(locatorFormDTO.getBusinessPhone());
		fhirPatient.addTelecom().setSystem(ContactPointSystem.EMAIL).setValue(locatorFormDTO.getEmail());

		ContactComponent contact = fhirPatient.addContact();//
		HumanName contactName = new HumanName().addGiven(locatorFormDTO.getEmergencyContact().getFirstName())
				.setFamily(locatorFormDTO.getEmergencyContact().getLastName());
		contact.setName(contactName)//
				.addTelecom()//
				.setSystem(ContactPointSystem.SMS)//
				.setValue(locatorFormDTO.getEmergencyContact().getMobilePhone());

		Address permAddress = fhirPatient.addAddress();
		permAddress.setCity(locatorFormDTO.getPermanentAddress().getCity())//
				.setCountry(locatorFormDTO.getPermanentAddress().getCountry())//
				.setPostalCode(locatorFormDTO.getPermanentAddress().getZipPostalCode())//
				.setState(locatorFormDTO.getPermanentAddress().getStateProvince())//
				.addLine(locatorFormDTO.getPermanentAddress().getApartmentNumber())//
				.addLine(locatorFormDTO.getPermanentAddress().getNumberAndStreet())//
				.setUse(AddressUse.HOME)//
				.setType(AddressType.PHYSICAL);

		Address tempAddress = fhirPatient.addAddress();
		tempAddress.setCity(locatorFormDTO.getTemporaryAddress().getCity())//
				.setCountry(locatorFormDTO.getTemporaryAddress().getCountry())//
				.setPostalCode(locatorFormDTO.getTemporaryAddress().getZipPostalCode())//
				.setState(locatorFormDTO.getTemporaryAddress().getStateProvince())//
				.addLine(locatorFormDTO.getTemporaryAddress().getApartmentNumber())//
				.addLine(locatorFormDTO.getTemporaryAddress().getHotelName())//
				.addLine(locatorFormDTO.getTemporaryAddress().getNumberAndStreet())//
				.setUse(AddressUse.TEMP)//
				.setType(AddressType.PHYSICAL);

		return fhirPatient;
	}

	@Override
	public Map<SummaryAccessInfo, LabelContentPair> createLabelContentPair(@Valid LocatorFormDTO locatorFormDTO) {
		String hash;
		try {
			hash = SecurityUtil.getSHA256Hash(objectMapper.writeValueAsString(locatorFormDTO));
		} catch (NoSuchAlgorithmException | JsonProcessingException e) {
			log.error("could not create access token for summary for ServiceRequest: "
					+ locatorFormDTO.getServiceRequestId());
			hash = "";
		}
		Map<SummaryAccessInfo, LabelContentPair> labels = new HashMap<>();
//		List<LabelContentPair> idAndLabels = new ArrayList<>();
		String patientName = locatorFormDTO.getFirstName();
		String serviceRequestId = locatorFormDTO.getServiceRequestId();
		labels.put(new SummaryAccessInfo(serviceRequestId, hash), new LabelContentPair(
				patientName + "'s Service Identifier", StringUtils.substring(serviceRequestId, 0, barcodeLength)));
		for (Traveller traveller : locatorFormDTO.getFamilyTravelCompanions()) {
			patientName = traveller.getFirstName();
			serviceRequestId = traveller.getServiceRequestId();
			labels.put(new SummaryAccessInfo(serviceRequestId, hash), new LabelContentPair(
					patientName + "'s Service Identifier", StringUtils.substring(serviceRequestId, 0, barcodeLength)));
		}
		for (Traveller traveller : locatorFormDTO.getNonFamilyTravelCompanions()) {
			patientName = traveller.getFirstName();
			serviceRequestId = traveller.getServiceRequestId();
			labels.put(new SummaryAccessInfo(serviceRequestId, hash), new LabelContentPair(
					patientName + "'s Service Identifier", StringUtils.substring(serviceRequestId, 0, barcodeLength)));
		}
		return labels;
	}

	private QuestionnaireResponse createQuestionareResponse(@Valid LocatorFormDTO locatorFormDTO,
			TaskStatus taskStatus) {
		QuestionnaireResponse questionnaireResponse = new QuestionnaireResponse();
		String questionnaireResponseId = locatorFormDTO.getQuestionnaireResponseId();
		if (StringUtils.isBlank(questionnaireResponseId)) {
			questionnaireResponseId = UUID.randomUUID().toString();
		}
		locatorFormDTO.setQuestionnaireResponseId(questionnaireResponseId);
		questionnaireResponse.setId(questionnaireResponseId);
		if (taskStatus == TaskStatus.DRAFT) {
			questionnaireResponse.setStatus(QuestionnaireResponseStatus.INPROGRESS);
		} else if (taskStatus == TaskStatus.REQUESTED) {
			questionnaireResponse.setStatus(QuestionnaireResponseStatus.COMPLETED);
		}
		questionnaireResponse
				.addBasedOn(new Reference(ResourceType.ServiceRequest + "/" + locatorFormDTO.getServiceRequestId()));
		questionnaireResponse.setSubject(new Reference(ResourceType.Patient + "/" + locatorFormDTO.getPatientId()));
		questionnaireResponse
				.setIdentifier(new Identifier().setSystem(locatorFormFhirSystem).setValue(questionnaireResponseId));
		questionnaireResponse.setAuthored(new Date());
		questionnaireResponse.setQuestionnaire(questionnaireId);

		QuestionnaireResponseItemComponent seatItem = questionnaireResponse.addItem();
		seatItem.setLinkId(FhirConstants.SEAT_LINK_ID).setText("Seat");
		QuestionnaireResponseItemAnswerComponent seatAnswer = seatItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getSeatNumber())) {
			seatAnswer.setValue(new StringType(locatorFormDTO.getSeatNumber()));
		}

		QuestionnaireResponseItemComponent nationalityItem = questionnaireResponse.addItem();
		nationalityItem.setLinkId(FhirConstants.NATIONALITY_LINK_ID).setText("Nationality");
		for (String countryCode : locatorFormDTO.getPassengerNationality()) {
			String country = LocatorFormUtil.getCountryLabelForValue(countryCode);
			QuestionnaireResponseItemAnswerComponent nationalityAnswer = nationalityItem.addAnswer();
			nationalityAnswer.setValue(new StringType(country));
		}

		QuestionnaireResponseItemComponent airLineItem = questionnaireResponse.addItem();
		airLineItem.setLinkId(FhirConstants.AIRLINE_LINK_ID).setText("Airline");
		QuestionnaireResponseItemAnswerComponent airLineAnswer = airLineItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getAirlineName())) {
			airLineAnswer.setValue(new StringType(locatorFormDTO.getAirlineName()));
		}

		QuestionnaireResponseItemComponent flightItem = questionnaireResponse.addItem();
		flightItem.setLinkId(FhirConstants.FLIGHT_LINK_ID).setText("Flight");
		QuestionnaireResponseItemAnswerComponent flightAnswer = flightItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getFlightNumber())) {
			flightAnswer.setValue(new StringType(locatorFormDTO.getFlightNumber()));
		}

		QuestionnaireResponseItemComponent countriesVistedItem = questionnaireResponse.addItem();
		countriesVistedItem.setLinkId(FhirConstants.COUNTRIES_VISTED_LINK_ID)
				.setText("Countries Vistied within 6 Months");
		for (String countryCode : locatorFormDTO.getCountriesVisited()) {
			String country = LocatorFormUtil.getCountryLabelForValue(countryCode);
			QuestionnaireResponseItemAnswerComponent countriesVistedAnswer = countriesVistedItem.addAnswer();
			countriesVistedAnswer.setValue(new StringType(country));
		}

		QuestionnaireResponseItemComponent infectionItem = questionnaireResponse.addItem();
		infectionItem.setLinkId(FhirConstants.PREVIOUS_INFECTION_LINK_ID).setText("Previous Infection");
		QuestionnaireResponseItemAnswerComponent infectionAnswer = infectionItem.addAnswer();
		if (locatorFormDTO.getHadCovidBefore() != null) {
			infectionAnswer.setValue(new BooleanType(locatorFormDTO.getHadCovidBefore()));
		}

		QuestionnaireResponseItemComponent feverItem = questionnaireResponse.addItem();
		feverItem.setLinkId(FhirConstants.FEVER_LINK_ID).setText("Fever");
		QuestionnaireResponseItemAnswerComponent feverAnswer = feverItem.addAnswer();
		if (locatorFormDTO.getFever() != null) {
			feverAnswer.setValue(new BooleanType(locatorFormDTO.getFever()));
		}

		QuestionnaireResponseItemComponent soreThroatItem = questionnaireResponse.addItem();
		soreThroatItem.setLinkId(FhirConstants.SORE_THROAT_LINK_ID).setText("Sore Throat");
		QuestionnaireResponseItemAnswerComponent soreThroatAnswer = soreThroatItem.addAnswer();
		if (locatorFormDTO.getSoreThroat() != null) {
			soreThroatAnswer.setValue(new BooleanType(locatorFormDTO.getSoreThroat()));
		}

		QuestionnaireResponseItemComponent jointPainItem = questionnaireResponse.addItem();
		jointPainItem.setLinkId(FhirConstants.JOINT_PAIN_LINK_ID).setText("Joint Pain");
		QuestionnaireResponseItemAnswerComponent jointPainAnswer = jointPainItem.addAnswer();
		if (locatorFormDTO.getJointPain() != null) {
			jointPainAnswer.setValue(new BooleanType(locatorFormDTO.getJointPain()));
		}
		
		QuestionnaireResponseItemComponent coughItem = questionnaireResponse.addItem();
		coughItem.setLinkId(FhirConstants.COUGH_LINK_ID).setText("Cough");
		QuestionnaireResponseItemAnswerComponent coughAnswer = coughItem.addAnswer();
		if (locatorFormDTO.getCough() != null) {
			coughAnswer.setValue(new BooleanType(locatorFormDTO.getCough()));
		}

		QuestionnaireResponseItemComponent breathingDifficultyItem = questionnaireResponse.addItem();
		breathingDifficultyItem.setLinkId(FhirConstants.BREATHING_LINK_ID).setText("Breathing Difficulty");
		QuestionnaireResponseItemAnswerComponent breathingDifficultyAnswer = breathingDifficultyItem.addAnswer();
		if (locatorFormDTO.getBreathingDifficulties() != null) {
			breathingDifficultyAnswer.setValue(new BooleanType(locatorFormDTO.getBreathingDifficulties()));
		}

		QuestionnaireResponseItemComponent rashItem = questionnaireResponse.addItem();
		rashItem.setLinkId(FhirConstants.RASH_LINK_ID).setText("Rash");
		QuestionnaireResponseItemAnswerComponent rashAnswer = rashItem.addAnswer();
		if (locatorFormDTO.getRash() != null) {
			rashAnswer.setValue(new BooleanType(locatorFormDTO.getRash()));
		}

		QuestionnaireResponseItemComponent senseOfSmellItem = questionnaireResponse.addItem();
		senseOfSmellItem.setLinkId(FhirConstants.SENSE_OF_SMELL_LINK_ID).setText("Sense of Smell or Taste");
		QuestionnaireResponseItemAnswerComponent senseOfSmellAnswer = senseOfSmellItem.addAnswer();
		if (locatorFormDTO.getSmellOrTaste() != null) {
			senseOfSmellAnswer.setValue(new BooleanType(locatorFormDTO.getSmellOrTaste()));
		}

		QuestionnaireResponseItemComponent contactWithInfectedItem = questionnaireResponse.addItem();
		contactWithInfectedItem.setLinkId(FhirConstants.CONTACT_WITH_NFECTED_LINK_ID)
		        .setText("Contact with Infected Individual");
		QuestionnaireResponseItemAnswerComponent contactWithInfectedAnswer = contactWithInfectedItem.addAnswer();
		if (locatorFormDTO.getContact() != null) {
			contactWithInfectedAnswer.setValue(new BooleanType(locatorFormDTO.getContact()));
		}

		QuestionnaireResponseItemComponent mobilePhoneItem = questionnaireResponse.addItem();
		mobilePhoneItem.setLinkId(FhirConstants.MOBILE_PHONE_LINK_ID).setText("Mobile Phone");
		QuestionnaireResponseItemAnswerComponent mobilePhoneAnswer = mobilePhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getMobilePhone())) {
			mobilePhoneAnswer.setValue(new StringType(locatorFormDTO.getMobilePhone()));
		}

		QuestionnaireResponseItemComponent fixedPhoneItem = questionnaireResponse.addItem();
		fixedPhoneItem.setLinkId(FhirConstants.FIXED_PHONE_LINK_ID).setText("Fixed Phone");
		QuestionnaireResponseItemAnswerComponent fixedPhoneAnswer = fixedPhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getFixedPhone())) {
			fixedPhoneAnswer.setValue(new StringType(locatorFormDTO.getFixedPhone()));
		}

		QuestionnaireResponseItemComponent workPhoneItem = questionnaireResponse.addItem();
		workPhoneItem.setLinkId(FhirConstants.WORK_PHONE_LINK_ID).setText("Work Phone");
		QuestionnaireResponseItemAnswerComponent workPhoneAnswer = workPhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getBusinessPhone())) {
			workPhoneAnswer.setValue(new StringType(locatorFormDTO.getBusinessPhone()));
		}

		QuestionnaireResponseItemComponent emailItem = questionnaireResponse.addItem();
		emailItem.setLinkId(FhirConstants.EMAIL_LINK_ID).setText("Email");
		QuestionnaireResponseItemAnswerComponent emailAnswer = emailItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmail())) {
			emailAnswer.setValue(new StringType(locatorFormDTO.getEmail()));
		}

		QuestionnaireResponseItemComponent nationalIdItem = questionnaireResponse.addItem();
		nationalIdItem.setLinkId(FhirConstants.NATIONAL_ID_LINK_ID).setText("National ID");
		QuestionnaireResponseItemAnswerComponent nationalIdAnswer = nationalIdItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getNationalID())) {
			nationalIdAnswer.setValue(new StringType(locatorFormDTO.getNationalID()));
		}

		QuestionnaireResponseItemComponent passportCountryItem = questionnaireResponse.addItem();
		passportCountryItem.setLinkId(FhirConstants.PASSPORT_COUNTRY_LINK_ID).setText("Passport Country of Issue");
		QuestionnaireResponseItemAnswerComponent passportCountryAnswer = passportCountryItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getCountryOfPassportIssue())) {
			String country = LocatorFormUtil.getCountryLabelForValue(locatorFormDTO.getCountryOfPassportIssue());
			passportCountryAnswer.setValue(new StringType(country));
		}

		QuestionnaireResponseItemComponent passportNumberItem = questionnaireResponse.addItem();
		passportNumberItem.setLinkId(FhirConstants.PASSPORT_NUMBER_LINK_ID).setText("Passport Number");
		QuestionnaireResponseItemAnswerComponent passportNumberAnswer = passportNumberItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPassportNumber())) {
			passportNumberAnswer.setValue(new StringType(locatorFormDTO.getPassportNumber()));
		}

		QuestionnaireResponseItemComponent purposeOfVisitItem = questionnaireResponse.addItem();
		purposeOfVisitItem.setLinkId(FhirConstants.PURPOSE_OF_VIST_LINK_ID).setText("Purpose of Visit");
		QuestionnaireResponseItemAnswerComponent purposeOfVisitAnswer = purposeOfVisitItem.addAnswer();
		if (locatorFormDTO.getVisitPurpose() != null) {
			purposeOfVisitAnswer.setValue(new StringType(locatorFormDTO.getVisitPurpose().toString()));
		}
		
		QuestionnaireResponseItemComponent dateOfArrivalItem = questionnaireResponse.addItem();
		dateOfArrivalItem.setLinkId(FhirConstants.DATE_OF_ARRIVAL_LINK_ID).setText("Date Of Arrival");
		QuestionnaireResponseItemAnswerComponent dateOfArrivalAnswer = dateOfArrivalItem.addAnswer();
		if (locatorFormDTO.getArrivalDate() != null) {
			dateOfArrivalAnswer.setValue(new DateType(locatorFormDTO.getArrivalDate().toString()));
		}

		// Permanent address
		QuestionnaireResponseItemComponent permAddrNumAndStreetItem = questionnaireResponse.addItem();
		permAddrNumAndStreetItem.setLinkId(FhirConstants.PERM_ADDRESS_NUMBER_AND_STREET_LINK_ID)
				.setText("Permanent Address: Number and Street");
		QuestionnaireResponseItemAnswerComponent permAddrNumAndStreetAnswer = permAddrNumAndStreetItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getNumberAndStreet())) {
			permAddrNumAndStreetAnswer
					.setValue(new StringType(locatorFormDTO.getPermanentAddress().getNumberAndStreet()));
		}

		QuestionnaireResponseItemComponent permAddrAptmNumItem = questionnaireResponse.addItem();
		permAddrAptmNumItem.setLinkId(FhirConstants.PERM_ADDRESS_APARTMENT_NUMBER_LINK_ID)
				.setText("Permanent Address: Apartment Number");
		QuestionnaireResponseItemAnswerComponent permAddrAptmNumAnswer = permAddrAptmNumItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getApartmentNumber())) {
			permAddrAptmNumAnswer.setValue(new StringType(locatorFormDTO.getPermanentAddress().getApartmentNumber()));
		}

		QuestionnaireResponseItemComponent permAddrCityItem = questionnaireResponse.addItem();
		permAddrCityItem.setLinkId(FhirConstants.PERM_ADDRESS_CITY_LINK_ID).setText("Permanent Address: City");
		QuestionnaireResponseItemAnswerComponent permAddrCityAnswer = permAddrCityItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getCity())) {
			permAddrCityAnswer.setValue(new StringType(locatorFormDTO.getPermanentAddress().getCity()));
		}

		QuestionnaireResponseItemComponent permAddrStateProvItem = questionnaireResponse.addItem();
		permAddrStateProvItem.setLinkId(FhirConstants.PERM_ADDRESS_STATE_PROVINCE_LINK_ID)
				.setText("Permanent Address: State/Province");
		QuestionnaireResponseItemAnswerComponent permAddrStateProvAnswer = permAddrStateProvItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getStateProvince())) {
			permAddrStateProvAnswer.setValue(new StringType(locatorFormDTO.getPermanentAddress().getStateProvince()));
		}

		QuestionnaireResponseItemComponent permAddrCountryItem = questionnaireResponse.addItem();
		permAddrCountryItem.setLinkId(FhirConstants.PERM_ADDRESS_COUNTRY_LINK_ID).setText("Permanent Address: Country");
		QuestionnaireResponseItemAnswerComponent permAddrCountryAnswer = permAddrCountryItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getCountry())) {
			permAddrCountryAnswer.setValue(new StringType(locatorFormDTO.getPermanentAddress().getCountry()));
		}

		QuestionnaireResponseItemComponent permAddrZIPPostalItem = questionnaireResponse.addItem();
		permAddrZIPPostalItem.setLinkId(FhirConstants.PERM_ADDRESS_ZIP_POSTAL_CODE_LINK_ID)
				.setText("Permanent Address: ZIP/Postal Code");
		QuestionnaireResponseItemAnswerComponent permAddrZIPPostalAnswer = permAddrZIPPostalItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPermanentAddress().getZipPostalCode())) {
			permAddrZIPPostalAnswer.setValue(new StringType(locatorFormDTO.getPermanentAddress().getZipPostalCode()));
		}

		// Temp address
		QuestionnaireResponseItemComponent tempAddrHotelNameItem = questionnaireResponse.addItem();
		tempAddrHotelNameItem.setLinkId(FhirConstants.TEMP_ADDRESS_HOTEL_NAME_LINK_ID)
				.setText("Temp Address: Hotel Name");
		QuestionnaireResponseItemAnswerComponent tempAddrHotelNameAnswer = tempAddrHotelNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getHotelName())) {
			tempAddrHotelNameAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getHotelName()));
		}

		QuestionnaireResponseItemComponent tempAddrNumAndStreetItem = questionnaireResponse.addItem();
		tempAddrNumAndStreetItem.setLinkId(FhirConstants.TEMP_ADDRESS_NUMBER_AND_STREET_LINK_ID)
				.setText("Temp Address: Number and Street");
		QuestionnaireResponseItemAnswerComponent tempAddrNumAndStreetAnswer = tempAddrNumAndStreetItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getNumberAndStreet())) {
			tempAddrNumAndStreetAnswer
					.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getNumberAndStreet()));
		}

		QuestionnaireResponseItemComponent tempAddrAptmNumItem = questionnaireResponse.addItem();
		tempAddrAptmNumItem.setLinkId(FhirConstants.TEMP_ADDRESS_APARTMENT_NUMBER_LINK_ID)
				.setText("Temp Address: Apartment Number");
		QuestionnaireResponseItemAnswerComponent tempAddrAptmNumAnswer = tempAddrAptmNumItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getApartmentNumber())) {
			tempAddrAptmNumAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getApartmentNumber()));
		}

		QuestionnaireResponseItemComponent tempAddrCityItem = questionnaireResponse.addItem();
		tempAddrCityItem.setLinkId(FhirConstants.TEMP_ADDRESS_CITY_LINK_ID).setText("Temp Address: City");
		QuestionnaireResponseItemAnswerComponent tempAddrCityAnswer = tempAddrCityItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getCity())) {
			tempAddrCityAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getCity()));
		}

		QuestionnaireResponseItemComponent tempAddrStateProvItem = questionnaireResponse.addItem();
		tempAddrStateProvItem.setLinkId(FhirConstants.TEMP_ADDRESS_STATE_PROVINCE_LINK_ID)
				.setText("Temp Address: State/Province");
		QuestionnaireResponseItemAnswerComponent tempAddrStateProvAnswer = tempAddrStateProvItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getStateProvince())) {
			tempAddrStateProvAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getStateProvince()));
		}

		QuestionnaireResponseItemComponent tempAddrCountryItem = questionnaireResponse.addItem();
		tempAddrCountryItem.setLinkId(FhirConstants.TEMP_ADDRESS_COUNTRY_LINK_ID).setText("Temp Address: Country");
		QuestionnaireResponseItemAnswerComponent tempAddrCountryAnswer = tempAddrCountryItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getCountry())) {
			tempAddrCountryAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getCountry()));
		}

		QuestionnaireResponseItemComponent tempAddrZIPPostalItem = questionnaireResponse.addItem();
		tempAddrZIPPostalItem.setLinkId(FhirConstants.TEMP_ADDRESS_ZIP_POSTAL_CODE_LINK_ID)
				.setText("Temp Address: ZIP/Postal Code");
		QuestionnaireResponseItemAnswerComponent tempAddrZIPPostalAnswer = tempAddrZIPPostalItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getZipPostalCode())) {
			tempAddrZIPPostalAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getZipPostalCode()));
		}

		QuestionnaireResponseItemComponent tempAddrLocalPhoneItem = questionnaireResponse.addItem();
		tempAddrLocalPhoneItem.setLinkId(FhirConstants.TEMP_ADDRESS_LOCAL_PHONE_LINK_ID)
		        .setText("Temp Address: Local Phone");
		QuestionnaireResponseItemAnswerComponent tempAddrLocalPhoneAnswer = tempAddrLocalPhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getLocalPhone())) {
			tempAddrLocalPhoneAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getLocalPhone()));
		}

		QuestionnaireResponseItemComponent tempAddrQuarantineSiteItem = questionnaireResponse.addItem();
		tempAddrQuarantineSiteItem.setLinkId(FhirConstants.TEMP_ADDRESS_QUARANTINE_SITE_LINK_ID)
		        .setText("Temp Address : Quarantine Site");
		QuestionnaireResponseItemAnswerComponent tempAddrQuarantineSiteAnswer = tempAddrQuarantineSiteItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getTemporaryAddress().getQuarantineSite())) {
			tempAddrQuarantineSiteAnswer.setValue(new StringType(locatorFormDTO.getTemporaryAddress().getQuarantineSite()));
		}

		QuestionnaireResponseItemComponent healthOfficeItem = questionnaireResponse.addItem();
		healthOfficeItem.setLinkId(FhirConstants.HEALTH_OFFICE_LINK_ID).setText("Health Office");
		QuestionnaireResponseItemAnswerComponent healthOfficeAnswer = healthOfficeItem.addAnswer();

		QuestionnaireResponseItemComponent localityItem = questionnaireResponse.addItem();
		localityItem.setLinkId(FhirConstants.LOCALITY_LINK_ID).setText("Locality");
		QuestionnaireResponseItemAnswerComponent localityAnswer = localityItem.addAnswer();

		QuestionnaireResponseItemComponent testKitIdItem = questionnaireResponse.addItem();
		testKitIdItem.setLinkId(FhirConstants.TEST_KIT_ID_LINK_ID).setText("Test Kit Id");
		QuestionnaireResponseItemAnswerComponent testKitIdAnswer = testKitIdItem.addAnswer();
		if (locatorFormDTO instanceof HealthDeskDTO) {
			HealthDeskDTO healthDeskDto = (HealthDeskDTO) locatorFormDTO;
			if (StringUtils.isNotBlank(healthDeskDto.getHealthOffice())) {
				healthOfficeAnswer.setValue(new StringType(healthDeskDto.getHealthOffice()));
			}
			if (StringUtils.isNotBlank(healthDeskDto.getLocality())) {
				localityAnswer.setValue(new StringType(healthDeskDto.getLocality()));
			}
			if (StringUtils.isNotBlank(healthDeskDto.getTestKitId())) {
				testKitIdAnswer.setValue(new StringType(healthDeskDto.getTestKitId()));
			}
			
		}
		
		QuestionnaireResponseItemComponent lastNameItem = questionnaireResponse.addItem();
		lastNameItem.setLinkId(FhirConstants.LAST_NAME_LINK_ID).setText("Last Name");
		QuestionnaireResponseItemAnswerComponent lastNameAnswer = lastNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getLastName())) {
			lastNameAnswer.setValue(new StringType(locatorFormDTO.getLastName()));
		}
		QuestionnaireResponseItemComponent firstNameItem = questionnaireResponse.addItem();
		firstNameItem.setLinkId(FhirConstants.FIRST_NAME_LINK_ID).setText("First Name");
		QuestionnaireResponseItemAnswerComponent firstNameAnswer = firstNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getFirstName())) {
			firstNameAnswer.setValue(new StringType(locatorFormDTO.getFirstName()));
		}
		
		QuestionnaireResponseItemComponent middleInitialItem = questionnaireResponse.addItem();
		middleInitialItem.setLinkId(FhirConstants.MIDDLE_INITIAL_LINK_ID).setText("Middle Initial");
		QuestionnaireResponseItemAnswerComponent middleInitialAnswer = middleInitialItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getMiddleInitial())) {
			middleInitialAnswer.setValue(new StringType(locatorFormDTO.getMiddleInitial()));
		}
		
		QuestionnaireResponseItemComponent dateOfBirthItem = questionnaireResponse.addItem();
		dateOfBirthItem.setLinkId(FhirConstants.DATE_OF_BIRTH_LINK_ID).setText("Date Of Birth");
		QuestionnaireResponseItemAnswerComponent dateOfBirthAnswer = dateOfBirthItem.addAnswer();
		if (locatorFormDTO.getDateOfBirth() != null) {
			dateOfBirthAnswer.setValue(new DateType(locatorFormDTO.getDateOfBirth().toString()));
		}
		
		QuestionnaireResponseItemComponent sexItem = questionnaireResponse.addItem();
		sexItem.setLinkId(FhirConstants.SEX_LINK_ID).setText("Sex");
		QuestionnaireResponseItemAnswerComponent sexAnswer = sexItem.addAnswer();
		if (locatorFormDTO.getSex() != null) {
			sexAnswer.setValue(new StringType(locatorFormDTO.getSex().toString()));
		}
		
		QuestionnaireResponseItemComponent vaccinatedItem = questionnaireResponse.addItem();
		vaccinatedItem.setLinkId(FhirConstants.VACCINATED_LINK_ID).setText("Vaccinated");
		QuestionnaireResponseItemAnswerComponent vaccinatedAnswer = vaccinatedItem.addAnswer();
		if (locatorFormDTO.getVaccinated() != null) {
			vaccinatedAnswer.setValue(new BooleanType(locatorFormDTO.getVaccinated()));
		}
		
		QuestionnaireResponseItemComponent firstVaccineItem = questionnaireResponse.addItem();
		firstVaccineItem.setLinkId(FhirConstants.FIRST_VACCINE_NAME_LINK_ID).setText("Name of First Vaccine");
		QuestionnaireResponseItemAnswerComponent firstVaccineAnswer = firstVaccineItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getFirstVaccineName())) {
			firstVaccineAnswer.setValue(new StringType(locatorFormDTO.getFirstVaccineName()));
		}
		
		QuestionnaireResponseItemComponent secondVaccineItem = questionnaireResponse.addItem();
		secondVaccineItem.setLinkId(FhirConstants.SECOND_VACCINE_NAME_LINK_ID).setText("Name of Second Vaccine");
		QuestionnaireResponseItemAnswerComponent secondVaccineAnswer = secondVaccineItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getSecondVaccineName())) {
			secondVaccineAnswer.setValue(new StringType(locatorFormDTO.getSecondVaccineName()));
		}
		
		QuestionnaireResponseItemComponent dateOfFirstDoseItem = questionnaireResponse.addItem();
		dateOfFirstDoseItem.setLinkId(FhirConstants.DATE_OF_FIRST_DOSE_LINK_ID).setText("Date Of First Dose");
		QuestionnaireResponseItemAnswerComponent dateOfFirstDoseAnswer = dateOfFirstDoseItem.addAnswer();
		if (locatorFormDTO.getDateOfFirstDose() != null) {
			dateOfFirstDoseAnswer.setValue(new DateType(locatorFormDTO.getDateOfFirstDose().toString()));
		}
		
		QuestionnaireResponseItemComponent dateOfSecondDoseItem = questionnaireResponse.addItem();
		dateOfSecondDoseItem.setLinkId(FhirConstants.DATE_OF_SECOND_DOSE_LINK_ID).setText("Date Of Second Dose");
		QuestionnaireResponseItemAnswerComponent dateOfSecondDoseAnswer = dateOfSecondDoseItem.addAnswer();
		if (locatorFormDTO.getDateOfSecondDose() != null) {
			dateOfSecondDoseAnswer.setValue(new DateType(locatorFormDTO.getDateOfSecondDose().toString()));
		}
		
		QuestionnaireResponseItemComponent travellerTypeItem = questionnaireResponse.addItem();
		travellerTypeItem.setLinkId(FhirConstants.TRAVELLER_TYPE_LINK_ID).setText("Traveller Type");
		QuestionnaireResponseItemAnswerComponent travellerTypeAnswer = travellerTypeItem.addAnswer();
		if (locatorFormDTO.getTravellerType() != null) {
			travellerTypeAnswer.setValue(new StringType(locatorFormDTO.getTravellerType().toString()));
		}
		
		QuestionnaireResponseItemComponent timeOfArrivalItem = questionnaireResponse.addItem();
		timeOfArrivalItem.setLinkId(FhirConstants.ARRIVAL_TIME_LINK_ID).setText("Time of Arrival");
		QuestionnaireResponseItemAnswerComponent timeOfArrivalAnswer = timeOfArrivalItem.addAnswer();
		if (locatorFormDTO.getArrivalTime() != null) {
			timeOfArrivalAnswer.setValue(new TimeType(locatorFormDTO.getArrivalTime().toString()));
		}
		
		QuestionnaireResponseItemComponent titleItem = questionnaireResponse.addItem();
		titleItem.setLinkId(FhirConstants.TITLE_LINK_ID).setText("Title");
		QuestionnaireResponseItemAnswerComponent titleAnswer = titleItem.addAnswer();
		if (locatorFormDTO.getTitle() != null) {
			titleAnswer.setValue(new StringType(locatorFormDTO.getTitle().toString()));
		}
		
		QuestionnaireResponseItemComponent lengthOfStayItem = questionnaireResponse.addItem();
		lengthOfStayItem.setLinkId(FhirConstants.LENGTH_OF_STAY_LINK_ID).setText("Length Of Stay");
		QuestionnaireResponseItemAnswerComponent lengthOfStayAnswer = lengthOfStayItem.addAnswer();
		if (locatorFormDTO.getLengthOfStay() != null) {
			lengthOfStayAnswer.setValue(new IntegerType(locatorFormDTO.getLengthOfStay()));
		}
		
		QuestionnaireResponseItemComponent portOfEmbarkationItem = questionnaireResponse.addItem();
		portOfEmbarkationItem.setLinkId(FhirConstants.PORT_OF_EMBARKATION_LINK_ID).setText("Port Of Embarkation");
		QuestionnaireResponseItemAnswerComponent portOfEmbarkationAnswer = portOfEmbarkationItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getPortOfEmbarkation())) {
			portOfEmbarkationAnswer.setValue(new StringType(locatorFormDTO.getPortOfEmbarkation()));
		}
		
		QuestionnaireResponseItemComponent professionItem = questionnaireResponse.addItem();
		professionItem.setLinkId(FhirConstants.PROFESSION_LINK_ID).setText("Profession");
		QuestionnaireResponseItemAnswerComponent professionAnswer = professionItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getProfession())) {
			professionAnswer.setValue(new StringType(locatorFormDTO.getProfession()));
		}

		QuestionnaireResponseItemComponent countryOfBirthItem = questionnaireResponse.addItem();
		countryOfBirthItem.setLinkId(FhirConstants.COUNTRY_OF_BIRTH_LINK_ID).setText("Country Of Birth");
		QuestionnaireResponseItemAnswerComponent countryOfBirthAnswer = countryOfBirthItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getCountryOfBirth())) {
			String country = LocatorFormUtil.getCountryLabelForValue(locatorFormDTO.getCountryOfBirth());
			countryOfBirthAnswer.setValue(new StringType(country));
		}
		
		QuestionnaireResponseItemComponent passportExpiryDateItem = questionnaireResponse.addItem();
		passportExpiryDateItem.setLinkId(FhirConstants.PASSPORT_EXPIRY_DATE_LINK_ID).setText("Passport Expiry Date");
		QuestionnaireResponseItemAnswerComponent passportExpiryDateAnswer = passportExpiryDateItem.addAnswer();
		if (locatorFormDTO.getPassportExpiryDate() != null) {
			passportExpiryDateAnswer.setValue(new DateType(locatorFormDTO.getPassportExpiryDate().toString()));
		}
		
		QuestionnaireResponseItemComponent emergenceContactLastNameItem = questionnaireResponse.addItem();
		emergenceContactLastNameItem.setLinkId(FhirConstants.EMERG_CONTACT_LAST_NAME_LINK_ID)
		        .setText("Emergency Contact : Last Name");
		QuestionnaireResponseItemAnswerComponent emergenceContactLastNameAnswer = emergenceContactLastNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmergencyContact().getLastName())) {
			emergenceContactLastNameAnswer.setValue(new StringType(locatorFormDTO.getEmergencyContact().getLastName()));
		}

		QuestionnaireResponseItemComponent emergenceContactFirstNameItem = questionnaireResponse.addItem();
		emergenceContactFirstNameItem.setLinkId(FhirConstants.EMERG_CONTACT_FIRST_NAME_LINK_ID)
		        .setText("Emergency Contact : First Name");
		QuestionnaireResponseItemAnswerComponent emergenceContactFirstNameAnswer = emergenceContactFirstNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmergencyContact().getFirstName())) {
			emergenceContactFirstNameAnswer.setValue(new StringType(locatorFormDTO.getEmergencyContact().getFirstName()));
		}
		
		QuestionnaireResponseItemComponent emergenceContactAddressItem = questionnaireResponse.addItem();
		emergenceContactAddressItem.setLinkId(FhirConstants.EMERG_CONTACT_ADDRES_LINK_ID)
		        .setText("Emergency Contact : Address");
		QuestionnaireResponseItemAnswerComponent emergenceContactAddressAnswer = emergenceContactAddressItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmergencyContact().getAddress())) {
			emergenceContactAddressAnswer.setValue(new StringType(locatorFormDTO.getEmergencyContact().getAddress()));
		}

		QuestionnaireResponseItemComponent emergenceContactCountryItem = questionnaireResponse.addItem();
		emergenceContactCountryItem.setLinkId(FhirConstants.EMERG_CONTACT_COUNTRY_LINK_ID)
		        .setText("Emergency Contact : Country");
		QuestionnaireResponseItemAnswerComponent emergenceContactCountryAnswer = emergenceContactCountryItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmergencyContact().getCountry())) {
			String country = LocatorFormUtil.getCountryLabelForValue(locatorFormDTO.getEmergencyContact().getCountry());
			emergenceContactCountryAnswer.setValue(new StringType(country));
		}

		QuestionnaireResponseItemComponent emergenceContactMobilePhoneItem = questionnaireResponse.addItem();
		emergenceContactMobilePhoneItem.setLinkId(FhirConstants.EMERG_CONTACT_MOBILE_PHONE_LINK_ID)
		        .setText("Emergency Contact : Mobile Phone");
		QuestionnaireResponseItemAnswerComponent emergenceContactMobilePhoneAnswer = emergenceContactMobilePhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getEmergencyContact().getMobilePhone())) {
			emergenceContactMobilePhoneAnswer.setValue(new StringType(locatorFormDTO.getEmergencyContact().getMobilePhone()));
		}

		QuestionnaireResponseItemComponent stageItem = questionnaireResponse.addItem();
		stageItem.setLinkId(FhirConstants.STAGE_LINK_ID).setText("Stage");
		QuestionnaireResponseItemAnswerComponent stageAnswer = stageItem.addAnswer();
		if (locatorFormDTO.getStage() != null) {
			stageAnswer.setValue(new StringType(locatorFormDTO.getStage().toString()));
		}

		// Contact person
		QuestionnaireResponseItemComponent contactPersonLastNameItem = questionnaireResponse.addItem();
		contactPersonLastNameItem.setLinkId(FhirConstants.CONTACT_PERSON_LAST_NAME_LINK_ID)
		        .setText("Contact Person : Last Name");
		QuestionnaireResponseItemAnswerComponent contactPersonLastNameAnswer = contactPersonLastNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getContactPerson().getLastName())) {
			contactPersonLastNameAnswer.setValue(new StringType(locatorFormDTO.getContactPerson().getLastName()));
		}
		
		QuestionnaireResponseItemComponent contactPersonFirstNameItem = questionnaireResponse.addItem();
		contactPersonFirstNameItem.setLinkId(FhirConstants.CONTACT_PERSON_FIRST_NAME_LINK_ID)
		        .setText("Contact Person : First Name");
		QuestionnaireResponseItemAnswerComponent contactPersonFirstNameAnswer = contactPersonFirstNameItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getContactPerson().getFirstName())) {
			contactPersonFirstNameAnswer.setValue(new StringType(locatorFormDTO.getContactPerson().getFirstName()));
		}
		
		QuestionnaireResponseItemComponent contactPersonAddressItem = questionnaireResponse.addItem();
		contactPersonAddressItem.setLinkId(FhirConstants.CONTACT_PERSON_ADDRES_LINK_ID).setText("Contact Person : Address");
		QuestionnaireResponseItemAnswerComponent contactPersonAddressAnswer = contactPersonAddressItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getContactPerson().getAddress())) {
			contactPersonAddressAnswer.setValue(new StringType(locatorFormDTO.getContactPerson().getAddress()));
		}
		
		QuestionnaireResponseItemComponent contactPersonEmailItem = questionnaireResponse.addItem();
		contactPersonEmailItem.setLinkId(FhirConstants.CONTACT_PERSON_EMAIL_LINK_ID).setText("Contact Person : Email");
		QuestionnaireResponseItemAnswerComponent contactPersonEmailAnswer = contactPersonEmailItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getContactPerson().getEmail())) {
			contactPersonEmailAnswer.setValue(new StringType(locatorFormDTO.getContactPerson().getEmail()));
		}
		
		QuestionnaireResponseItemComponent contactPersonMobilePhoneItem = questionnaireResponse.addItem();
		contactPersonMobilePhoneItem.setLinkId(FhirConstants.CONTACT_PERSON_MOBILE_PHONE_LINK_ID)
		        .setText("Contact Person : Mobile Phone");
		QuestionnaireResponseItemAnswerComponent contactPersonMobilePhoneAnswer = contactPersonMobilePhoneItem.addAnswer();
		if (StringUtils.isNotBlank(locatorFormDTO.getContactPerson().getMobilePhone())) {
			contactPersonMobilePhoneAnswer.setValue(new StringType(locatorFormDTO.getContactPerson().getMobilePhone()));
		}

		return questionnaireResponse;
	}

	@Override
	public Questionnaire createQuestionnaire() {
		Questionnaire questionnaire = new Questionnaire();
		questionnaire.setId(questionnaireId);
		
		questionnaire.addIdentifier(new Identifier().setSystem(locatorFormFhirSystem).setValue(questionnaireId));
		questionnaire.setTitle("Locator Form Questionnaire");
		questionnaire.setDate(new Date());
		questionnaire.setPublisher(locatorFormFhirSystem);

		QuestionnaireItemComponent seatItem = questionnaire.addItem();
		seatItem.setLinkId(FhirConstants.SEAT_LINK_ID).setText("Seat").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent nationalityItem = questionnaire.addItem();
		nationalityItem.setLinkId(FhirConstants.NATIONALITY_LINK_ID).setText("Nationality")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent airLineItem = questionnaire.addItem();
		airLineItem.setLinkId(FhirConstants.AIRLINE_LINK_ID).setText("Airline").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent flightItem = questionnaire.addItem();
		flightItem.setLinkId(FhirConstants.FLIGHT_LINK_ID).setText("Flight").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent countriesVistedItem = questionnaire.addItem();
		countriesVistedItem.setLinkId(FhirConstants.COUNTRIES_VISTED_LINK_ID)
				.setText("Countries Vistied within 6 Months").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent infectionItem = questionnaire.addItem();
		infectionItem.setLinkId(FhirConstants.PREVIOUS_INFECTION_LINK_ID).setText("Previous Infection")
				.setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent feverItem = questionnaire.addItem();
		feverItem.setLinkId(FhirConstants.FEVER_LINK_ID).setText("Fever").setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent soreThroatItem = questionnaire.addItem();
		soreThroatItem.setLinkId(FhirConstants.SORE_THROAT_LINK_ID).setText("Sore Throat")
				.setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent jointPainItem = questionnaire.addItem();
		jointPainItem.setLinkId(FhirConstants.SORE_THROAT_LINK_ID).setText("Joint Pain")
				.setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent coughItem = questionnaire.addItem();
		coughItem.setLinkId(FhirConstants.COUGH_LINK_ID).setText("Cough").setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent breathingDifficultyItem = questionnaire.addItem();
		breathingDifficultyItem.setLinkId(FhirConstants.BREATHING_LINK_ID).setText("Breathing Difficulty")
				.setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent rashItem = questionnaire.addItem();
		rashItem.setLinkId(FhirConstants.RASH_LINK_ID).setText("Rash").setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent senseOfSmellItem = questionnaire.addItem();
		senseOfSmellItem.setLinkId(FhirConstants.SENSE_OF_SMELL_LINK_ID).setText("Sense of Smell or Taste")
				.setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent contactWithInfectedItem = questionnaire.addItem();
		contactWithInfectedItem.setLinkId(FhirConstants.CONTACT_WITH_NFECTED_LINK_ID)
				.setText("Contact with Infected Individual").setType(QuestionnaireItemType.BOOLEAN);

		QuestionnaireItemComponent mobilePhoneItem = questionnaire.addItem();
		mobilePhoneItem.setLinkId(FhirConstants.MOBILE_PHONE_LINK_ID).setText("Mobile Phone")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent fixedPhoneItem = questionnaire.addItem();
		fixedPhoneItem.setLinkId(FhirConstants.FIXED_PHONE_LINK_ID).setText("Fixed Phone")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent workPhoneItem = questionnaire.addItem();
		workPhoneItem.setLinkId(FhirConstants.WORK_PHONE_LINK_ID).setText("Work Phone")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent emailItem = questionnaire.addItem();
		emailItem.setLinkId(FhirConstants.EMAIL_LINK_ID).setText("Email").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent nationalIdItem = questionnaire.addItem();
		nationalIdItem.setLinkId(FhirConstants.NATIONAL_ID_LINK_ID).setText("National ID")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent passportCountryItem = questionnaire.addItem();
		passportCountryItem.setLinkId(FhirConstants.PASSPORT_COUNTRY_LINK_ID).setText("Passport Country of Issue")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent passportNumberItem = questionnaire.addItem();
		passportNumberItem.setLinkId(FhirConstants.PASSPORT_NUMBER_LINK_ID).setText("Passport Number")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent visitPurposeItem = questionnaire.addItem();
		visitPurposeItem.setLinkId(FhirConstants.PURPOSE_OF_VIST_LINK_ID).setText("Purpose of Visit")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent dateOfArrivalItem = questionnaire.addItem();
		dateOfArrivalItem.setLinkId(FhirConstants.DATE_OF_ARRIVAL_LINK_ID).setText("Date Of Arrival")
		        .setType(QuestionnaireItemType.DATE);

		QuestionnaireItemComponent healthOfficeItem = questionnaire.addItem();
		healthOfficeItem.setLinkId(FhirConstants.HEALTH_OFFICE_LINK_ID).setText("Health Office")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent localityItem = questionnaire.addItem();
		localityItem.setLinkId(FhirConstants.LOCALITY_LINK_ID).setText("Locality").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent testKitIdItem = questionnaire.addItem();
		testKitIdItem.setLinkId(FhirConstants.TEST_KIT_ID_LINK_ID).setText("Test Kit Id")
						.setType(QuestionnaireItemType.TEXT);

		// Permanent address
		QuestionnaireItemComponent permAddrNumAndStreetItem = questionnaire.addItem();
		permAddrNumAndStreetItem.setLinkId(FhirConstants.PERM_ADDRESS_NUMBER_AND_STREET_LINK_ID)
				.setText("Permanent Address: Number and Street").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent permAddrAptmNumItem = questionnaire.addItem();
		permAddrAptmNumItem.setLinkId(FhirConstants.PERM_ADDRESS_APARTMENT_NUMBER_LINK_ID)
				.setText("Permanent Address: Apartment Number").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent permAddrCityItem = questionnaire.addItem();
		permAddrCityItem.setLinkId(FhirConstants.PERM_ADDRESS_CITY_LINK_ID).setText("Permanent Address: City")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent permAddrStateProvItem = questionnaire.addItem();
		permAddrStateProvItem.setLinkId(FhirConstants.PERM_ADDRESS_STATE_PROVINCE_LINK_ID)
				.setText("Permanent Address: State/Province").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent permAddrCountryItem = questionnaire.addItem();
		permAddrCountryItem.setLinkId(FhirConstants.PERM_ADDRESS_COUNTRY_LINK_ID).setText("Permanent Address: Country")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent permAddrZIPPostalItem = questionnaire.addItem();
		permAddrZIPPostalItem.setLinkId(FhirConstants.PERM_ADDRESS_ZIP_POSTAL_CODE_LINK_ID)
				.setText("Permanent Address: ZIP/Postal Code").setType(QuestionnaireItemType.TEXT);

		// Temp address
		QuestionnaireItemComponent tempAddrHotelNameItem = questionnaire.addItem();
		tempAddrHotelNameItem.setLinkId(FhirConstants.TEMP_ADDRESS_HOTEL_NAME_LINK_ID)
				.setText("Temp Address: Hotel Name").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrNumAndStreetItem = questionnaire.addItem();
		tempAddrNumAndStreetItem.setLinkId(FhirConstants.TEMP_ADDRESS_NUMBER_AND_STREET_LINK_ID)
				.setText("Temp Address: Number and Street").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrAptmNumItem = questionnaire.addItem();
		tempAddrAptmNumItem.setLinkId(FhirConstants.TEMP_ADDRESS_APARTMENT_NUMBER_LINK_ID)
				.setText("Temp Address: Apartment Number").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrCityItem = questionnaire.addItem();
		tempAddrCityItem.setLinkId(FhirConstants.TEMP_ADDRESS_CITY_LINK_ID).setText("Temp Address: City")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrStateProvItem = questionnaire.addItem();
		tempAddrStateProvItem.setLinkId(FhirConstants.TEMP_ADDRESS_STATE_PROVINCE_LINK_ID)
				.setText("Temp Address: State/Province").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrCountryItem = questionnaire.addItem();
		tempAddrCountryItem.setLinkId(FhirConstants.TEMP_ADDRESS_COUNTRY_LINK_ID).setText("Temp Address: Country")
				.setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent tempAddrZIPPostalItem = questionnaire.addItem();
		tempAddrZIPPostalItem.setLinkId(FhirConstants.TEMP_ADDRESS_ZIP_POSTAL_CODE_LINK_ID)
		        .setText("Temp Address: ZIP/Postal Code").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent tempAddrLocalPhoneItem = questionnaire.addItem();
		tempAddrLocalPhoneItem.setLinkId(FhirConstants.TEMP_ADDRESS_LOCAL_PHONE_LINK_ID).setText("Temp Address: Local Phone")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent tempAddrQuarantineSiteItem = questionnaire.addItem();
		tempAddrQuarantineSiteItem.setLinkId(FhirConstants.TEMP_ADDRESS_QUARANTINE_SITE_LINK_ID)
		        .setText("Temp Address : Quarantine Site").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent lastNameItem = questionnaire.addItem();
		lastNameItem.setLinkId(FhirConstants.LAST_NAME_LINK_ID).setText("Last Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent firstNameItem = questionnaire.addItem();
		firstNameItem.setLinkId(FhirConstants.FIRST_NAME_LINK_ID).setText("First Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent middleInitialItem = questionnaire.addItem();
		middleInitialItem.setLinkId(FhirConstants.MIDDLE_INITIAL_LINK_ID).setText("Middle Initial")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent dateOfBirthItem = questionnaire.addItem();
		dateOfBirthItem.setLinkId(FhirConstants.DATE_OF_BIRTH_LINK_ID).setText("Date Of Birth")
		        .setType(QuestionnaireItemType.DATE);
		
		QuestionnaireItemComponent sexItem = questionnaire.addItem();
		sexItem.setLinkId(FhirConstants.SEX_LINK_ID).setText("Sex").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent vaccinatedItem = questionnaire.addItem();
		vaccinatedItem.setLinkId(FhirConstants.VACCINATED_LINK_ID).setText("Vaccinated")
		        .setType(QuestionnaireItemType.BOOLEAN);
		
		QuestionnaireItemComponent firstVaccineItem = questionnaire.addItem();
		firstVaccineItem.setLinkId(FhirConstants.FIRST_VACCINE_NAME_LINK_ID).setText("Name of First Vaccine")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent secondVaccineItem = questionnaire.addItem();
		secondVaccineItem.setLinkId(FhirConstants.SECOND_VACCINE_NAME_LINK_ID).setText("Name of Second Vaccine")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent dateOfFirstDoseItem = questionnaire.addItem();
		dateOfFirstDoseItem.setLinkId(FhirConstants.DATE_OF_FIRST_DOSE_LINK_ID).setText("Date Of First Dose")
		        .setType(QuestionnaireItemType.DATE);
		
		QuestionnaireItemComponent dateOfSecondDoseItem = questionnaire.addItem();
		dateOfSecondDoseItem.setLinkId(FhirConstants.DATE_OF_SECOND_DOSE_LINK_ID).setText("Date Of Second Dose")
		        .setType(QuestionnaireItemType.DATE);
		
		QuestionnaireItemComponent travellerTypeItem = questionnaire.addItem();
		travellerTypeItem.setLinkId(FhirConstants.TRAVELLER_TYPE_LINK_ID).setText("Traveller Type")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent timeOfArrivalItem = questionnaire.addItem();
		timeOfArrivalItem.setLinkId(FhirConstants.ARRIVAL_TIME_LINK_ID).setText("Time of Arrival")
		        .setType(QuestionnaireItemType.TIME);
		
		QuestionnaireItemComponent tittleItem = questionnaire.addItem();
		tittleItem.setLinkId(FhirConstants.TITLE_LINK_ID).setText("Tittle").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent lengthOfStayItem = questionnaire.addItem();
		lengthOfStayItem.setLinkId(FhirConstants.LENGTH_OF_STAY_LINK_ID).setText("Length Of Stay")
		        .setType(QuestionnaireItemType.INTEGER);
		
		QuestionnaireItemComponent portOfEmbarkationItem = questionnaire.addItem();
		portOfEmbarkationItem.setLinkId(FhirConstants.PORT_OF_EMBARKATION_LINK_ID).setText("Port Of Embarkation")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent professionItem = questionnaire.addItem();
		professionItem.setLinkId(FhirConstants.PROFESSION_LINK_ID).setText("Profession").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent countryOfBirthItem = questionnaire.addItem();
		countryOfBirthItem.setLinkId(FhirConstants.COUNTRY_OF_BIRTH_LINK_ID).setText("Country Of Birth")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent passportExpiryDateItem = questionnaire.addItem();
		passportExpiryDateItem.setLinkId(FhirConstants.PASSPORT_EXPIRY_DATE_LINK_ID).setText("Passport Expiry Date")
		        .setType(QuestionnaireItemType.DATE);
		
		QuestionnaireItemComponent emergenceContactLastNameItem = questionnaire.addItem();
		emergenceContactLastNameItem.setLinkId(FhirConstants.EMERG_CONTACT_LAST_NAME_LINK_ID)
		        .setText("Emergency Contact : Last Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent emergenceContactFirstNameItem = questionnaire.addItem();
		emergenceContactFirstNameItem.setLinkId(FhirConstants.EMERG_CONTACT_FIRST_NAME_LINK_ID)
		        .setText("Emergency Contact : First Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent emergenceContactAddressItem = questionnaire.addItem();
		emergenceContactAddressItem.setLinkId(FhirConstants.EMERG_CONTACT_ADDRES_LINK_ID)
		        .setText("Emergency Contact : Address").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent emergenceContactCountryItem = questionnaire.addItem();
		emergenceContactCountryItem.setLinkId(FhirConstants.EMERG_CONTACT_COUNTRY_LINK_ID)
		        .setText("Emergency Contact : Country").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent emergenceContactMobilePhoneItem = questionnaire.addItem();
		emergenceContactMobilePhoneItem.setLinkId(FhirConstants.EMERG_CONTACT_MOBILE_PHONE_LINK_ID)
		        .setText("Emergency Contact : Mobile Phone").setType(QuestionnaireItemType.TEXT);

		QuestionnaireItemComponent stageItem = questionnaire.addItem();
		stageItem.setLinkId(FhirConstants.STAGE_LINK_ID).setText("Stage").setType(QuestionnaireItemType.TEXT);

		// Contact person
		QuestionnaireItemComponent contactPersonLastNameItem = questionnaire.addItem();
		contactPersonLastNameItem.setLinkId(FhirConstants.CONTACT_PERSON_LAST_NAME_LINK_ID)
		        .setText("Contact Person : Last Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent contactPersonFirstNameItem = questionnaire.addItem();
		contactPersonFirstNameItem.setLinkId(FhirConstants.CONTACT_PERSON_FIRST_NAME_LINK_ID)
		        .setText("Contact Person : First Name").setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent contactPersonAddressItem = questionnaire.addItem();
		contactPersonAddressItem.setLinkId(FhirConstants.CONTACT_PERSON_ADDRES_LINK_ID).setText("Contact Person : Address")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent contactPersonEmailItem = questionnaire.addItem();
		contactPersonEmailItem.setLinkId(FhirConstants.CONTACT_PERSON_EMAIL_LINK_ID).setText("Contact Person : Email")
		        .setType(QuestionnaireItemType.TEXT);
		
		QuestionnaireItemComponent contactPersonMobilePhoneItem = questionnaire.addItem();
		contactPersonMobilePhoneItem.setLinkId(FhirConstants.CONTACT_PERSON_MOBILE_PHONE_LINK_ID)
		        .setText("Contact Person : Mobile Phone").setType(QuestionnaireItemType.TEXT);

		return questionnaire;
	}
}
