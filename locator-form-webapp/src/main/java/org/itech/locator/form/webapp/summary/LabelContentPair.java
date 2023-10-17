package org.itech.locator.form.webapp.summary;

import lombok.Data;

@Data
public class LabelContentPair {

	private String label;

	private String barcodeContent;

	public LabelContentPair(String label, String barcodeContent) {
		this.label = label;
		this.barcodeContent = barcodeContent;
	}


}
