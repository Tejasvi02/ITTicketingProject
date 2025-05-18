package com.synex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.synex.domain.Employee;
import com.synex.domain.Role;
import com.synex.service.EmployeeRoleService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class AccessController {

    @Autowired
    private EmployeeRoleService employeeRoleService;


    // Show registration page
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
    	//System.out.println("Register page called");
        model.addAttribute("employee", new Employee());
        return "register"; // register.jsp
    }

    // Handle registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute Employee employee) {
    	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employeeRoleService.registerUser(employee);
        return "redirect:/login";
    }

    // Show login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // login.jsp
    }

    // After login success
    @GetMapping("/home")
    public String homePage(HttpServletRequest request) {
        if (request.getUserPrincipal() == null) {
            return "redirect:/login";
        }
        return "home"; // home.jsp
    }

    @GetMapping("/logout-success")
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the session
        request.getSession().invalidate();
        
        // Clear security context
        SecurityContextHolder.clearContext();

        // Explicitly remove cookies
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "logout"; // Return logout.jsp
    }


    // Admin page to view users
    @GetMapping("/admin/users")
    public String listUsers(Model model) {
        List<Employee> users = employeeRoleService.getAllEmployees();
        model.addAttribute("users", users);
        return "adminPage"; // adminPage.jsp
    }

    // Assign MANAGER role to a user without user assign
//    @PostMapping("/admin/assign-role")
//    public String assignManager(@RequestParam Long userId) {
//        employeeRoleService.assignManagerRole(userId);
//        return "redirect:/admin/users";
//    }
    
    //Manager with user assignment try -1
//    @PostMapping("/admin/assign-role")
//    public String assignManager(@RequestParam Long userId,
//                                @RequestParam(required = false, name = "assignedUserIds") List<Long> assignedUserIds) {
//        employeeRoleService.assignManagerRole(userId);
//
//        if (assignedUserIds != null && !assignedUserIds.isEmpty()) {
//            for (Long assignedUserId : assignedUserIds) {
//                employeeRoleService.saveEmployeeWithNewManager(assignedUserId, userId);
//            }
//        }
//        return "redirect:/admin/users";
//    }
    
    @PostMapping("/admin/assign-role")
    public String assignManager(@RequestParam Long userId,
                                @RequestParam(required = false, name = "assignedUserIds") List<Long> assignedUserIds,
                                Model model) {
        if (assignedUserIds == null || assignedUserIds.isEmpty()) {
            model.addAttribute("error", "You must assign at least one employee when making a manager.");
            return "redirect:/admin/users"; // optionally render the JSP with error
        }

        employeeRoleService.assignManagerRole(userId);

        for (Long assignedUserId : assignedUserIds) {
            employeeRoleService.setManagerForUser(assignedUserId, userId);
        }

        return "redirect:/admin/users";
    }

    
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody Employee employee) {
    	//System.out.println("Checking existing employee: " + employeeRoleService.findByEmail(employee.getEmail()));
        if (employeeRoleService.findByEmail(employee.getEmail()) != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin already exists");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));

        Role adminRole = employeeRoleService.findOrCreateRole("ADMIN");
        employee.getRoles().add(adminRole);

        employeeRoleService.saveEmployee(employee);

        return ResponseEntity.ok("Admin created successfully");
    }

    
}
