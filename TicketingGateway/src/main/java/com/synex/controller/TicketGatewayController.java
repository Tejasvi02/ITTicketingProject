package com.synex.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.synex.component.TicketClient;
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
    

    @PostMapping("/submitTicket")
    public String submitTicket(@ModelAttribute TicketForm form,
                               @RequestParam("files") MultipartFile[] files,
                               RedirectAttributes redirectAttributes) {
        try {
            // Get logged-in user's email
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            // Set metadata
            form.setCreatedBy(currentUserEmail);
            form.setAssignedTo(currentUserEmail);

            List<MultipartFile> fileList = Arrays.asList(files);
            ResponseEntity<String> response = ticketClient.createTicketWithFiles(form, fileList);

            // OPTIONAL: Deserialize and do something with response
            // ObjectMapper mapper = new ObjectMapper();
            // Ticket createdTicket = mapper.readValue(response.getBody(), Ticket.class);

            // Add flash attribute to show success alert
            redirectAttributes.addFlashAttribute("ticketCreated", true);
            return "redirect:/user/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error submitting ticket: " + e.getMessage());
            return "redirect:/user/ticket/form";
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

    
    @PostMapping("/update-ticket/{id}")
    public String updateTicket(@PathVariable Long id,
                               @ModelAttribute TicketForm form,
                               @RequestParam("files") List<MultipartFile> newFiles) throws Exception {
        ticketClient.updateTicketWithFiles(id, form, newFiles);
        return "redirect:/ticket/edit/" + id;
    }

}


