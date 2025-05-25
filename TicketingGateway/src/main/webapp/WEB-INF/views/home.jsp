<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%@ include file="header.jsp" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Home</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <h2>Welcome to Home Page</h2>
    <p>You are successfully logged in.</p>

	<security:authorize access="hasRole('ADMIN')">
	    <p>Hello Admin</p>
	    <a href="/admin/users" class="btn btn-warning">Manage users</a>
	    <a href="/admin/tickets" class="btn btn-primary">View Tickets</a>
		<a href="/admin/assigned-tickets" class="btn btn-success">View Assigned Tickets</a>
	</security:authorize>

	<security:authorize access="hasRole('MANAGER')">
	    <p>Hello Manager</p>
	    <a href="/manager/tickets"><button>Approve Tickets</button></a>
	</security:authorize>

	<security:authorize access="hasRole('USER')">
	    <p>Hello User</p>
	    <a href="/user/ticket/form" class="btn btn-primary">Create a Ticket</a>
	    <a href="/user/tickets" class="btn btn-info">View My Tickets</a>
	</security:authorize>
	
    <a href="/logout-success" class="btn btn-danger">Logout</a>
</div>
</body>
</html>
