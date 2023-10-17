package org.itech.locator.form.webapp.api;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;

import org.itech.locator.form.webapp.api.dto.DataFlowSummary;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.fhir.service.transform.FhirTransformService;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.itech.locator.form.webapp.summary.service.SummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
@RequestMapping("/support")
@Slf4j
public class SupportController {

	@Autowired
	protected FhirPersistingService fhirPersistingService;
	@Autowired
	protected FhirTransformService fhirTransformService;
	@Autowired
	private SummaryService barcodeService;
	@Autowired
	private ObjectMapper objectMapper;

	@GetMapping("/summary/{serviceRequestId}")
	public ResponseEntity<byte[]> getSummaryPDFByIds(@PathVariable("serviceRequestId") String serviceRequestId)
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
						.generateSummaryFile(idAndLabels.entrySet().stream()
								.collect(Collectors.toMap(e -> e.getKey().getId(), e -> e.getValue())), locatorFormDTO)
						.toByteArray());
	}

	@GetMapping("/dataFlowSummary/bounded/calculated")
	public ResponseEntity<DataFlowSummary> checkDataFlowSummary(@RequestParam(defaultValue = "0") Long sinceSeconds,
			@RequestParam(defaultValue = "0") Long sinceMinutes, @RequestParam(defaultValue = "1") Long sinceHours,
			@RequestParam(defaultValue = "0") Long sinceDays, @RequestParam(defaultValue = "0") Long untilSeconds,
			@RequestParam(defaultValue = "0") Long untilMinutes, @RequestParam(defaultValue = "0") Long untilHours,
			@RequestParam(defaultValue = "0") Long untilDays, @RequestParam(defaultValue = "0") Long secondsFlagged,
			@RequestParam(defaultValue = "30") Long minutesFlagged, @RequestParam(defaultValue = "0") Long hoursFlagged,
			@RequestParam(defaultValue = "0") Long daysFlagged) {

		Instant since = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		since = since.minus(sinceSeconds, ChronoUnit.SECONDS);
		since = since.minus(sinceMinutes, ChronoUnit.MINUTES);
		since = since.minus(sinceHours, ChronoUnit.HOURS);
		since = since.minus(sinceDays, ChronoUnit.DAYS);

		Instant until = Instant.now().truncatedTo(ChronoUnit.SECONDS);
		until = until.minus(untilSeconds, ChronoUnit.SECONDS);
		until = until.minus(untilMinutes, ChronoUnit.MINUTES);
		until = until.minus(untilHours, ChronoUnit.HOURS);
		until = until.minus(untilDays, ChronoUnit.DAYS);

		Instant flaggedUntil = until.minus(secondsFlagged, ChronoUnit.SECONDS);
		flaggedUntil = flaggedUntil.minus(minutesFlagged, ChronoUnit.MINUTES);
		flaggedUntil = flaggedUntil.minus(hoursFlagged, ChronoUnit.HOURS);
		flaggedUntil = flaggedUntil.minus(daysFlagged, ChronoUnit.DAYS);

		return ResponseEntity.ok(fhirPersistingService.getDataFlowSummary(since, until, flaggedUntil));
	}

	@GetMapping("/dataFlowSummary/bounded")
	public ResponseEntity<DataFlowSummary> checkDataFlowSummary(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime since,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime until,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime flaggedUntil,
			@RequestParam(defaultValue = "+04:00") String timezone) {
		return ResponseEntity
				.ok(fhirPersistingService.getDataFlowSummary(since.atZone(ZoneOffset.of(timezone)).toInstant(),
						until.atZone(ZoneOffset.of(timezone)).toInstant(),
						flaggedUntil.atZone(ZoneOffset.of(timezone)).toInstant()));
	}

	@GetMapping("/dataFlowSummary")
	public ResponseEntity<DataFlowSummary> checkDataFlowSummary(@RequestParam(defaultValue = "0") Long secondsFlagged,
			@RequestParam(defaultValue = "30") Long minutesFlagged, @RequestParam(defaultValue = "0") Long hoursFlagged,
			@RequestParam(defaultValue = "0") Long daysFlagged) {

		Instant flaggedUntil = Instant.now().truncatedTo(ChronoUnit.SECONDS).minus(secondsFlagged, ChronoUnit.SECONDS);
		flaggedUntil = flaggedUntil.minus(minutesFlagged, ChronoUnit.MINUTES);
		flaggedUntil = flaggedUntil.minus(hoursFlagged, ChronoUnit.HOURS);
		flaggedUntil = flaggedUntil.minus(daysFlagged, ChronoUnit.DAYS);

		return ResponseEntity.ok(fhirPersistingService.getDataFlowSummary(Instant.EPOCH,
				Instant.now().truncatedTo(ChronoUnit.SECONDS), flaggedUntil));
	}

}
