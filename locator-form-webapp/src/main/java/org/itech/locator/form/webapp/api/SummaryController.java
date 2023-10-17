package org.itech.locator.form.webapp.api;

import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.itech.locator.form.webapp.summary.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.DocumentException;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/summary")
@Slf4j
public class SummaryController {

	@Autowired
	protected FhirPersistingService fhirPersistingService;
	@Autowired
	protected FhirTransformService fhirTransformService;
	@Autowired
	private SummaryService barcodeService;
	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping
	public ResponseEntity<byte[]> getSummaryPDF(@RequestBody @Valid LocatorFormDTO locatorFormDTO)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		if (!locatorFormDTO.getAcceptedTerms()) {
			return ResponseEntity.badRequest().build();
		}

		log.trace("Received: " + locatorFormDTO.toString());
		Map<SummaryAccessInfo, LabelContentPair> idAndLabels = fhirTransformService
				.createLabelContentPair(locatorFormDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		return ResponseEntity.ok() //
				.headers(headers) //
				.body(barcodeService
						.generateSummaryFile(
								idAndLabels.entrySet().stream()
										.collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue())),
								locatorFormDTO)
						.toByteArray());
	}

	@GetMapping("/{serviceRequestId}")
	public ResponseEntity<byte[]> getSummaryPDFByIds(@PathVariable("serviceRequestId") String serviceRequestId,
			@RequestHeader(name = "Authorization", defaultValue = "") String bearerPass)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		log.trace("Received: " + serviceRequestId);
		LocatorFormDTO locatorFormDTO = objectMapper.readValue(
				fhirPersistingService.getTaskFromServiceRequest(serviceRequestId).orElseThrow().getDescription(),
				LocatorFormDTO.class);
		Map<SummaryAccessInfo, LabelContentPair> idAndLabels = fhirTransformService
				.createLabelContentPair(locatorFormDTO);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_PDF);
		return ResponseEntity.ok() //
				.headers(headers) //
				.body(barcodeService
						.generateSummaryFile(
								idAndLabels.entrySet().stream()
										.collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue())),
								locatorFormDTO)
						.toByteArray());
	}
}
