package com.synex.component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.model.TicketForm;
import com.synex.model.TicketModel;

@Component
public class TicketClient {
	
	private static final String testGetUrl = "http://localhost:8383/testGet/";
	private static final String testPostUrl = "http://localhost:8383/testPost";
	 private static final String createTicketUrl = "http://localhost:8383/tickets";
	 private final String TICKET_SERVICE_URL = "http://localhost:8383";

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
	public ResponseEntity<String> createTicketWithFiles(TicketForm form, List<MultipartFile> files) throws Exception {
	    List<String> paths = new ArrayList<>();

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
	//without file upload
//    public ResponseEntity<String> createTicket(TicketForm form) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        String json = mapper.writeValueAsString(form);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> request = new HttpEntity<>(json, headers);
//
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate.postForEntity(createTicketUrl, request, String.class);
//    }
//	
//	@Value("${upload.dir}")
//	private String uploadDir;
//
//	public ResponseEntity<String> createTicketWithFiles(TicketForm form, List<MultipartFile> files) throws Exception {
//	    List<String> paths = new ArrayList<>();
//
//	    System.out.println("Files received in Client: " + files.size()); // Debug
//	    for (MultipartFile file : files) {
//	        System.out.println("Processing file: " + file.getOriginalFilename());
//
//	        if (!file.isEmpty()) {
//	            String uuidFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
//	            File savedFile = new File(uploadDir, uuidFileName);
//
//	            System.out.println("Saving file to: " + savedFile.getAbsolutePath());
//	            file.transferTo(savedFile);
//	            paths.add(savedFile.getAbsolutePath());
//	        } else {
//	            System.out.println("Skipping empty file: " + file.getOriginalFilename());
//	        }
//	    }
//
//	    form.setFileAttachmentPaths(paths);
//	    System.out.println("Final stored paths before sending to microservice: " + form.getFileAttachmentPaths());
//
//	    ObjectMapper mapper = new ObjectMapper();
//	    String json = mapper.writeValueAsString(form);
//
//	    HttpHeaders headers = new HttpHeaders();
//	    headers.setContentType(MediaType.APPLICATION_JSON);
//
//	    HttpEntity<String> request = new HttpEntity<>(json, headers);
//	    return new RestTemplate().postForEntity(createTicketUrl, request, String.class);
//	}
	

//    public List<TicketModel> getAllTickets() {
//    	System.out.println("Inside getAllTickets() method");
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<TicketModel[]> response = restTemplate.getForEntity(
//                TICKET_SERVICE_URL + "/tickets", TicketModel[].class
//        );
//        System.out.println("Response from /tickets: " + Arrays.toString(response.getBody()));
//        return Arrays.asList(response.getBody());
//    }
//	public List<TicketModel> getAllTickets() {
//	    System.out.println("Inside getAllTickets() method");
//
//	    try {
//	        RestTemplate restTemplate = new RestTemplate();
//	        ResponseEntity<TicketModel[]> response = restTemplate.getForEntity(
//	                TICKET_SERVICE_URL + "/tickets", TicketModel[].class
//	        );
//
//	        TicketModel[] tickets = response.getBody();
//
//	        System.out.println("HTTP Status: " + response.getStatusCode());
//	        System.out.println("Response body: " + Arrays.toString(tickets));
//
//	        return Arrays.asList(tickets);
//
//	    } catch (Exception e) {
//	        System.out.println("Exception in getAllTickets: " + e.getMessage());
//	        e.printStackTrace();
//	        return new ArrayList<>();
//	    }
//	}
//
//    public List<TicketModel> getTicketsByUser(Long userId) {
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<TicketModel[]> response = restTemplate.getForEntity(
//                TICKET_SERVICE_URL + "/tickets/user/" + userId, TicketModel[].class
//        );
//        return Arrays.asList(response.getBody());
//    }
//
//    public void resolveTicket(Long ticketId, String adminId) {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.put(TICKET_SERVICE_URL + "/tickets/resolve/" + ticketId + "?adminId=" + adminId, null);
//    }
//
//    public void reopenTicket(Long ticketId) {
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.put(TICKET_SERVICE_URL + "/tickets/reopen/" + ticketId, null);
//    }
//	
}
