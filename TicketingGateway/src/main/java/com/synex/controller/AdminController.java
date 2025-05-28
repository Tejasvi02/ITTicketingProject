package com.synex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.synex.component.NotificationClient;
import com.synex.component.TicketClient;
import com.synex.domain.Employee;
import com.synex.service.EmployeeRoleService;
import com.synex.service.PdfService;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    @Autowired
    private TicketClient ticketClient;
    
    @Autowired
    private EmployeeRoleService employeeRoleService;
    
    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private NotificationClient notificationClient;

    // Load JSP page
    @GetMapping("/admin/tickets")
    public String adminViewTicketsPage(Model model) {
        return "adminViewAllTickets"; // loads adminviewtickets.jsp
    }

    // Serve ticket data to AJAX
    @GetMapping("/admin/api/tickets")
    @ResponseBody
    public List<Map<String, Object>> getAllTickets() {
        List<Map<String, Object>> tickets = ticketClient.getAllTickets();
        //System.out.println("Tickets returned to frontend: " + tickets); // DEBUG LOG
        return tickets;
    }
    
//    @PostMapping("/admin/api/ticket/{id}/resolve")
//    @ResponseBody
//    public ResponseEntity<?> resolveTicket(@PathVariable Long id, @RequestBody Map<String, String> body) {
//        String comment = body.get("comment");
//        ticketClient.resolveTicket(id, comment);
//        return ResponseEntity.ok(Map.of("message", "Ticket resolved."));
//    }

    @PostMapping("/admin/api/ticket/{id}/resolve")
    @ResponseBody
    public ResponseEntity<?> resolveTicket(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String comment = body.get("comment");

        // Call Ticket microservice to resolve the ticket and get the updated data
        Map<String, Object> ticket = ticketClient.resolveTicket(id, comment);

        // Generate PDF and get the file path
        String filePath = pdfService.createResolutionPdf(ticket);

        // Send email with attachment using JMS (centralized via NotificationClient)
        notificationClient.sendResolvedTicketWithPdf(
            ticket.get("createdBy").toString(),
            "Your Ticket #" + ticket.get("id") + " has been Resolved",
            "Dear user,\n\nYour ticket has been resolved. Please find the resolution PDF attached.\n\nRegards,\nSupport Team",
            filePath
        );

        return ResponseEntity.ok(Map.of("message", "Ticket resolved and email sent."));
    }

    
 // Show JSP for assigned tickets
    @GetMapping("/admin/assigned-tickets")
    public String adminAssignedTicketsPage() {
        return "adminAssignedTickets"; // ✅ create adminAssignedTickets.jsp
    }

    // Serve only assigned tickets (for logged-in admin)
    @GetMapping("/admin/api/assigned-tickets")
    @ResponseBody
    public List<Map<String, Object>> getAssignedTickets(Principal principal) {
        String adminEmail = principal.getName();
        List<Map<String, Object>> tickets = ticketClient.getAssignedTickets(adminEmail, "APPROVED");
        System.out.println("Assigned tickets for " + adminEmail + ": " + tickets);
        return tickets;
    }


    // Admin page to view users
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<Employee> users = employeeRoleService.getAllEmployees();
        model.addAttribute("users", users);
        return "manageUsers"; // adminPage.jsp
    }


    @PostMapping("/admin/assign-role")
    public String assignManager(@RequestParam Long userId,
                                @RequestParam(required = false, name = "assignedUserIds") List<Long> assignedUserIds,
                                Model model) {

        if (assignedUserIds == null || assignedUserIds.isEmpty()) {
            model.addAttribute("error", "You must assign at least one employee when making a manager.");
            return "redirect:/admin/users";
        }

        Employee manager = employeeRoleService.getEmployeeById(userId);

        // Assign MANAGER role to selected user and set ADMIN as their manager
        Employee admin = employeeRoleService.findAdminForNewManager();
        if (admin != null) {
            manager.setManagerId(admin.getId());
        }
        employeeRoleService.assignManagerRole(userId);

        // Assign selected users to this manager
        for (Long assignedUserId : assignedUserIds) {
            employeeRoleService.setManagerForUser(assignedUserId, userId);
        }

        return "redirect:/admin/users";
    }
}

