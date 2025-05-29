package com.synex.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.synex.component.TicketClient;
import com.synex.service.TicketScheduler;

@RestController
public class TestController {

	@Autowired
	TicketClient ticketClient;
	
	@RequestMapping(value = "/testGetUser/{data}",method = RequestMethod.GET)
	public String testGetUser(@PathVariable String data) {
		return ticketClient.testGetClient(data); //this returns "Welcome I am Weather API"+data beacuse we are returning that in the CountryController
		
		
	}
	
	@RequestMapping(value = "/testPost",method = RequestMethod.POST)
	public JsonNode testPostUser(@RequestBody JsonNode node) {
		System.out.println(node.get("data"));
		return ticketClient.testPostClient(node);
		
	}
	
}
