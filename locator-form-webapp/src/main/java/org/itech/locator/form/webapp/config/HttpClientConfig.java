package org.itech.locator.form.webapp.config;

import java.util.Optional;

import javax.net.ssl.SSLContext;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.itech.soap.infohighway.config.InfoHighwayConfigProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.ws.transport.http.HttpComponentsMessageSender.RemoveSoapHeadersInterceptor;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class HttpClientConfig {

	@Value("${server.ssl.trust-store}")
	private Resource trustStore;

	@Value("${server.ssl.trust-store-password}")
	private char[] trustStorePassword;

	@Value("${server.ssl.backend.key-store}")
	private Resource keyStore;

	@Value("${server.ssl.backend.key-store-password}")
	private char[] keyStorePassword;

	@Value("${server.ssl.backend.key-password}")
	private char[] keyPassword;

	@Value("${org.openhim.basicauth.username:}")
	private String openHimUsername;

	@Value("${org.openhim.basicauth.password:}")
	private String openHimPassword;

	private InfoHighwayConfigProperties infoHighwayConfigProperties;

	public HttpClientConfig(InfoHighwayConfigProperties infoHighwayConfigProperties) {
		this.infoHighwayConfigProperties = infoHighwayConfigProperties;
	}

	public SSLConnectionSocketFactory sslConnectionSocketFactory() throws Exception {
		return new SSLConnectionSocketFactory(sslContext());
	}

	public SSLContext sslContext() throws Exception {
		return SSLContextBuilder.create().loadKeyMaterial(keyStore.getFile(), keyStorePassword, keyPassword)
				.loadTrustMaterial(trustStore.getFile(), trustStorePassword).build();
	}

	@Bean
	@Primary
	public HttpClient httpClient() throws Exception {
		log.debug("creating httpClient");
		CloseableHttpClient httpClient = HttpClientBuilder.create()//
				.setSSLSocketFactory(sslConnectionSocketFactory())//
				.build();
		return httpClient;
	}

	@Bean("openHimCredentials")
	@ConditionalOnProperty(prefix = "org.openhim.basicauth", name = "username")
	public UsernamePasswordCredentials openHimCredentials() {
		return new UsernamePasswordCredentials(openHimUsername, openHimPassword);
	}

	@Bean("soapHttpClient")
	public HttpClient soapHttpClient(@Qualifier("openHimCredentials") Optional<UsernamePasswordCredentials> credentials)
			throws Exception {
		log.debug("creating soap httpClient");
		HttpClientBuilder clientBuilder = HttpClientBuilder.create()//
				.setSSLSocketFactory(sslConnectionSocketFactory())//
				.addInterceptorFirst(new RemoveSoapHeadersInterceptor());
		if (infoHighwayConfigProperties.getConnectionTimeout() >= 0
				|| infoHighwayConfigProperties.getSocketTimeout() >= 0) {
			RequestConfig.Builder requestBuilder = RequestConfig.custom();
			if (infoHighwayConfigProperties.getConnectionTimeout() >= 0) {
				requestBuilder.setConnectTimeout(infoHighwayConfigProperties.getConnectionTimeout());
			}
			if (infoHighwayConfigProperties.getSocketTimeout() >= 0) {
				requestBuilder.setConnectionRequestTimeout(infoHighwayConfigProperties.getSocketTimeout());
			}
			clientBuilder.setDefaultRequestConfig(requestBuilder.build());
		}
		if (credentials.isPresent()) {
			log.debug("OpenHIM authentication supplied, loading into httpClient for user:"
					+ credentials.get().getUserName());
			CredentialsProvider provider = new BasicCredentialsProvider();
			provider.setCredentials(AuthScope.ANY, credentials.get());
			clientBuilder.setDefaultCredentialsProvider(provider);
		} else {
			log.debug("No OpenHIM authentication supplied, httpClient will have no authentication");
		}
		CloseableHttpClient httpClient = clientBuilder.build();
		return httpClient;
	}

}
