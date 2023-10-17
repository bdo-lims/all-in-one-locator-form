package org.itech.locator.form.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("org.itech")
@PropertySources({ @PropertySource("classpath:application.properties"),
		@PropertySource(value = "file:/var/lib/locatorform/app.properties", ignoreResourceNotFound = true) })
@EnableAsync
public class LocatorFormWebApp {

	public static void main(String[] args) {
		SpringApplication.run(LocatorFormWebApp.class, args);
	}

}
