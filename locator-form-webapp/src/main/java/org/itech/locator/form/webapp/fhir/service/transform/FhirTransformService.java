package org.itech.locator.form.webapp.fhir.service.transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.QuestionnaireResponse;
import org.hl7.fhir.r4.model.Questionnaire;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Specimen;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface FhirTransformService {

	public class TransactionObjects {
		public Bundle bundle;
		public Task task;
		public List<ServiceRequestObjects> serviceRequestPatientPairs = new ArrayList<>();
		public QuestionnaireResponse questionnaireResponse;
	}

	public class ServiceRequestObjects {
		public Task task;
		public ServiceRequest serviceRequest;
		public Patient patient;
		public Specimen specimen;

		public ServiceRequestObjects(Task task, ServiceRequest sRequest, Patient patient, Specimen specimen) {
			this.task = task;
			this.serviceRequest = sRequest;
			this.patient = patient;
			this.specimen = specimen;
		}
	}

	Map<SummaryAccessInfo, LabelContentPair> createLabelContentPair(@Valid LocatorFormDTO locatorFormDTO);

	TransactionObjects createTransactionObjects(LocatorFormDTO locatorFormDTO, boolean assignIds, TaskStatus status)
			throws JsonProcessingException;

	Questionnaire createQuestionnaire() ;		
}
