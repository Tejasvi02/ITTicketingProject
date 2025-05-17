<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Admin - View Tickets</title>
</head>
<body>
    <h2>All Tickets</h2>
    <table border="1">
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Created By</th>
            <th>Assigned To</th>
            <th>Status</th>
            <th>Actions</th>
        </tr>
        <c:forEach var="ticket" items="${tickets}">
            <tr>
                <td>${ticket.id}</td>
                <td>${ticket.title}</td>
                <td>${ticket.createdBy}</td>
                <td>${ticket.assignedTo}</td>
                <td>${ticket.status}</td>
                <td>
                    <c:choose>
                        <c:when test="${ticket.status == 'OPEN'}">
                            <form method="post" action="/user/ticket/admin/resolve/${ticket.id}">
                                <button type="submit">Resolve</button>
                            </form>
                        </c:when>
                        <c:when test="${ticket.status == 'RESOLVED'}">
                            <form method="post" action="/user/ticket/admin/reopen/${ticket.id}">
                                <button type="submit">Reopen</button>
                            </form>
                        </c:when>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
