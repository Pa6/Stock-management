package com.productmanagement.security;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {

		if (authException != null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			PrintWriter writer = response.getWriter();
			writer.println("Unauthorized Access: " + authException.getMessage());
		} else {

			int statusCode = response.getStatus();

			if (statusCode == HttpServletResponse.SC_NOT_FOUND) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				PrintWriter writer = response.getWriter();
				writer.println("Resource Not Found");
			} else {
				// Default handling for other errors
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				PrintWriter writer = response.getWriter();
				writer.println("An error occurred");
			}
		}
	}
}
