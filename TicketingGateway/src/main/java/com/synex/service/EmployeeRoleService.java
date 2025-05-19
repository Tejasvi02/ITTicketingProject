package com.synex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.synex.domain.Employee;
import com.synex.domain.Role;
import com.synex.repository.EmployeeRepository;
import com.synex.repository.RoleRepository;

@Service
public class EmployeeRoleService {

	 @Autowired 
	 EmployeeRepository employeeRepo;
	 @Autowired 
	 RoleRepository roleRepo;

	 public Employee registerUser(Employee employee) {
		    Role userRole = roleRepo.findByRoleName("USER");
		    if (userRole == null) {
		        userRole = new Role();
		        userRole.setRoleName("USER");
		        roleRepo.save(userRole);
		    }
		    employee.getRoles().add(userRole);
		    return employeeRepo.save(employee);
		}

	    public List<Employee> getAllEmployees() {
	        return employeeRepo.findAll();
	    }

	    public void assignManagerRole(Long id) {
	        Employee employee = employeeRepo.findById(id).orElseThrow();
	        Role managerRole = roleRepo.findByRoleName("MANAGER");
	        if (managerRole == null) {
	            managerRole = new Role();
	            managerRole.setRoleName("MANAGER");
	            roleRepo.save(managerRole);
	        }
	        if (!employee.getRoles().contains(managerRole)) {
	            employee.getRoles().add(managerRole);
	        }
	        employeeRepo.save(employee);
	    }


	    public Employee findByEmail(String email) {
	        return employeeRepo.findByEmail(email);
	    }
	    
	    public Role findOrCreateRole(String roleName) {
	        Role role = roleRepo.findByRoleName(roleName);
	        if (role == null) {
	            role = new Role();
	            role.setRoleName(roleName);
	            roleRepo.save(role);
	        }
	        return role;
	    }

	    public Employee saveEmployee(Employee employee) {
	        return employeeRepo.save(employee);
	    }
	    
	    public Employee saveEmployeeWithNewManager(Long employeeId, Long managerId) {
	        Employee employee = employeeRepo.findById(employeeId).orElseThrow();
	        employee.setManagerId(managerId);
	        return employeeRepo.save(employee);
	    }
	    public void setManagerForUser(Long employeeId, Long managerId) {
	        Employee employee = employeeRepo.findById(employeeId).orElseThrow();
	        employee.setManagerId(managerId);
	        employeeRepo.save(employee);
	    }
	    
	    public Employee getEmployeeById(Long id) {
	        return employeeRepo.findById(id).orElseThrow();
	    }
	    
	    public Employee findAdminForNewManager() {
	        List<Employee> all = employeeRepo.findAll();
	        return all.stream()
	            .filter(emp -> emp.getRoles().stream().anyMatch(r -> r.getRoleName().equals("ADMIN")))
	            .findFirst()
	            .orElse(null);
	    }
	    
	    public String getManagerEmailForUser(String userEmail) {
	        Employee employee = employeeRepo.findByEmail(userEmail);
	        if (employee == null) throw new RuntimeException("Employee not found");

	        Long managerId = employee.getManagerId();
	        if (managerId == null) throw new RuntimeException("Manager ID not set");

	        Employee manager = employeeRepo.findById(managerId).orElseThrow(() -> new RuntimeException("Manager not found"));
	        return manager.getEmail();
	    }

}
