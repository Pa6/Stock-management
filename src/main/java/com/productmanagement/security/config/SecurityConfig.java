package com.productmanagement.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.productmanagement.security.JwtAuthenticationEntryPoint;
import com.productmanagement.security.JwtAuthenticationFilter;

@Component
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Autowired
	private JwtAuthenticationEntryPoint entryPoint;
	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf(csrf -> csrf.disable()).cors(cors -> cors.disable()).authorizeHttpRequests(
				auth -> auth
				.requestMatchers("/**").permitAll()
				.requestMatchers("/graphql/**").authenticated())
//				.requestMatchers("/auth/login").permitAll()
//				.requestMatchers("/h2-console/**").permitAll()
//				.requestMatchers("/h2-ui/**").permitAll())
		.exceptionHandling(ex->ex.authenticationEntryPoint(entryPoint))
		.sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
		
		httpSecurity.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);

		return httpSecurity.build();
	}
}
