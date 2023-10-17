package org.itech.locator.form.webapp.api;

import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.mail.MessagingException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;

import org.hl7.fhir.r4.model.Patient;
import org.itech.locator.form.webapp.api.dto.SwabDTO;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.uhn.fhir.context.FhirContext;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/swab")
@Slf4j
public class SwabController {
	
	@Autowired
	protected FhirPersistingService fhirPersistingService;
	
	@Autowired
	protected FhirTransformService fhirTransformService;
	
	@Autowired
	private FhirContext fhirContext;
	
	@GetMapping("/servicerequest/{testKitId}")
	public ResponseEntity<SwabDTO> searchByServiceRequestId(@PathVariable String testKitId)
	        throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		log.trace("Received: " + testKitId);
		Optional<Patient> patient = fhirPersistingService.getPatientFromServiceRequest(testKitId);
		if (patient.isPresent()) {
			SwabDTO swabDTO = new SwabDTO();
			Patient fhirPatient = patient.get();
			log.trace("fhirTransactionResponse: "
			        + fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(fhirPatient));
			swabDTO.setTestKidNumber(testKitId);
			swabDTO.setPassengerName(fhirPatient.getNameFirstRep().getFamily() + " " +fhirPatient.getNameFirstRep().getGivenAsSingleString());
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
			String formatedDob = formatter.format(fhirPatient.getBirthDate()); 
			swabDTO.setPassengerDob(formatedDob);
			return ResponseEntity.ok(swabDTO);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
