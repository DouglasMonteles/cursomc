package com.douglas.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import com.douglas.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido obj);
	
	void sendEmail(SimpleMailMessage msg);	
	
}
