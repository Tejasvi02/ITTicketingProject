<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>All Tickets</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>All Tickets</h2>
<table border="1" id="ticketTable">
    <thead>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Description</th>
            <th>Priority</th>
            <th>Status</th>
            <th>Category</th>
            <th>Creation Date</th>
            <th>Attachments</th>
        </tr>
    </thead>
    <tbody>
        <!-- Data will be loaded by AJAX -->
    </tbody>
</table>

<script>
    $(document).ready(function () {
        $.ajax({
            url: "/viewTickets",
            type: "GET",
            success: function (tickets) {
                let rows = "";
                tickets.forEach(function (ticket) {
                    let attachments = ticket.fileAttachmentPaths?.join("<br>") || "None";
                    rows += `<tr>
                        <td>${ticket.id}</td>
                        <td>${ticket.title}</td>
                        <td>${ticket.description}</td>
                        <td>${ticket.priority}</td>
                        <td>${ticket.status}</td>
                        <td>${ticket.category}</td>
                        <td>${new Date(ticket.creationDate).toLocaleString()}</td>
                        <td>${attachments}</td>
                    </tr>`;
                });
                $("#ticketTable tbody").html(rows);
            },
            error: function () {
                alert("Error fetching ticket data.");
            }
        });
    });
</script>
</body>
</html>
