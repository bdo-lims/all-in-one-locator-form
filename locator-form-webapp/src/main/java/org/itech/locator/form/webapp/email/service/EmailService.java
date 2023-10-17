package org.itech.locator.form.webapp.email.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

import javax.mail.MessagingException;

public interface EmailService {

	void sendSimpleMessage(String to, String subject, String text);

	void sendMessageWithAttachment(String to, String subject, String text, File attachment) throws MessagingException;

	void sendMessageWithAttachment(String to, String subject, String text, Iterable<File> attachment)
			throws MessagingException;

	void sendMessageWithAttachment(String to, String subject, String text, String attachmentFileName,
			String pathToAttachment) throws MessagingException;

	void sendMessageWithAttachment(String to, String subject, String text,
			Map<String, ByteArrayOutputStream> pdfsByName) throws MessagingException;

	void sendTemplateMessageWithAttachment(String to, String subject, String templatePath,
			Map<String, Object> templateObjects, Map<String, ByteArrayOutputStream> pdfsByName)
			throws MessagingException;

}
