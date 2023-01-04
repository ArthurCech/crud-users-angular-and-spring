package io.github.arthurcech.dhaulagiri.services;

import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.CC_EMAIL;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.DEFAULT_MESSAGE;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.EMAIL_SUBJECT;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.FROM_EMAIL;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.OUTLOOK_SMTP_SERVER;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.PASSWORD;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.PORT_OUTLOOK;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP_AUTH;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP_HOST;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP_PORT;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP_STARTTLS_ENABLE;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.SMTP_STARTTLS_REQUIRED;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.USERNAME;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.X_PRIORITY_HEADER;
import static io.github.arthurcech.dhaulagiri.constants.EmailConstant.X_PRIORITY_HEADER_VALUE;
import static jakarta.mail.Message.RecipientType.CC;
import static jakarta.mail.Message.RecipientType.TO;

import java.util.Date;
import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	public void sendNewPasswordEmail(String firstName, String password, String email) {
		Message message;
		try {
			message = createEmail(firstName, password, email);
			// smtp for outlook and smtps for gmail
			Transport transport = getEmailSession().getTransport(SMTP);
			transport.connect(OUTLOOK_SMTP_SERVER, USERNAME, PASSWORD);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private Message createEmail(String firstName, String password, String email)
			throws MessagingException {
		Message message = new MimeMessage(getEmailSession());
		message.setFrom(new InternetAddress(FROM_EMAIL));
		message.setRecipients(TO, InternetAddress.parse(email, false));
		message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
		message.setHeader(X_PRIORITY_HEADER, X_PRIORITY_HEADER_VALUE); // only for outlook
		message.setSubject(EMAIL_SUBJECT);
		message.setText(DEFAULT_MESSAGE.formatted(firstName, password));
		message.setSentDate(new Date());
		message.saveChanges();
		return message;
	}

	private Session getEmailSession() {
		Properties properties = System.getProperties();
		properties.put(SMTP_HOST, OUTLOOK_SMTP_SERVER);
		properties.put(SMTP_AUTH, true);
		properties.put(SMTP_PORT, PORT_OUTLOOK);
		properties.put(SMTP_STARTTLS_ENABLE, true);
		properties.put(SMTP_STARTTLS_REQUIRED, true);

		// properties for outlook (comment if use another email service)
		properties.put("mail.smtp.ssl.trust", "*");
		properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

		return Session.getInstance(properties, null);
	}

}
