package com.synex.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
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
        Map<String, Object> ticket = ticketClient.resolveTicket(id, comment);

        // Send full ticket data for PDF generation & email
        notificationClient.sendResolvedTicketNotification(ticket);

        return ResponseEntity.ok(Map.of("message", "Ticket resolved and email sent."));
    }

    
 // Show JSP for assigned tickets
    @GetMapping("/admin/assigned-tickets")
    public String adminAssignedTicketsPage() {
        return "adminAssignedTickets"; // ✅ create adminAssignedTickets.jsp
    }

    // Serve only assigned tickets (for logged-in admin)
//    @GetMapping("/admin/api/assigned-tickets")
//    @ResponseBody
//    public List<Map<String, Object>> getAssignedTickets(Principal principal) {
//        String adminEmail = principal.getName();
//        List<Map<String, Object>> tickets = ticketClient.getAssignedTickets(adminEmail, "APPROVED");
//        System.out.println("Assigned tickets for " + adminEmail + ": " + tickets);
//        return tickets;
//    }
    @GetMapping("/admin/api/assigned-tickets")
    @ResponseBody
    public List<Map<String, Object>> getAssignedTickets(Principal principal) {
        String adminEmail = "tejasvijava555@gmail.com"; // or hardcoded for testing
        List<Map<String, Object>> rawTickets = ticketClient.getAssignedTickets(adminEmail, "APPROVED");

        List<Map<String, Object>> convertedTickets = new ArrayList<>();

        for (Map<String, Object> ticket : rawTickets) {
            Map<String, Object> newTicket = new HashMap<>(ticket);

            List<String> rawPaths = (List<String>) ticket.get("fileAttachmentPaths");
            List<String> downloadUrls = new ArrayList<>();

            if (rawPaths != null) {
                for (String fullPath : rawPaths) {
                    if (fullPath != null && !fullPath.isEmpty()) {
                        // Just extract the filename
                        String filename = new File(fullPath).getName();
                        System.out.println(filename);
                        // Encode URL safely
                        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8);
                        // Build download URL
                        String downloadUrl = "/admin/api/download?path=" + encoded;
                        downloadUrls.add(downloadUrl);
                    }
                }
            }

            newTicket.put("fileAttachmentPaths", downloadUrls); // replace raw paths with URLs
            convertedTickets.add(newTicket);
        }

        return convertedTickets;
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
    
//    @GetMapping("/admin/api/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String encodedPath, Principal principal) {
//        try {
//            String decodedPath = URLDecoder.decode(encodedPath, StandardCharsets.UTF_8);
//
//            // Base upload directory (absolute path, no trailing slash)
//            Path uploadDir = Paths.get("C:/Synergistic/Spring_Ankit/uploads").toAbsolutePath().normalize();
//
//            // Normalize user-requested path and resolve it safely against base
//            Path filePath = uploadDir.resolve(decodedPath).normalize();
//
//            // Prevent path traversal by ensuring file is within base directory
//            if (!filePath.startsWith(uploadDir)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//
//            File file = filePath.toFile();
//            if (!file.exists() || !file.isFile()) {
//                return ResponseEntity.notFound().build();
//            }
//
//            Resource resource = new UrlResource(file.toURI());
//            String contentDisposition = "attachment; filename=\"" + file.getName() + "\"";
//
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                    .body(resource);
//        } catch (Exception e) {
//            // Optional: log the error for debugging
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }

//    @GetMapping("/admin/api/download")
//    public ResponseEntity<Resource> downloadFile(@RequestParam String path) throws IOException {
//        String uploadDir = "C:/Synergistic/Spring_Ankit/uploads/"; // base directory
//        Path filePath = Paths.get(uploadDir).resolve(path).normalize();
//        System.out.println(filePath);
//        
//        if (!Files.exists(filePath)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Resource resource = new UrlResource(filePath.toUri());
//
//        return ResponseEntity.ok()
//            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//            .body(resource);
//    }
    @GetMapping("/admin/api/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam String path) {
        try {
            // 1) Base upload directory
            Path uploadRoot = Paths.get("C:/Synergistic/Spring_Ankit/uploads")
                                  .toAbsolutePath().normalize();

            // 2) Resolve filename against it
            Path filePath = uploadRoot.resolve(path).normalize();

            // ==== DEBUG LOGS ====
            System.out.println(">>> Requested download for: " + path);
            System.out.println(">>> Resolved uploadRoot: "  + uploadRoot);
            System.out.println(">>> Resolved filePath: "    + filePath);
            System.out.println(">>> Exists? " + Files.exists(filePath));
            System.out.println(">>> IsRegularFile? " + Files.isRegularFile(filePath));
            System.out.println(">>> IsReadable? " + Files.isReadable(filePath));
            // ====================

            if (!filePath.startsWith(uploadRoot) 
                || !Files.exists(filePath) 
                || !Files.isRegularFile(filePath)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}

