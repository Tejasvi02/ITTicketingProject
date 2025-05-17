package com.synex.model;

import java.util.List;

public class TicketForm {
    private String title;
    private String description;
    private String priority;
    private String category;
    private List<String> fileAttachmentPaths;

    private String createdBy;  
    //private String assignedTo; 


    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
  
	public List<String> getFileAttachmentPaths() {
		return fileAttachmentPaths;
	}
	public void setFileAttachmentPaths(List<String> fileAttachmentPaths) {
		this.fileAttachmentPaths = fileAttachmentPaths;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
//	public String getAssignedTo() {
//		return assignedTo;
//	}
//	public void setAssignedTo(String assignedTo) {
//		this.assignedTo = assignedTo;
//	}
	
    
}