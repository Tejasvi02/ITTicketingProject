package com.synex.component;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.swing.text.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.model.EmailMessage;
import com.synex.model.TicketForm;
import com.synex.model.TicketHistoryDTO;
import com.synex.service.EmployeeRoleService;

@Component
public class TicketClient {
	
	private static final String testGetUrl = "http://localhost:8383/testGet/";
	private static final String testPostUrl = "http://localhost:8383/testPost";
	private static final String createTicketUrl = "http://localhost:8383/tickets";
	private static final String getAllTicketUrl = "http://localhost:8383/getAllTickets";
	private static final String getUserTicketUrl = "http://localhost:8383/createdby/";
    //private static final String approvalUrl = "http://localhost:8282/manager/api/manager-email";
    private static final String requestApprovalUrl = "http://localhost:8383/ticket/"; // append {id}/request-approval
    private static final String ticketsToApproveUrl = "http://localhost:8383/api/manager/tickets?managerEmail=";
    private static final String baseTicketUrl = "http://localhost:8383/ticket/"; 
 
    @Autowired
    private EmployeeRoleService employeeRoleService;
    
    @Autowired
    private NotificationClient notificationClient;

	public String testGetClient(String data) {		
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(testGetUrl+data, String.class);
		String response = responseEntity.getBody();
		return response;	
	}
	
	public JsonNode testPostClient(JsonNode node) {
		//httpHeader is essential because it consists of metadata sent with HTTP requests and responses,
        //providing information about the request, response, and the content being transmitted
		HttpHeaders headers = new HttpHeaders(); 
		headers.setContentType(MediaType.APPLICATION_JSON);//in header we set content type to identify the data format which is going to be sent back to api
		//media type from http springframework
		
		HttpEntity<String> request = new HttpEntity<String>(node.toString(),headers);
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Object> responseEntity = restTemplate.postForEntity(testPostUrl, request, Object.class);
		Object objects = responseEntity.getBody();

		//ObjectMapper converts object to particular type - in JacksonAPi - used to convert json
		ObjectMapper mapper = new ObjectMapper();
		//JsonNode is in Jackson Api
		JsonNode returnObj = mapper.convertValue(objects,JsonNode.class);
		return returnObj;
	}
	@Value("${upload.dir}")
    private String uploadDir;
	
	//Create ticket
	public ResponseEntity<String> createTicketWithFiles(TicketForm form, List<MultipartFile> files) throws Exception {
	    List<String> paths = new ArrayList<>();
	    for(String path : paths) {
	    	System.out.println(path);
	    }

	    System.out.println("Files received in Client: " + files.size()); // Debug
	    for (MultipartFile file : files) {
	        System.out.println("Processing file: " + file.getOriginalFilename());

	        if (!file.isEmpty()) {
	            String uuidFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	            File savedFile = new File(uploadDir, uuidFileName);

	            System.out.println("Saving file to: " + savedFile.getAbsolutePath());
	            file.transferTo(savedFile);
	            paths.add(savedFile.getAbsolutePath());
	        } else {
	            System.out.println("Skipping empty file: " + file.getOriginalFilename());
	        }
	    }

	    form.setFileAttachmentPaths(paths);
	    System.out.println("Final stored paths before sending to microservice: " + form.getFileAttachmentPaths());

	    ObjectMapper mapper = new ObjectMapper();
	    String json = mapper.writeValueAsString(form);

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.APPLICATION_JSON);

	    HttpEntity<String> request = new HttpEntity<>(json, headers);
	    return new RestTemplate().postForEntity(createTicketUrl, request, String.class);
	}
	
	//update ticket
    public void updateTicketWithFiles(Long id, TicketForm form, List<MultipartFile> newFiles) throws IOException {
        // Ensure upload directory exists
        File uploadFolder = new File(uploadDir);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs();
        }

        // Get existing ticket to preserve current attachments
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(baseTicketUrl + id, Map.class);
        Map<String, Object> ticketMap = response.getBody();
        List<String> currentPaths = (List<String>) ticketMap.get("fileAttachmentPaths");
        List<String> updatedPaths = new ArrayList<>();
        if (currentPaths != null) updatedPaths.addAll(currentPaths);

        // Save new files
        for (MultipartFile file : newFiles) {
            if (!file.isEmpty()) {
                String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                File saved = new File(uploadDir, uniqueName);
                file.transferTo(saved);
                updatedPaths.add(saved.getAbsolutePath());
            }
        }

        form.setFileAttachmentPaths(updatedPaths);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TicketForm> entity = new HttpEntity<>(form, headers);

        restTemplate.put(baseTicketUrl + id, entity);
    }

    public List<Map<String, Object>> getAllTickets() {
    	RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(getAllTicketUrl, List.class);
    }
    
    public List<Map<String, Object>> getTicketsByCreatedBy(String createdBy) {
    	//System.out.println("Calling microservice for createdBy: " + createdBy);
    	RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(getUserTicketUrl+createdBy, List.class);
        //System.out.println("Response from microservice: " + response.getBody());
        return response.getBody();
    }
    
    
    public void sendForApproval(Long ticketId, String username) {
        String managerEmail = employeeRoleService.getManagerEmailForUser(username);

        RestTemplate restTemplate = new RestTemplate();
        String url = requestApprovalUrl
                   + ticketId
                   + "/request-approval?managerEmail="
                   + managerEmail
                   + "&actionBy="
                   + username;

        restTemplate.postForEntity(url, null, Void.class);
    }

    public List<Map<String, Object>> getTicketsToApprove(String managerEmail) {
    	RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(ticketsToApproveUrl + managerEmail, List.class);
    }
    
    public void approveTicket(Long ticketId, String adminEmailForAssignment, String adminEmailForNotification) {
        RestTemplate restTemplate = new RestTemplate();

        String url = baseTicketUrl + ticketId + "/approve?adminEmail=" + adminEmailForAssignment;

        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> ticketData = response.getBody();
            Object id = ticketData.get("id");
            String createdBy = (String) ticketData.get("createdBy");

            String subjectToAdmin = "Ticket Approved: ID " + id;
            String bodyToAdmin = "A ticket has been approved and assigned to you.";

            String subjectToUser = "Your Ticket has been Approved";
            String bodyToUser = "Your ticket (ID " + id + ") has been approved by your manager and assigned to the admin.";

            notificationClient.sendTicketCreationEmail(adminEmailForNotification, subjectToAdmin, bodyToAdmin);
            notificationClient.sendTicketCreationEmail(createdBy, subjectToUser, bodyToUser);
        } else {
            throw new RuntimeException("Ticket approval failed");
        }
    }
    
    public List<Map<String, Object>> getAssignedTickets(String email, String status) {
        String url = baseTicketUrl + "assigned?email=" + email + "&status=" + status;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        return response.getBody();
    }
    
    
    public void rejectTicket(Long ticketId, String managerEmail, String reason) {
        RestTemplate restTemplate = new RestTemplate();

        String encodedReason = URLEncoder.encode(reason, StandardCharsets.UTF_8);
        String url = baseTicketUrl + ticketId + "/reject?managerEmail=" + managerEmail + "&reason=" + encodedReason;

        // Deserialize response into a Map instead of Ticket class
        ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> ticketData = response.getBody();
            Object id = ticketData.get("id");
            String createdBy = (String) ticketData.get("createdBy");

            // Send rejection email to ticket creator
            String subject = "Your Ticket has been Rejected";
            String body = "Your ticket (ID " + id + ") was rejected by " + managerEmail +
                          ".\n\nReason: " + reason;

            notificationClient.sendTicketCreationEmail(createdBy, subject, body);
        } else {
            throw new RuntimeException("Failed to reject ticket.");
        }
    }

    
    public Map<String, Object> resolveTicket(Long ticketId, String comment) {
        String url = baseTicketUrl + ticketId + "/resolve"; // Ticket Microservice URL
        Map<String, String> body = new HashMap<>();
        body.put("comment", comment);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        return response.getBody();
    }



    public void reopenTicket(Long ticketId) {
    String url = baseTicketUrl + ticketId + "/reopen";
    new RestTemplate().postForEntity(url, null, Void.class);
    }

    public void closeTicket(Long ticketId) {
    String url = baseTicketUrl + ticketId + "/close";
    new RestTemplate().postForEntity(url, null, Void.class);
    }	
    
    public Map<String, Object> getTicketById(Long id) {
        String url = baseTicketUrl + id;
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, Map.class);
    }
    
    public List<Map<String, Object>> getTicketHistory(Long ticketId) {
        String url = baseTicketUrl + ticketId + "/history";
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<List<Map<String,Object>>> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<Map<String,Object>>>() {}
        );
        return response.getBody();
    }



}
