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

import com.synex.component.NotificationClient;
import com.synex.component.TicketClient;
import com.synex.model.TicketForm;
import com.synex.service.EmployeeRoleService;

@Controller
@RequestMapping("/user/ticket")
public class TicketGatewayController {

    @Autowired
    private TicketClient ticketClient;
    
    @Autowired
    private NotificationClient notificationClient;

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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();

            // Set ticket creator and assigned user
            form.setCreatedBy(currentUserEmail);
            form.setAssignedTo(currentUserEmail); // Or use manager's email if needed

            List<MultipartFile> fileList = Arrays.asList(files);
            ResponseEntity<String> response = ticketClient.createTicketWithFiles(form, fileList);

            // Set success message immediately
            redirectAttributes.addFlashAttribute("ticketCreated", true);

            // Compose email details
            String subject = "Ticket Created Successfully: " + form.getTitle();
            String body = "Hi,\n\nYour ticket \"" + form.getTitle() + "\" has been created with priority: "
                          + form.getPriority() + " in category: " + form.getCategory() + ".\n\nThank you,\nSupport Team";

            // Try to send email notification separately
            try {
                System.out.println("Sending ticket email to: " + form.getAssignedTo());
                notificationClient.sendTicketCreationEmail(form.getAssignedTo(), subject, body);
            } catch (Exception ex) {
                System.err.println("Failed to send email notification: " + ex.getMessage());
                // You may log this instead of printing in production
            }

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


