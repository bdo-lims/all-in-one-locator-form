package org.itech.locator.form.webapp.fhir.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.itech.locator.form.webapp.api.dto.DataFlowSummary;

import ca.uhn.fhir.rest.api.MethodOutcome;

public interface FhirPersistingService {

	Bundle executeTransaction(Bundle transactionBundle);

	MethodOutcome executeTransaction(Resource resource);

	Optional<Task> getTaskFromServiceRequest(String serviceRequestId);

	Optional<Patient> getPatientFromServiceRequest(String testKitId);

	List<Patient> searchPatientByValue(String searchValue);

	Optional<ServiceRequest> getServiceRequestForPatient(Patient patient);

	List<ServiceRequest> getServiceRequestsForPatients(List<Patient> patients);

	Optional<Task> getTaskById(String taskId);

	DataFlowSummary getDataFlowSummary(Instant since, Instant until, Instant flaggedUntil);
}
