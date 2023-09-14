package com.cm.service;

import java.util.Properties;

import org.springframework.stereotype.Service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService 
{
	  public  boolean sendMail(String message, String subject, String to) {
		  //form
		  String from="Example@gmail.com";
		 //variable for gmail protocol
		String host="smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		
		System.out.println("Properties"+properties);
		
		//setting important information to properties object
		properties.put("mail.smtp.host",host);
		properties.put("mail.smtp.port","465");
		properties.put("mail.smtp.ssl.enable","true");
		properties.put("mail.smtp.auth","true");
		
		
		//step: 1 get session object
		Session session = Session.getInstance(properties,new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("Example@gmail.com","***pass****");
			}
			
		});
		
		session.setDebug(true);
		
		//setp: 2 compose the message[text,multi,media]
		MimeMessage mimeMessage = new MimeMessage(session);
		
		try {
			mimeMessage.setFrom(from);
			
			//adding recipient to message
			mimeMessage.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
			
			//adding subject to message
			mimeMessage.setSubject(subject);
			
			//adding text to message
			//mimeMessage.setText(message);
			mimeMessage.setContent(message,"text/html");
			
			//Step: 3 send the message using transport class
			Transport.send(mimeMessage);
			
			System.out.println("send Successfully");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return false;
		}
		
		
		
	}
}
