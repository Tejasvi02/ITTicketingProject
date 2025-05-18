package com.synex.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private String createdBy; 
    private String assignedTo;


    private String priority; // LOW, MEDIUM, HIGH
    private String status; // OPEN, etc.
    private Date creationDate;
    private String category;
    

    @ElementCollection
    @CollectionTable(name ="ticket_attachments", joinColumns = @JoinColumn(name = "ticket_id"))
    @Column(name = "file_path")
    private List<String> fileAttachmentPaths = new ArrayList<>();

    public List<String> getFileAttachmentPaths() {
		return fileAttachmentPaths;
	}

	public void setFileAttachmentPaths(List<String> fileAttachmentPaths) {
		this.fileAttachmentPaths = fileAttachmentPaths;
	}

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    private List<TicketHistory> history = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JsonManagedReference
	public List<TicketHistory> getHistory() {
		return history;
	}

	public void setHistory(List<TicketHistory> history) {
		this.history = history;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

    
}
