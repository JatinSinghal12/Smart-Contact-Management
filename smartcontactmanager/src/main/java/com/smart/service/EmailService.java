package com.smart.service;

import java.util.Properties;
//
//import javax.mail.Authenticator;
//import javax.mail.Message;
//import javax.mail.PasswordAuthentication;
//import javax.mail.Session;
//import javax.mail.Transport;
//import javax.mail.internet.InternetAddress;
//import javax.mail.internet.MimeMessage;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public boolean sendOtpEmail(String toEmail, int otp) {
		boolean f = false;

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toEmail);
		message.setSubject("OTP Verification");
		message.setText("Your OTP for verification is: " + otp);
		try {
			javaMailSender.send(message);
			f=true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

//	public boolean sendEmail(String subject,String message,String to) {
//		
//		//rest of the code
//		boolean f=false;
//		
//		String from="jsinghal12121999@gmail.com";
//		
//		//variable for gmail
//		
//		String host="smtp.gmail.com";
//		
//		//get the system properties
//		Properties properties=System.getProperties();
//		System.out.println("PROPERTIES "+properties);
//		
//		//setting important information to properties object
//		
//		//host set
//		properties.put("mail.smtp.host", host);
//		properties.put("mail.smtp.port", "465");
//		properties.put("mail.smtp.ssl.enable", "true");
//		properties.put("mail.smtp.auth", "true");
//		
//		//step1 to get the session object
//		Session session=Session.getInstance(properties, new Authenticator(){
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new PasswordAuthentication("jsinghal12121999@gmail.com", "");
//			}
//		});
//		session.setDebug(true);
//		
//		//step 2 compose the message [test, multi media]
//		MimeMessage m=new MimeMessage(session);
//		try {
//			//from email
//			m.setFrom(from);
//			
//			//adding recipient to message
//			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//			
//			//adding subject to message
//			m.setSubject(subject);
//			
//			//adding text to message
//			m.setText(message);
//			
//			//send
//			
//			//step 3 send the message using transport class
//			Transport.send(m);
//			System.out.println("Sent success.......");
//			f=true;
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return f;
//		
//		
//		
//	}
}
