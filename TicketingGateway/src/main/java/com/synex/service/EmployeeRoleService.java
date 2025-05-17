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
	        employee.getRoles().add(managerRole);
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
}
