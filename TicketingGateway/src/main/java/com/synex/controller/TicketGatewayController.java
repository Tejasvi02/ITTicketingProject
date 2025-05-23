package com.synex.controller;


import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.synex.component.TicketClient;
import com.synex.domain.Employee;
import com.synex.domain.Ticket;
import com.synex.model.TicketForm;
import com.synex.service.EmployeeRoleService;

@Controller
@RequestMapping("/user/ticket")
public class TicketGatewayController {

    @Autowired
    private TicketClient ticketClient;
    

    @Autowired
    private EmployeeRoleService employeeRoleService;

    @GetMapping("/form")
    public String showTicketForm(Model model) {
        model.addAttribute("ticketForm", new TicketForm());
        return "ticketForm";
    }
    
    
    //with createdby
    @PostMapping("/submitTicket")
    public String submitTicket(@ModelAttribute TicketForm form,
                               @RequestParam("files") MultipartFile[] files,
                               Model model) {
        try {
            // Get logged-in user's email from Security Context
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName(); // assuming email is the username

            
            // Use service to fetch employee name by email
//            Employee employee = employeeRoleService.findByEmail(currentUserEmail);
//            if (employee == null) {
//                throw new RuntimeException("Employee not found for email: " + currentUserEmail);
//            }

            //Set createdBy as employee name
            //form.setCreatedBy(employee.getName());
            
            // Set createdBy field in TicketForm- email
            form.setCreatedBy(currentUserEmail);
            
            form.setAssignedTo(currentUserEmail);

            // Proceed with ticket creation
            List<MultipartFile> fileList = Arrays.asList(files);
            ResponseEntity<String> response = ticketClient.createTicketWithFiles(form, fileList);

            ObjectMapper mapper = new ObjectMapper();
            Ticket createdTicket = mapper.readValue(response.getBody(), Ticket.class);
            model.addAttribute("response", createdTicket);
            return "ticketSuccess";
        } catch (Exception e) {
            model.addAttribute("error", "Error submitting ticket: " + e.getMessage());
            return "ticketForm";
        }
    }

    
    @GetMapping("/viewTickets")
    @ResponseBody
    public List<Map<String, Object>> viewAllTickets() throws Exception {
        //System.out.println("viewAllTickets() called");
        List<Map<String, Object>> tickets = ticketClient.getAllTickets();
        //System.out.println("Tickets: " + tickets);
        return ticketClient.getAllTickets();
    }
    
    @GetMapping("/ticketsPage")
    public String showTicketsPage() {
        return "viewTickets"; // resolves to viewTickets.jsp
    }
    
    
    @PostMapping("/update-ticket/{id}")
    public String updateTicket(@PathVariable Long id,
                               @ModelAttribute TicketForm form,
                               @RequestParam("files") List<MultipartFile> newFiles) throws Exception {
        ticketClient.updateTicketWithFiles(id, form, newFiles);
        return "redirect:/ticket/edit/" + id;
    }

}


