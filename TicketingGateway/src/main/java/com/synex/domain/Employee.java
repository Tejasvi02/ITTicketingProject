package com.synex.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;

@Entity
public class Employee {

	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private String name;
	    private String email;
	    private String password;
	    private String department;
	    private String project;
	    private Long managerId;

	    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	    @JoinTable(
	        name = "employee_roles",
	        joinColumns = @JoinColumn(name = "employee_id"),
	        inverseJoinColumns = @JoinColumn(name = "role_id")
	    )
	    private List<Role> roles = new ArrayList<>();

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getDepartment() {
			return department;
		}

		public void setDepartment(String department) {
			this.department = department;
		}

		public String getProject() {
			return project;
		}

		public void setProject(String project) {
			this.project = project;
		}

		public Long getManagerId() {
			return managerId;
		}

		public void setManagerId(Long managerId) {
			this.managerId = managerId;
		}

		public List<Role> getRoles() {
			return roles;
		}

		public void setRoles(List<Role> roles) {
			this.roles = roles;
		}

		public Employee(Long id, String name, String email, String password, String department, String project,
				Long managerId, List<Role> roles) {
			super();
			this.id = id;
			this.name = name;
			this.email = email;
			this.password = password;
			this.department = department;
			this.project = project;
			this.managerId = managerId;
			this.roles = roles;
		}

		public Employee() {
			super();
			// TODO Auto-generated constructor stub
		}

}
