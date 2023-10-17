package org.itech.locator.form.webapp.resident.service.impl;

import java.util.List;
import java.util.Optional;

import org.itech.locator.form.webapp.api.dto.Resident;
import org.itech.locator.form.webapp.resident.service.ResidentService;
import org.itech.soap.infohighway.QueryResponse;
import org.itech.soap.infohighway.QwsOutputValues;
import org.itech.soap.infohighway.client.InfoHighwayClient;
import org.springframework.stereotype.Service;

@Service
public class ResidentServiceImpl implements ResidentService {

	private InfoHighwayClient infoHighwayClient;

	public ResidentServiceImpl(InfoHighwayClient infoHighwayClient) {
		this.infoHighwayClient = infoHighwayClient;
	}

	@Override
	public Optional<Resident> getResidentByNationalID(String nationalID) {
		QueryResponse response = infoHighwayClient.getClientByNationalID(nationalID);
		if (response == null) {
			return Optional.empty();
		}
		for (QwsOutputValues value : response.getReturn().getValues()) {
			return Optional.of(getResident(response.getReturn().getFields(), value.getValue()));
		}
		return Optional.empty();
	}

	private Resident getResident(List<String> fields, List<String> value) {
		Resident resident = new Resident();
		for (int i = 0; i < fields.size(); ++i) {
			addField(resident, fields.get(i), value.get(i));
		}
		return resident;
	}

	private void addField(Resident resident, String field, String value) {
		switch (field) {
		case "NIC_NUMBER":
			resident.setNationalID(value);
			break;
		case "SURNAME":
			resident.setLastInitial(value.charAt(0));
			break;
		case "FIRST_NAME":
			resident.setFirstName(value);
			break;
		case "SEX":
			break;
		case "BIRTH_DATE":
			break;
		case "FLAT_NO_APARTMENT_NAME":
			break;
		case "STREET_NAME":
			break;
		case "POSTAL_CODE":
			break;
		case "TOWN_VILLAGE":
			break;
		case "LOCALITY":
			break;
		case "DISTRICT":
			break;
		default:
			break;
		}
	}

}
