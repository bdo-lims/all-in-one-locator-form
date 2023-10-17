package org.itech.locator.form.webapp.resident.service;

import java.util.Optional;

import org.itech.locator.form.webapp.api.dto.Resident;

public interface ResidentService {

	Optional<Resident> getResidentByNationalID(String nationalID);

}
