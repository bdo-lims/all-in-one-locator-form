package org.itech.locator.form.webapp.security;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

import org.hl7.fhir.r4.model.Task;
import org.itech.locator.form.webapp.api.dto.LocatorFormDTO;
import org.itech.locator.form.webapp.fhir.service.FhirPersistingService;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private FhirPersistingService fhirPersistingService;

	public AuthenticationProvider(ObjectMapper objectMapper, FhirPersistingService fhirPersistingService) {
		this.objectMapper = objectMapper;
		this.fhirPersistingService = fhirPersistingService;
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {
	}

	@Override
	protected UserDetails retrieveUser(String userName,
			UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken) throws AuthenticationException {

		String token = (String) usernamePasswordAuthenticationToken.getCredentials();

		Optional<Task> task = fhirPersistingService.getTaskFromServiceRequest(userName);
		LocatorFormDTO locatorFormDTO;
		try {
			locatorFormDTO = objectMapper.readValue(task
					.orElseThrow(
							() -> new UsernameNotFoundException("Cannot find user with serviceRequest " + userName))
					.getDescription(), LocatorFormDTO.class);
		} catch (JsonProcessingException e) {
			log.error("could not parse json of the locator form");
			throw new AccountExpiredException("locator form has changed, so this 'account' has expired");
		}

		if (hasAccess(locatorFormDTO,
				new SummaryAccessInfo(userName, token))) {
			SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
			return new User(userName, userName, true, true, true, true, Arrays.asList(authority));
		} else {
			throw new BadCredentialsException("Password was incorrect");
		}
	}

	private boolean hasAccess(LocatorFormDTO locatorFormDTO, SummaryAccessInfo Info) {
		String hash;
		try {
			hash = SecurityUtil.getSHA256Hash(objectMapper.writeValueAsString(locatorFormDTO));
		} catch (NoSuchAlgorithmException | JsonProcessingException e) {
			log.error("could not create access token for summary for ServiceRequest: "
					+ locatorFormDTO.getServiceRequestId());
			return false;
		}
		return hash.equals(Info.getPass());
	}
}
