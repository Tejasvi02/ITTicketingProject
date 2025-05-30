package com.synex.model;

import java.io.Serializable;

public class EmailMessage implements Serializable {
	private static final long serialVersionUID = 1L;
    private String to;
    private String subject;
    private String body;
    private String attachmentPath;

    public EmailMessage() {}

    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    // Getters and Setters
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

	public String getAttachmentPath() {
		return attachmentPath;
	}

	public void setAttachmentPath(String attachmentPath) {
		this.attachmentPath = attachmentPath;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
    
    
}