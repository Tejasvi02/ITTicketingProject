<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin - User Management</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
</head>
<body>
<div class="container mt-5">
    <h2>User Management</h2>
    <table class="table table-bordered">
        <thead class="thead-dark">
            <tr>
                <th>Email</th>
                <th>Roles</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
			<c:forEach items="${users}" var="user">
			    <tr>
			        <td>${user.email}</td>
			        <td>
			            <c:forEach items="${user.roles}" var="role">
			                <span class="badge badge-info">${role.roleName}</span>
			            </c:forEach>
			        </td>
			        <td>
			            <c:set var="hasManagerOrAdmin" value="false" />
			            <c:forEach items="${user.roles}" var="role">
			                <c:if test="${role.roleName == 'MANAGER' || role.roleName == 'ADMIN'}">
			                    <c:set var="hasManagerOrAdmin" value="true" />
			                </c:if>
			            </c:forEach>
			            <c:if test="${!hasManagerOrAdmin}">
			                <form action="/admin/assign-role" method="post">
			                    <input type="hidden" name="userId" value="${user.id}" />
			                    <button type="submit" class="btn btn-sm btn-success">Make Manager</button>
			                </form>
			            </c:if>
			        </td>
			    </tr>
			</c:forEach>
        </tbody>
    </table>
    <a href="/home" class="btn btn-secondary">Back to Home</a>
</div>
</body>
</html>
