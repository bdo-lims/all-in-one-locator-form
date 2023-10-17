package org.itech.locator.form.webapp.api;

import java.util.Optional;

import javax.mail.MessagingException;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.itech.locator.form.webapp.api.dto.Resident;
import org.itech.locator.form.webapp.resident.service.ResidentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.itextpdf.text.DocumentException;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@RestController
@RequestMapping("/resident")
@Slf4j
public class ResidentController {

	private ResidentService residentService;

	public ResidentController(ResidentService residentService) {
		this.residentService = residentService;
	}

	@GetMapping("/{nationalID}")
	public ResponseEntity<Resident> getResidentByNationalID(
			@PathVariable @Valid @Pattern(regexp = "[a-zA-Z-0-9] + ") String nationalID)
			throws OutputException, BarcodeException, MessagingException, DocumentException, JsonProcessingException {
		log.debug("searching for nationalID: " + nationalID);

		try {
			Optional<Resident> resident = residentService.getResidentByNationalID(nationalID);
			if (resident.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(resident.get());
		} catch (RuntimeException e) {
			log.error("error searching for resident by national ID: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
