<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Admin - User Management</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
    <script>
        function toggleCheckboxes(userId) {
            const row = document.getElementById("assign-users-" + userId);
            row.style.display = row.style.display === "none" ? "table-row" : "none";
        }
    </script>
</head>
<body>
<div class="container mt-5">
    <h2>User Management</h2>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>
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
            <c:set var="hasManagerOrAdmin" value="false" />
            <c:forEach items="${user.roles}" var="role">
                <c:if test="${role.roleName == 'MANAGER' || role.roleName == 'ADMIN'}">
                    <c:set var="hasManagerOrAdmin" value="true" />
                </c:if>
            </c:forEach>

            <tr>
                <td>${user.email}</td>
                <td>
                    <c:forEach items="${user.roles}" var="role">
                        <span class="badge badge-info">${role.roleName}</span>
                    </c:forEach>
                </td>
                <td>
                    <c:if test="${!hasManagerOrAdmin}">
                        <button class="btn btn-sm btn-success" onclick="toggleCheckboxes(${user.id})">
                            Make Manager
                        </button>
                    </c:if>
                </td>
            </tr>

            <!-- Hidden row to show checkboxes when Make Manager is clicked -->
            <tr id="assign-users-${user.id}" style="display: none;">
                <td colspan="3">
                    <form method="post" action="/admin/assign-role">
                        <input type="hidden" name="userId" value="${user.id}" />
                        <label><strong>Select users to assign under this manager:</strong></label><br>
						<c:forEach items="${users}" var="u">
						    <c:set var="uHasManagerRole" value="false" />
						    <c:set var="uHasAdminRole" value="false" />
						    <c:forEach items="${u.roles}" var="r">
						        <c:if test="${r.roleName == 'MANAGER'}">
						            <c:set var="uHasManagerRole" value="true" />
						        </c:if>
						        <c:if test="${r.roleName == 'ADMIN'}">
						            <c:set var="uHasAdminRole" value="true" />
						        </c:if>
						    </c:forEach>
						    <c:if test="${!uHasManagerRole && !uHasAdminRole && u.id != user.id}">
						        <label class="mr-3">
						            <input type="checkbox" name="assignedUserIds" value="${u.id}" />
						            ${u.email}
						        </label>
						    </c:if>
						</c:forEach>
                        <br><button type="submit" class="btn btn-primary mt-2">Assign & Promote</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <a href="/home" class="btn btn-secondary">Back to Home</a>
</div>
</body>
</html>
