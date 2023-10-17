package org.itech.locator.form.webapp.summary.service;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.summary.LabelContentPair;

import com.itextpdf.text.DocumentException;

import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

public interface SummaryService {

	ByteArrayOutputStream generateBarcodeFile(String barcodeLabel, String barcodeContent)
			throws OutputException, BarcodeException, DocumentException;

	ByteArrayOutputStream generateSummaryFile(Map<String, LabelContentPair> idAndLabels, LocatorFormDTO locatorFormDto)
			throws OutputException, BarcodeException, DocumentException;

}
