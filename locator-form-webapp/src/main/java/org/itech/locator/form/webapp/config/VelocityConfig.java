package org.itech.locator.form.webapp.config;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VelocityConfig {


	@Value("${org.itech.velocity.resourcePath:/var/lib/locatorform/velocity/}")
	private String velocityResourcePath;

	@Bean
	@ConditionalOnProperty(prefix = "org.itech.velocity", name = "useVelocity", havingValue = "true")
	public VelocityEngine velocityEngine() {
		VelocityEngine engine = new VelocityEngine();
		ResourceLoader resourceLoader = new FileResourceLoader();

		engine.setProperty("resource.loader", "file");
		engine.setProperty("file.resource.loader.instance", resourceLoader);
		engine.setProperty("file.resource.loader.path", velocityResourcePath);

		return engine;
	}
}
