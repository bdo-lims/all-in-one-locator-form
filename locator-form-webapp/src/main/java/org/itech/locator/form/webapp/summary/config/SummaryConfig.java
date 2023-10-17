package org.itech.locator.form.webapp.summary.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "org.itech.locator.form.summary")
@Data
public class SummaryConfig {

	public enum BarcodeType {
		QR, BAR_128
	}

	private BarcodeType barcodeType = BarcodeType.BAR_128;

}
