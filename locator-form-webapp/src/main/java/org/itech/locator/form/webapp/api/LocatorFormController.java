package org.itech.locator.form.webapp.api;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.velocity.app.VelocityEngine;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Task.TaskStatus;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO.Stage;
import org.itech.locator.form.webapp.email.service.EmailService;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService.TransactionObjects;
import org.itech.locator.form.webapp.logging.LogUtil;
import org.itech.locator.form.webapp.security.recaptcha.service.RecaptchaService;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.itech.locator.form.webapp.summary.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;

import ca.uhn.fhir.context.FhirContext;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/locator-form")
@Slf4j
public class LocatorFormController {

	@Autowired
	protected FhirPersistingService fhirPersistingService;
	@Autowired
	protected FhirTransformService fhirTransformService;
	@Autowired
	private SummaryService barcodeService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private FhirContext fhirContext;
	@Autowired
	private RecaptchaService recaptchaService;
	@Autowired
	private Optional<VelocityEngine> velocityEngine;

	@PostMapping
	public ResponseEntity<List<SummaryAccessInfo>> submitForm(
			@RequestParam(name = "recaptchaToken") String recaptchaToken,
			@RequestBody @Valid LocatorFormDTO locatorFormDTO, BindingResult result, HttpServletRequest request)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {

		if (result.hasErrors()) {
			result.getFieldErrors().stream().forEach(
					e -> log.error("Validation error for field " + e.getField() + ": " + e.getDefaultMessage() + " '"
							+ LogUtil.sanitizeUntrustedInputMessageForLog(e.getRejectedValue().toString()) + "'"));
			result.getGlobalErrors().stream()
					.forEach(e -> log.error("Validation error for global object: " + e.getDefaultMessage()));
			return ResponseEntity.badRequest().build();
		}
		if (!recaptchaService.verifyRecaptcha(request.getRemoteAddr(), recaptchaToken)) {
			log.error("reCAPTCHA token could not be verified");
			return ResponseEntity.badRequest().build();
		}
		if (!locatorFormDTO.getAcceptedTerms()) {
			log.error("Accepted terms could not be detected");
			return ResponseEntity.badRequest().build();
		}

		log.trace("Received: " + locatorFormDTO.toString());
		locatorFormDTO.setStage(Stage.PASSENGER);
		TransactionObjects transactionObjects = fhirTransformService.createTransactionObjects(locatorFormDTO, true,
				TaskStatus.DRAFT);
		Bundle transactionResponseBundle = fhirPersistingService.executeTransaction(transactionObjects.bundle);
		log.trace("fhirTransactionResponse: "
				+ fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(transactionResponseBundle));

		// no further changes to locator form should happen at this point, or the
		// summary access token will become invalid
		Map<SummaryAccessInfo, LabelContentPair> idAndLabels = fhirTransformService
				.createLabelContentPair(locatorFormDTO);
		Map<String, ByteArrayOutputStream> attachments = new HashMap<>();
		attachments.put("locatorFormBarcodes" + transactionObjects.task.getIdElement().getIdPart() + ".pdf",
				barcodeService.generateSummaryFile(idAndLabels.entrySet().stream()
						.collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue())), locatorFormDTO));

		if (velocityEngine.isEmpty()) {
			emailService.sendMessageWithAttachment(locatorFormDTO.getEmail(), "Locator-Form Barcode", "Hello "
					+ locatorFormDTO.getFirstName() + ",\n\n"
					+ "Please bring a printed copy of the attached file to the Airport of Mauritius as you will need them when you land in Mauritius",
					attachments);
		} else {
			Map<String, Object> templateObjects = new HashMap<>();
			templateObjects.put("locatorForm", locatorFormDTO);
			emailService.sendTemplateMessageWithAttachment(locatorFormDTO.getEmail(), "Locator-Form Barcode",
					"traveller_notification_email.vm", templateObjects, attachments);
		}
		return ResponseEntity.ok(new ArrayList<>(idAndLabels.keySet()));
	}

	@GetMapping("/test/")
	public ResponseEntity<String> test() {
		return ResponseEntity.ok("Hello, World");
	}

}
