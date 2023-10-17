package org.itech.soap.infohighway.config;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "infohighway")
@Data
public class InfoHighwayConfigProperties {

	private URI uri;
	private String username;
	private char[] password;
	private int connectionTimeout = -1;
	private int socketTimeout = -1;

}
