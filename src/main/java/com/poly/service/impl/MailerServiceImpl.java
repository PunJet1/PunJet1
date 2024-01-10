package com.poly.service.impl;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Random;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.poly.entity.Mailer;
import com.poly.service.MailerService;

@Service
public class MailerServiceImpl implements MailerService {

	@Autowired
	private JavaMailSender sender;

	@Override
	public void send(Mailer mail) throws MessagingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom(mail.getFrom());
		helper.setTo(mail.getTo());
		helper.setSubject(mail.getSubject());
		helper.setText(mail.getBody(), true);
		helper.setReplyTo(mail.getFrom());
		String[] cc = mail.getCc();
		if (cc != null && cc.length > 0) {
			helper.setCc(cc);
		}
		String[] bcc = mail.getBcc();
		if (bcc != null && bcc.length > 0) {
			helper.setBcc(bcc);
		}
		String[] attachments = mail.getAttachments();
		if (attachments != null && attachments.length > 0) {
			for (String path : attachments) {
				File file = new File(path);
				helper.addAttachment(file.getName(), file);
			}
		}
		sender.send(message);
	}

	@Override
	public void send(String to, String subject, String body) throws MessagingException {
		this.send(new Mailer(to, subject, body));
	}

	@Override
	public void sendEmail(String email, String link) throws MessagingException, UnsupportedEncodingException {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
		helper.setFrom("khoahmpc04769@fpt.edu.vn", "Julie Store");
		helper.setTo(email);
		String subject = "Request to reset your password";
		String confirmationCode = String.format("%06d", new Random().nextInt(1000000));

		String content = "<b>Password reset</b><br>"
		        + "To reset your password, visit the following address: <a href=\"" + link + "\">Reset your password</a><br>"
		        + "Your mail: <a href=\"mailto:" + email + "\" style='color:#b745dd;text-decoration:none'>" + email + "</a><br>"
		        + "Mã xác nhận là: <b>" + confirmationCode + "</b><br>";
		helper.setSubject(subject);
		helper.setText(content, true);
		sender.send(message);
	}
}
