package org.itech.locator.form.webapp.email.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.itech.locator.form.webapp.email.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

	@Value("${org.itech.locator.form.email.from:noreply@itech.org}")
	private String from;
	@Value("${org.itech.locator.form.email.bcc:noreply@itech.org}")
	private String bcc;

	private JavaMailSender javaMailSender;
	private Optional<VelocityEngine> velocityEngine;

	public EmailServiceImpl(JavaMailSender javaMailSender, Optional<VelocityEngine> velocityEngine) {
		this.javaMailSender = javaMailSender;
		this.velocityEngine = velocityEngine;
	}

	@Override
	@Async
	public void sendSimpleMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setBcc(bcc);
		message.setSubject(subject);
		message.setText(text);
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendMessageWithAttachment(String to, String subject, String text, String attachmentFileName,
			String pathToAttachment) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		message.addFrom(InternetAddress.parse(from));
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setBcc(bcc);
		helper.setSubject(subject);
		helper.setText(text);
		FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
		helper.addAttachment(attachmentFileName, file);
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendMessageWithAttachment(String to, String subject, String text, File attachment)
			throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		message.addFrom(InternetAddress.parse(from));
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setBcc(bcc);
		helper.setSubject(subject);
		helper.setText(text);
		helper.addAttachment(attachment.getName(), attachment);
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendMessageWithAttachment(String to, String subject, String text, Iterable<File> attachments)
			throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		message.addFrom(InternetAddress.parse(from));
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setBcc(bcc);
		helper.setSubject(subject);
		helper.setText(text);
		for (File attachment : attachments) {
			helper.addAttachment(attachment.getName(), attachment);
		}
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendMessageWithAttachment(String to, String subject, String text,
			Map<String, ByteArrayOutputStream> pdfsByName) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		message.addFrom(InternetAddress.parse(from));
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setBcc(bcc);
		helper.setSubject(subject);
		helper.setText(text);
		for (Entry<String, ByteArrayOutputStream> pdfByName : pdfsByName.entrySet()) {
			DataSource dataSource = new ByteArrayDataSource(pdfByName.getValue().toByteArray(), "application/pdf");
			helper.addAttachment(pdfByName.getKey(), dataSource);
		}
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

	@Override
	@Async
	public void sendTemplateMessageWithAttachment(String to, String subject, String templatePath,
			Map<String, Object> templateObjects,
			Map<String, ByteArrayOutputStream> pdfsByName) throws MessagingException {
		MimeMessage message = javaMailSender.createMimeMessage();
		message.addRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc));
		message.addFrom(InternetAddress.parse(from));
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setBcc(bcc);
		helper.setSubject(subject);

		VelocityContext velocityContext = new VelocityContext();
		for (Entry<String, Object> entry : templateObjects.entrySet()) {
			velocityContext.put(entry.getKey(), entry.getValue());
		}
		StringWriter stringWriter = new StringWriter();
		velocityEngine.orElseThrow().mergeTemplate(templatePath, "UTF-8", velocityContext, stringWriter);
		String text = stringWriter.toString();
		helper.setText(text);
		if (text.startsWith("<html") || text.startsWith("<!DOCTYPE html")) {
			helper.setText(text, true);
		} else {
			helper.setText(text);
		}

		for (Entry<String, ByteArrayOutputStream> pdfByName : pdfsByName.entrySet()) {
			DataSource dataSource = new ByteArrayDataSource(pdfByName.getValue().toByteArray(), "application/pdf");
			helper.addAttachment(pdfByName.getKey(), dataSource);
		}
		try {
			javaMailSender.send(message);
		} catch (MailException e) {
			log.error("error sending email to " + to + ": " + e.getMessage(), e);
		}
	}

}
