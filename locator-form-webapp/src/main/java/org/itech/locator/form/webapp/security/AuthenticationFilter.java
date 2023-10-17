package org.itech.locator.form.webapp.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.itech.locator.form.webapp.summary.security.SummaryAccessInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	private static final String AUTHORIZATION = "Authorization";

	AuthenticationFilter(final RequestMatcher requiresAuth) {
		super(requiresAuth);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) throws AuthenticationException, IOException, ServletException {

		Optional<String> tokenParam = Optional.ofNullable(httpServletRequest.getHeader(AUTHORIZATION)); // Authorization:
		String path = httpServletRequest.getRequestURI();
		SummaryAccessInfo accessInfo = new SummaryAccessInfo(path.substring(Math.max(0, path.lastIndexOf("/") + 1)),
				StringUtils.removeStart(tokenParam.orElse(""), "Bearer").trim());
		Authentication requestAuthentication = new UsernamePasswordAuthenticationToken(accessInfo.getId(),
				accessInfo.getPass());
		return getAuthenticationManager().authenticate(requestAuthentication);

	}

	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain chain, final Authentication authResult) throws IOException, ServletException {
		SecurityContextHolder.getContext().setAuthentication(authResult);
		chain.doFilter(request, response);
	}

}