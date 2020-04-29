package com.douglas.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.douglas.cursomc.domain.Cliente;
import com.douglas.cursomc.repositories.ClienteRepository;
import com.douglas.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired 
	private EmailService emailService;
	
	Random random = new Random();
	
	public void sendNewPassword(String email) {
		Cliente cliente = clienteRepository.findByEmail(email);
		if (cliente == null) {
			throw new ObjectNotFoundException("Email não encontrado");
		}
		String newPass = newPassword();
		cliente.setSenha(passwordEncoder.encode(newPass));
		
		clienteRepository.save(cliente);
		emailService.sendNewPasswordEmail(cliente, newPass);
	}

	private String newPassword() {
		char[] randomPassword = new char[10];
		
		for (int i = 0; i < randomPassword.length; i++) {
			randomPassword[i] = randomChar();
		}
		
		return new String(randomPassword);
	}

	private char randomChar() {
		int opt = random.nextInt(3);
		
		switch(opt) {
			case 0: // gera um digito
				return (char) (random.nextInt(10) + 48);	
				
			case 1: // gera uma letra maiuscula
				return (char) (random.nextInt(26) + 65);
			
			default: // gera uma letra minuscula
				return (char) (random.nextInt(26) + 97);
		}
	}
	
}
