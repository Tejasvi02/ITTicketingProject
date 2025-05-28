package com.synex.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void sendEmail(String to, String subject, String body) {
    	System.out.println("Sending email to: " + to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom("your_email@gmail.com");
        emailSender.send(message);
    }
    
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.setFrom("your_email@gmail.com");

        File file = new File(attachmentPath);
        if (file.exists()) {
            helper.addAttachment(file.getName(), file);
        }

        emailSender.send(message);
    }
    
    public void sendResolvedTicketWithPdf(Map<String, Object> ticketData) throws MessagingException, IOException, DocumentException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        String to = (String) ticketData.get("createdBy");
        String subject = "Your Ticket #" + ticketData.get("id") + " has been Resolved";
        String body = "Dear user,\n\nYour ticket has been resolved. Please find the resolution PDF attached.\n\nRegards,\nSupport Team";

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);
        helper.setFrom("your_email@gmail.com");

        // Generate PDF in memory
        ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        try {
			PdfWriter.getInstance(document, pdfOutputStream);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        document.open();
        try {
			document.add(new Paragraph("Ticket Resolution Summary"));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        document.add(new Paragraph("Ticket ID: " + ticketData.get("id")));
        document.add(new Paragraph("Title: " + ticketData.get("title")));
        document.add(new Paragraph("Description: " + ticketData.get("description")));
        document.add(new Paragraph("Status: " + ticketData.get("status")));
        document.close();

        // Attach PDF
        ByteArrayDataSource pdfAttachment = new ByteArrayDataSource(pdfOutputStream.toByteArray(), "application/pdf");
        helper.addAttachment("ticket_" + ticketData.get("id") + "_resolution.pdf", pdfAttachment);

        emailSender.send(message);
    }

}