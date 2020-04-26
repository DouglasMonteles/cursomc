package com.douglas.cursomc.security;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.douglas.cursomc.dto.CredenciaisDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager manager;
	
	private JWTUtil jwtUtil;
	
	public JWTAuthenticationFilter(AuthenticationManager manager, JWTUtil jwtUtil) {
		this.manager = manager;
		this.jwtUtil = jwtUtil;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		try {
			CredenciaisDTO creds = new ObjectMapper()
					.readValue(request.getInputStream(), CredenciaisDTO.class);
			
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getSenha(), new ArrayList<>());
		
			Authentication auth = manager.authenticate(authToken);
			
			return auth;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, 
			FilterChain chain,
			Authentication auth) throws IOException, ServletException {
		
		String username = ((UserSS) auth.getPrincipal()).getUsername();
		String token = jwtUtil.generateToken(username);
		
		response.addHeader("Authorization", "Bearer " + token);
	}

}
