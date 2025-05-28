package com.synex.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

@Service
public class PdfService {

    @Value("${upload.dir}")
    private String uploadDir;

    public String createResolutionPdf(Map<String, Object> ticket) {
        String filePath = uploadDir + "/ticket_" + ticket.get("id") + "_resolution.pdf";
        Document document = new Document();

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Ticket Resolution Summary"));
            document.add(new Paragraph("Ticket ID: " + ticket.get("id")));
            document.add(new Paragraph("Title: " + ticket.get("title")));
            document.add(new Paragraph("Description: " + ticket.get("description")));
            document.add(new Paragraph("Status: " + ticket.get("status")));
           // document.add(new Paragraph("Resolved On: " + new Date()));
            document.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create PDF: " + e.getMessage());
        }

        return filePath;
    }
}
