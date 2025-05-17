<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>User Tickets</title>
</head>
<body>
    <h2>Your Submitted Tickets</h2>
    <table border="1">
        <thead>
            <tr>
                <th>Ticket ID</th>
                <th>Title</th>
                <th>Description</th>
                <th>Priority</th>
                <th>Category</th>
                <th>Status</th>
                <th>Created At</th>
                <th>Resolved At</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="ticket" items="${tickets}">
                <tr>
                    <td>${ticket.id}</td>
                    <td>${ticket.title}</td>
                    <td>${ticket.description}</td>
                    <td>${ticket.priority}</td>
                    <td>${ticket.category}</td>
                    <td>${ticket.status}</td>
                    <td>${ticket.createdAt}</td>
                    <td>${ticket.resolvedAt}</td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</body>
</html>
