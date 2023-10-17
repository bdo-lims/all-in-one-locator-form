package org.itech.locator.form.webapp.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO.Stage;
import org.itech.locator.form.webapp.api.dto.PIODTO;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService.TransactionObjects;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;

import ca.uhn.fhir.context.FhirContext;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/pio")
@Slf4j
public class PIOController {

	@Autowired
	protected FhirPersistingService fhirPersistingService;
	@Autowired
	protected FhirTransformService fhirTransformService;
	@Autowired
	private FhirContext fhirContext;

	@PostMapping
	public ResponseEntity<List<SummaryAccessInfo>> submitForm(@RequestBody @Valid PIODTO pioDTO,
			BindingResult result)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().build();
		}

		log.trace("Received: " + pioDTO.toString());
		pioDTO.setStage(Stage.PIO);
		TransactionObjects transactionObjects = fhirTransformService.createTransactionObjects(pioDTO, false,
				TaskStatus.DRAFT);
		Bundle transactionResponseBundle = fhirPersistingService.executeTransaction(transactionObjects.bundle);
		log.trace("fhirTransactionResponse: "
				+ fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionResponseBundle));

		// no further changes to locator form should happen at this point, or the
		// summary access token will become invalid
		Map<SummaryAccessInfo, LabelContentPair> idAndLabels = fhirTransformService
				.createLabelContentPair(pioDTO);
		return ResponseEntity.ok(new ArrayList<>(idAndLabels.keySet()));
	}

	@GetMapping()
	public ResponseEntity<String> getHealthDesk() {
		return ResponseEntity.ok("authenticated");

	}

}
