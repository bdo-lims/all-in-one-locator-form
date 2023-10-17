package org.itech.locator.form.webapp.summary.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.api.dto.Traveller;
import org.itech.locator.form.webapp.country.Country;
import org.itech.locator.form.webapp.summary.LabelContentPair;
import org.itech.locator.form.webapp.summary.config.SummaryConfig;
import org.itech.locator.form.webapp.summary.service.SummaryService;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

@Service
@Slf4j
public class SummaryServiceImpl implements SummaryService {

	private SummaryConfig summaryConfig;

	public SummaryServiceImpl(SummaryConfig summaryConfig) {
		this.summaryConfig = summaryConfig;
	}

	private Country[] countries;

	float marginSize = 36;
	float headerPadding = 10;
	float headerSize = 16;
	float headerHeight = headerSize + headerPadding;

	Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, headerSize);
	Font sectionsFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
	Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11.5f, BaseColor.BLACK);

	@Override
	public ByteArrayOutputStream generateBarcodeFile(String barcodeLabel, String barcodeContent)
			throws OutputException, BarcodeException, DocumentException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Document document = new Document(new Rectangle(PageSize.LETTER));
		PdfWriter writer = PdfWriter.getInstance(document, stream);
		document.open();
		Chunk chunk = new Chunk(barcodeLabel, bodyFont);
		document.add(chunk);
		switch (summaryConfig.getBarcodeType()) {
		case QR:
			BarcodeQRCode barcodeQRCode = new BarcodeQRCode(barcodeContent, 1000, 1000, null);
			Image codeQrImage = barcodeQRCode.getImage();
			codeQrImage.scaleAbsolute(100, 100);
			document.add(codeQrImage);
			break;
		case BAR_128:
			Barcode128 code128 = new Barcode128();
			code128.setGenerateChecksum(true);
			code128.setCode(barcodeContent);
			document.add(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
			break;
		default:
		}
		document.close();
		writer.close();

		return stream;
	}

	@Override
	public ByteArrayOutputStream generateSummaryFile(Map<String, LabelContentPair> idAndLabels, LocatorFormDTO dto)
			throws OutputException, BarcodeException, DocumentException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Document document = new Document(PageSize.LETTER);
		document.addTitle("Mauritius All-in-One Travel Digital Form");
		PdfWriter writer = PdfWriter.getInstance(document, stream);
		document.open();

		addHeader(document, writer);
		addBody(idAndLabels, dto, document, writer);

//		table = new PdfPTable(4);
//		table.setWidthPercentage(100);

		document.close();
		writer.close();

		return stream;
	}

	private void addHeader(Document document, PdfWriter writer) throws DocumentException {
		PdfPTable header = new PdfPTable(1);
		header.setWidthPercentage(100);
		// add text
		PdfPCell text = new PdfPCell(new Phrase("Mauritius All-in-One Travel Digital Form", headerFont));
		text.setPaddingBottom(headerPadding);
		text.setHorizontalAlignment(Element.ALIGN_CENTER);
		text.setBorder(Rectangle.NO_BORDER);
		text.setBorderColor(BaseColor.LIGHT_GRAY);
		header.addCell(text);
		document.add(header);
	}

	private void addBody(Map<String, LabelContentPair> idAndLabels, LocatorFormDTO dto, Document document,
			PdfWriter writer) throws DocumentException {
		PdfPTable table = new PdfPTable(4);

		addSectionCellToTable("Traveller ", 4, table);
		addCellToTable("Passenger Type: " + convertResdentToCitzen(Objects.toString(dto.getTravellerType(), "")), 4,
				table);
		addPersonalInformationToTable(dto, table);
		addHealthInformationToTable(dto, table);
		addCommonInformationToTable(dto, dto, table);
		addBarcodeLabelToTable(idAndLabels.get(dto.getServiceRequestId()), 4, table, writer);

		addTableToDocument(table, document, writer);
		document.newPage();

		for (Traveller companion : dto.getFamilyTravelCompanions()) {
			table = new PdfPTable(4);

			addPersonalInformationToTable(companion, table);
			addCommonInformationToTable(dto, companion, table);
			addBarcodeLabelToTable(idAndLabels.get(companion.getServiceRequestId()), 4, table, writer);
			addTableToDocument(table, document, writer);
			document.newPage();
		}

		for (Traveller companion : dto.getNonFamilyTravelCompanions()) {
			table = new PdfPTable(4);

			addPersonalInformationToTable(companion, table);
			addCommonInformationToTable(dto, companion, table);
			addBarcodeLabelToTable(idAndLabels.get(companion.getServiceRequestId()), 4, table, writer);
			addTableToDocument(table, document, writer);
			document.newPage();
		}
	}

	private void addTableToDocument(PdfPTable table, Document document, PdfWriter writer) throws DocumentException {
		table.setTotalWidth(PageSize.LETTER.getWidth() - (marginSize * 2));
		table.setLockedWidth(true);
		if (table.getTotalHeight() + ((marginSize * 2) + headerHeight) > PageSize.LETTER.getHeight()) {
			PdfContentByte canvas = writer.getDirectContent();
			PdfTemplate template = canvas.createTemplate(table.getTotalWidth(), table.getTotalHeight());
			table.writeSelectedRows(0, -1, 0, table.getTotalHeight(), template);
			Image img = Image.getInstance(template);
			img.scaleAbsolute(PageSize.LETTER.getWidth() - (marginSize * 2),
					PageSize.LETTER.getHeight() - (marginSize * 2) - headerHeight);
			img.setAbsolutePosition(marginSize, marginSize);

			document.add(img);
		} else {
			table.setWidthPercentage(100);
			document.add(table);
		}

	}

	private void addSectionCellToTable(String label, int columns, PdfPTable table) {
		PdfPCell hcell = new PdfPCell(new Phrase(label, sectionsFont));
		hcell.setColspan(columns);
		table.addCell(hcell);
	}

	private void addCellToTable(String label, int columns, PdfPTable table) {
		PdfPCell hcell = new PdfPCell(new Phrase(label, bodyFont));
		hcell.setColspan(columns);
		table.addCell(hcell);
	}

	private void addBarcodeLabelToTable(LabelContentPair pair, int columns, PdfPTable table, PdfWriter writer)
			throws BadElementException {
		PdfPCell cell = new PdfPCell();
		cell.setColspan(4);
		Chunk chunk = new Chunk(pair.getLabel() + ": " + pair.getBarcodeContent(), sectionsFont);
		cell.addElement(chunk);
		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);

		cell = new PdfPCell();
		cell.setColspan(4);

		switch (summaryConfig.getBarcodeType()) {
		case QR:
			BarcodeQRCode barcodeQRCode = new BarcodeQRCode(pair.getBarcodeContent(), 1000, 1000, null);
			Image codeQrImage = barcodeQRCode.getImage();
			codeQrImage.scaleAbsolute(100, 100);
			cell.addElement(codeQrImage);
			break;
		case BAR_128:
			Barcode128 code128 = new Barcode128();
			code128.setGenerateChecksum(true);
			code128.setCode(pair.getBarcodeContent());
			cell.addElement(code128.createImageWithBarcode(writer.getDirectContent(), null, null));
			break;
		default:
		}

		cell.setBorder(Rectangle.NO_BORDER);
		table.addCell(cell);
	}

	private void addPersonalInformationToTable(Traveller traveller, PdfPTable table) {
		addSectionCellToTable("Personal Information ", 4, table);
		if (traveller instanceof LocatorFormDTO) {
			addCellToTable("Title: " + Objects.toString(((LocatorFormDTO) traveller).getTitle(), ""), 1, table);
		} else {
			addCellToTable("", 1, table);
		}
		addCellToTable("Last (Family) Name: " + Objects.toString(traveller.getLastName(), ""), 1, table);
		addCellToTable("First (Given) Name: " + Objects.toString(traveller.getFirstName(), ""), 1, table);
		addCellToTable("Middle Initial: " + Objects.toString(traveller.getMiddleInitial(), ""), 1, table);
		addCellToTable("Sex: " + Objects.toString(traveller.getSex(), ""), 1, table);
		addCellToTable("Date Of Birth: " + Objects.toString(traveller.getDateOfBirth(), ""), 1, table);

		addCellToTable(
				"Country Of Birth: " + Objects.toString(getCountryLabelForValue(traveller.getCountryOfBirth()), ""), 1,
				table);
		addCellToTable(
				"Passport Issue Country: "
						+ Objects.toString(getCountryLabelForValue(traveller.getCountryOfPassportIssue()), ""),
				1, table);

		addCellToTable("Passport Expiry Date: " + Objects.toString(traveller.getPassportExpiryDate(), ""), 1, table);
		addCellToTable("Passport Number: " + Objects.toString(traveller.getPassportNumber(), ""), 1, table);
		addCellToTable("Proposed Length of Stay in Mauritius (days): "
				+ Objects.toString(((LocatorFormDTO) traveller).getLengthOfStay(), ""), 1, table);
		addCellToTable(
				"Port Of Embarkation: " + Objects.toString(((LocatorFormDTO) traveller).getPortOfEmbarkation(), ""), 1,
				table);
		addCellToTable(
				"Nationalities: " + StringUtils
						.join(getPassengerNationalities(((LocatorFormDTO) traveller).getPassengerNationality()), ", "),
				2, table);
		addCellToTable(
				"Countries visited during last 6 months: " + StringUtils
						.join(getCountriesVisitedByName(((LocatorFormDTO) traveller).getCountriesVisited()), ", "),
				2, table);
	}

	private void addHealthInformationToTable(LocatorFormDTO dto, PdfPTable table) {
		// addHeaderCellToTable("Health Information ", 4, table);
		addSectionCellToTable("Have you experienced any of the following within the past 14 days? ", 4, table);
		addCellToTable("Fever: " + convertBoolean(Objects.toString(dto.getFever(), "")), 1, table);
		addCellToTable("Sore Throat: " + convertBoolean(Objects.toString(dto.getSoreThroat(), "")), 1, table);
		addCellToTable("Joint Pain: " + convertBoolean(Objects.toString(dto.getJointPain(), "")), 1, table);
		addCellToTable("Cough: " + convertBoolean(Objects.toString(dto.getCough(), "")), 1, table);
		addCellToTable(
				"Breathing Difficulties: " + convertBoolean(Objects.toString(dto.getBreathingDifficulties(), "")), 1,
				table);
		addCellToTable("Rash: " + convertBoolean(Objects.toString(dto.getRash(), "")), 1, table);
		addCellToTable(
				"Loss of Sense of Smell or Taste: " + convertBoolean(Objects.toString(dto.getSmellOrTaste(), "")), 2,
				table);

//		addHeaderCellToTable("Other Health Questions:  ", 4, table);
		addCellToTable("Possible contact with COVID 19: " + convertBoolean(Objects.toString(dto.getContact(), "")), 4,
				table);
		addCellToTable("Have you tested positive for Covid-19 in the past 7 days? "
				+ convertBoolean(Objects.toString(dto.getHadCovidBefore(), "")), 4, table);

		addSectionCellToTable("Vaccine ", 4, table);
		addCellToTable("Vaccinated: " + convertBoolean(Objects.toString(dto.getVaccinated(), "")), 4, table);
		addCellToTable("First Vaccine: " + Objects.toString(dto.getFirstVaccineName(), ""), 1, table);
		addCellToTable("First Vaccine Date: " + Objects.toString(dto.getDateOfFirstDose(), ""), 1, table);
		addCellToTable("Second Vaccine: " + Objects.toString(dto.getSecondVaccineName(), ""), 1, table);
		addCellToTable("Second Vaccine Date: " + Objects.toString(dto.getDateOfSecondDose(), ""), 1, table);
	}

	private void addCommonInformationToTable(LocatorFormDTO dto, Traveller traveller, PdfPTable table) {
		addSectionCellToTable("Flight Information ", 4, table);
		addCellToTable("Airline: " + Objects.toString(dto.getAirlineName(), ""), 1, table);
		addCellToTable("Flight: " + Objects.toString(dto.getFlightNumber(), ""), 1, table);
		addCellToTable("Seat: " + Objects.toString(traveller.getSeatNumber(), ""), 1, table);
		addCellToTable("Date Of Arrival: " + Objects.toString(dto.getArrivalDate(), ""), 1, table);
		addCellToTable("Purpose of Visit: " + Objects.toString(dto.getVisitPurpose(), ""), 2, table);
		// just added to properly format table
		addCellToTable("Time Of Arrival: " + Objects.toString(dto.getArrivalTime(), ""), 2, table);

		addSectionCellToTable("Contact Info ", 4, table);
		addCellToTable("Email Address: " + Objects.toString(dto.getEmail(), ""), 2, table);
		addCellToTable("Mobile Phone: " + Objects.toString(dto.getMobilePhone(), ""), 2, table);
		addCellToTable("Fixed Phone:" + Objects.toString(dto.getFixedPhone(), ""), 2, table);
		addCellToTable("Business Phone:" + Objects.toString(dto.getBusinessPhone(), ""), 2, table);

		addSectionCellToTable("Permanent Address ", 4, table);
		addCellToTable("Number and Street: " + Objects.toString(dto.getPermanentAddress().getNumberAndStreet(), ""), 1,
				table);
		addCellToTable("Apartment Number: " + Objects.toString(dto.getPermanentAddress().getApartmentNumber(), ""), 1,
				table);
		addCellToTable("City: " + Objects.toString(dto.getPermanentAddress().getCity(), ""), 1, table);
		addCellToTable("State/Province: " + Objects.toString(dto.getPermanentAddress().getStateProvince(), ""), 1,
				table);
		addCellToTable(
				"Country: " + Objects.toString(getCountryLabelForValue(dto.getPermanentAddress().getCountry()), ""), 1,
				table);
		addCellToTable("Zip/Postal Code: " + Objects.toString(dto.getPermanentAddress().getZipPostalCode(), ""), 3,
				table);

		addSectionCellToTable("Temporary Address ", 4, table);
		addCellToTable("Hotel Name: " + Objects.toString(dto.getTemporaryAddress().getHotelName(), ""), 2, table);
		addCellToTable("Number and Street: " + Objects.toString(dto.getTemporaryAddress().getNumberAndStreet(), ""), 2,
		    table);
		addCellToTable("Apartment Number: " + Objects.toString(dto.getTemporaryAddress().getApartmentNumber(), ""), 1,
		    table);
		addCellToTable("Quarantine Site: " + Objects.toString(dto.getTemporaryAddress().getQuarantineSite(), ""), 1, table);
		addCellToTable("Local Phone: " + Objects.toString(dto.getTemporaryAddress().getLocalPhone(), ""), 2,
		    table);	
			
		addSectionCellToTable("Contact person in Mauritius", 4, table);
		addCellToTable("Last (Family) Name: " + Objects.toString(dto.getContactPerson().getLastName(), ""), 2, table);
		addCellToTable("First (Given) Name: " + Objects.toString(dto.getContactPerson().getFirstName(), ""), 2, table);
		addCellToTable("Address: " + Objects.toString(dto.getContactPerson().getAddress(), ""), 1, table);
		addCellToTable("Email: " + Objects.toString(dto.getContactPerson().getEmail(), ""), 1, table);
		addCellToTable("Mobile Phone: " + Objects.toString(dto.getContactPerson().getMobilePhone(), ""), 2, table);

		addSectionCellToTable("Emergency Contact ", 4, table);
		addCellToTable("Last (Family) Name: " + Objects.toString(dto.getEmergencyContact().getLastName(), ""), 1,
				table);
		addCellToTable("First (Given) Name: " + Objects.toString(dto.getEmergencyContact().getFirstName(), ""), 1,
				table);
		addCellToTable("Address: " + Objects.toString(dto.getEmergencyContact().getAddress(), ""), 1, table);
		addCellToTable(
				"Country: " + Objects.toString(getCountryLabelForValue(dto.getEmergencyContact().getCountry()), ""), 1,
				table);
		addCellToTable("Mobile Phone: " + Objects.toString(dto.getEmergencyContact().getMobilePhone(), ""), 4, table);

		if (dto != traveller) {
			addSectionCellToTable("Primary Travel Companion ", 4, table);
			addCellToTable("Last (Family) Name: " + Objects.toString(dto.getLastName(), ""), 1, table);
			addCellToTable("First (Given) Name: " + Objects.toString(dto.getFirstName(), ""), 1, table);
			addCellToTable("Sex: " + Objects.toString(dto.getSex(), ""), 1, table);
			addCellToTable("Seat: " + Objects.toString(dto.getSeatNumber(), ""), 1, table);
			addCellToTable("Date Of Birth: " + Objects.toString(dto.getDateOfBirth(), ""), 1, table);
			addCellToTable("Profession: " + Objects.toString(dto.getProfession(), ""), 1, table);
			addCellToTable(
					"Country Of Birth: " + Objects.toString(getCountryLabelForValue(dto.getCountryOfBirth()), ""), 1,
					table);
			addCellToTable("Passport Number: " + Objects.toString(dto.getPassportNumber(), ""), 2, table);
		}

		if (dto.getFamilyTravelCompanions().length != 0) {
			addSectionCellToTable("Travel Companions Family ", 4, table);
		}
		for (Traveller companion : dto.getFamilyTravelCompanions()) {
			if (companion != traveller) {
				addCellToTable("Last (Family) Name: " + Objects.toString(companion.getLastName(), ""), 1, table);
				addCellToTable("First (Given) Name: " + Objects.toString(companion.getFirstName(), ""), 1, table);
				addCellToTable("Sex: " + Objects.toString(companion.getSex(), ""), 1, table);
				addCellToTable("Seat: " + Objects.toString(companion.getSeatNumber(), ""), 1, table);
				addCellToTable("Date Of Birth: " + Objects.toString(companion.getDateOfBirth(), ""), 1, table);
				addCellToTable(
						"Nationality: " + Objects.toString(getCountryLabelForValue(companion.getNationality()), ""), 1,
						table);
				addCellToTable("Passport Number: " + Objects.toString(companion.getPassportNumber(), ""), 2, table);
			}
		}

		if (dto.getNonFamilyTravelCompanions().length != 0) {
			addSectionCellToTable("Travel Companions Non-Family ", 4, table);
		}
		for (Traveller companion : dto.getNonFamilyTravelCompanions()) {
			if (companion != traveller) {
				addCellToTable("Last (Family) Name: " + Objects.toString(companion.getLastName(), ""), 1, table);
				addCellToTable("First (Given) Name: " + Objects.toString(companion.getFirstName(), ""), 1, table);
				addCellToTable("Sex: " + Objects.toString(companion.getSex(), ""), 1, table);
				addCellToTable("Seat: " + Objects.toString(companion.getSeatNumber(), ""), 1, table);
				addCellToTable("Date Of Birth: " + Objects.toString(companion.getDateOfBirth(), ""), 1, table);
				addCellToTable(
						"Nationality: " + Objects.toString(getCountryLabelForValue(companion.getNationality()), ""), 1,
						table);
				addCellToTable("Passport Number: " + Objects.toString(companion.getPassportNumber(), ""), 2, table);
			}
		}

	}

	private List<String> getCountriesVisitedByName(Collection<String> countriesVisited) {
		List<String> countriesVisitedByName = new ArrayList<>();
		for (String countryVisited : countriesVisited) {
			countriesVisitedByName.add(getCountryLabelForValue(countryVisited));
		}
		return countriesVisitedByName;
	}

	private List<String> getPassengerNationalities(Collection<String> nationalities) {
		List<String> nationalitiesByName = new ArrayList<>();
		for (String nationality : nationalities) {
			nationalitiesByName.add(getCountryLabelForValue(nationality));
		}
		return nationalitiesByName;
	}

	private List<String> getCountriesVisitedByName(String[] countriesVisited) {
		List<String> countriesVisitedByName = new ArrayList<>();
		for (String countryVisited : countriesVisited) {
			countriesVisitedByName.add(getCountryLabelForValue(countryVisited));
		}
		return countriesVisitedByName;
	}

	private Country[] getCountries() {
		if (countries == null) {
			ObjectMapper mapper = new ObjectMapper();
			ClassLoader cLoader = this.getClass().getClassLoader();
			try {
				countries = mapper.readValue(cLoader.getResourceAsStream("countries.js"), Country[].class);
			} catch (IOException e) {
				log.error("could not parse countries file, using values instead of label");
				countries = new Country[0];
			}
		}
		return countries;
	}

	private String getCountryLabelForValue(String value) {
		if (value == null) {
			return null;
		}
		for (Country country : getCountries()) {
			if (country.getValue().equals(value)) {
				return country.getLabel();
			}
		}
		return value;
	}

	private String convertBoolean(String bool) {
		return bool.equals("true") ? "Yes" : "No";
	}

	private String convertResdentToCitzen(String residentType) {
		return residentType.equals("resident") ? "Citizen" : "Non Citizen";
	}
}
