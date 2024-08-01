package com.messenger_server.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.messenger_server.service.CustomUserDetailsService;
import com.messenger_server.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	private static final List<String> EXCLUDE_URLS = List.of("/auth/register", "/auth/login", "/friend-requests/notifications");

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
		final String requestURI = request.getRequestURI();
		logger.info("Request URI: " + requestURI);

		if (EXCLUDE_URLS.contains(requestURI)) {
			chain.doFilter(request, response);
			return;
		}

		String requestTokenHeader = request.getHeader("Authorization");
		if (requestTokenHeader == null) {
			requestTokenHeader = request.getParameter("token");
		}

		System.out.println("Authorization Header: " + requestTokenHeader);

		String username = null;
		String jwtToken = null;

		if (requestTokenHeader != null) {
			if (requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
			} else {
				jwtToken = requestTokenHeader;
			}

			try {
				username = jwtUtil.extractUsername(jwtToken);
			} catch (TokenExpiredException e) {
				System.out.println("JWT Token has expired");
			} catch (JWTVerificationException e) {
				System.out.println("Invalid JWT Token");
			}
		} else {
			logger.warn("JWT Token does not begin with Bearer String");
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

			if (jwtUtil.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}
}
