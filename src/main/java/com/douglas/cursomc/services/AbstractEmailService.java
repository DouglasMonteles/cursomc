package com.douglas.cursomc.services;

import java.util.Date;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.douglas.cursomc.domain.Cliente;
import com.douglas.cursomc.domain.Pedido;

public abstract class AbstractEmailService implements EmailService {

	@Value("${default.sender}")
	private String sender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Override
	public void sendOrderConfirmationEmail(Pedido obj) {
		SimpleMailMessage message = prepareSimpleMailMessageFromPedido(obj);
		sendEmail(message);
	}

	protected SimpleMailMessage prepareSimpleMailMessageFromPedido(Pedido obj) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(obj.getCliente().getEmail());
		message.setFrom(sender);
		message.setSubject("Pedido confirmado! Código: " + obj.getId());
		message.setSentDate(new Date(System.currentTimeMillis()));
		message.setText(obj.toString());
		return message;
	}
	
	protected String htmlFromTemplatePedido(Pedido obj) {
		Context context = new Context();
		context.setVariable("pedido", obj);
		
		return templateEngine.process("email/confirmacaoPedido", context);
	}
	
	@Override
	public void sendOrderConfirmationHtmlEmail(Pedido obj) {
		try {
			MimeMessage message = prepareMimeMessage(obj);
			sendHtmlEmail(message);
		} catch (MessagingException e) {
			sendOrderConfirmationEmail(obj);
		}
	}

	protected MimeMessage prepareMimeMessage(Pedido obj) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		helper.setTo(obj.getCliente().getEmail());
		helper.setFrom(sender);
		helper.setSubject("Pedido confirmado! Código: " + obj.getId());
		helper.setSentDate(new Date(System.currentTimeMillis()));
		helper.setText(htmlFromTemplatePedido(obj), true);
		
		return message;
	}
	
	@Override
	public void sendNewPasswordEmail(Cliente cliente, String newPass) {
		SimpleMailMessage sm = prepareNewPasswordEmail(cliente, newPass);
		sendEmail(sm);
	}

	protected SimpleMailMessage prepareNewPasswordEmail(Cliente cliente, String newPass) {
		SimpleMailMessage sm = new SimpleMailMessage();
		sm.setTo(cliente.getEmail());
		sm.setFrom(sender);
		sm.setSubject("Solicitação de nova senha");
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText("Nova senha: " + newPass);
		return sm;
	}
	
}
