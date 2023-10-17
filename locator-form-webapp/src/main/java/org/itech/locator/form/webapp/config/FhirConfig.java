package org.itech.locator.form.webapp.config;

import org.apache.http.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.FhirVersionEnum;
import ca.uhn.fhir.rest.client.apache.ApacheRestfulClientFactory;
import ca.uhn.fhir.rest.client.api.IRestfulClientFactory;

@Configuration
public class FhirConfig {

	HttpClient httpClient;

	public FhirConfig(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Bean
	public FhirContext fhirContext() {
		FhirContext fhirContext = new FhirContext(FhirVersionEnum.R4);
		configureFhirHttpClient(fhirContext);
		return fhirContext;
	}

	public void configureFhirHttpClient(FhirContext fhirContext) {
		try {
			IRestfulClientFactory clientFactory = new ApacheRestfulClientFactory(fhirContext);

			clientFactory.setHttpClient(httpClient);
			fhirContext.setRestfulClientFactory(clientFactory);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

