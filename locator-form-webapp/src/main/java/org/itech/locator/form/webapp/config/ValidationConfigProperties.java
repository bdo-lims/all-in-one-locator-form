package org.itech.locator.form.webapp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@ConfigurationProperties(prefix = "org.itech.validation")
@Configuration
@Data
public class ValidationConfigProperties {

	String testKitIdRegex = "^[0-9]{20}$";


}

