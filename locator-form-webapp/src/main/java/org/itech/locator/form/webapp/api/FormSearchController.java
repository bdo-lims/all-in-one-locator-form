package org.itech.locator.form.webapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.Task;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.itech.locator.form.webapp.api.dto.FormPersonSearchDTO;
import org.itech.locator.form.webapp.api.dto.HealthDeskDTO;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.api.dto.Traveller;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/formsearch")
@Slf4j
public class FormSearchController {

	@Autowired
	protected FhirPersistingService fhirPersistingService;
	@Autowired
	protected FhirTransformService fhirTransformService;
	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/servicerequest/{serviceRequestId}")
	public ResponseEntity<LocatorFormDTO> searchByServiceRequestId(@PathVariable String serviceRequestId,
			@RequestParam(defaultValue = "false") Boolean allForms)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		log.trace("Received: " + serviceRequestId);
		Optional<Task> task = fhirPersistingService.getTaskFromServiceRequest(serviceRequestId);
		if (task.isPresent() && TaskStatus.DRAFT.equals(task.get().getStatus())) {
			LocatorFormDTO locatorFormDTO = objectMapper.readValue(task.get().getDescription(), LocatorFormDTO.class);
			locatorFormDTO.setFinalized(!TaskStatus.DRAFT.equals(task.get().getStatus()));
			return ResponseEntity.ok(locatorFormDTO);
		} else if (task.isPresent() && (!TaskStatus.DRAFT.equals(task.get().getStatus()) && allForms)) {
			HealthDeskDTO dto = objectMapper.readValue(task.get().getDescription(), HealthDeskDTO.class);
			dto.setFinalized(!TaskStatus.DRAFT.equals(task.get().getStatus()));
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/passenger/{searchValue}")
	public ResponseEntity<FormPersonSearchDTO> searchServiceRequestByPatient(@PathVariable String searchValue)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		log.trace("Received: " + searchValue);
		List<Traveller> travellers = new ArrayList<>();
		List<Patient> patients = fhirPersistingService.searchPatientByValue(searchValue);

		if (patients.size() > 0) {
			for (Patient patient : patients) {
				Optional<ServiceRequest> serviceRequest = fhirPersistingService.getServiceRequestForPatient(patient);
				if (serviceRequest.isPresent()) {
					Traveller traveller = new Traveller();
					traveller.setServiceRequestId(serviceRequest.get().getIdElement().getIdPart());
					traveller.setLastName(patient.getNameFirstRep().getFamily());
					traveller.setFirstName(patient.getNameFirstRep().getGivenAsSingleString());
					for (Identifier id : patient.getIdentifier()) {
						if ("passport".equals(id.getSystemElement().getValueAsString())) {
							traveller.setPassportNumber(id.getValue());
						}
					}
					traveller.setFormSubmitionDateTime(serviceRequest.get().getAuthoredOn());
					travellers.add(traveller);
				}
			}
			FormPersonSearchDTO dto = new FormPersonSearchDTO();
			dto.setTravellers(travellers);
			return ResponseEntity.ok(dto);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

}
