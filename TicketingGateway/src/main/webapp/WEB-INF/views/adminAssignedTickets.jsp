<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Assigned Tickets</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f0f0f0; }
    </style>
</head>
<body>
    <h2>Tickets Assigned to You</h2>
    <table id="ticketTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Assigned To</th>
				<th>Action</th>
            </tr>
        </thead>
        <tbody>
            <!-- Populated by jQuery -->
        </tbody>
    </table>

    <script>
        $(document).ready(function () {
            $.get("/admin/api/assigned-tickets", function (tickets) {
                let rows = "";
                if (tickets && tickets.length > 0) {
                    for (let t of tickets) {
                        let createdDate = "N/A";
                        try {
                            createdDate = new Date(t.creationDate).toLocaleString();
                        } catch (e) {}

                        rows += "<tr>" +
                            "<td>" + t.id + "</td>" +
                            "<td>" + t.title + "</td>" +
                            "<td>" + t.status + "</td>" +
                            "<td>" + t.assignedTo + "</td>" +
							"<td>" +
							       "<a href='#' onclick='resolveTicket(" + t.id + ")'>Resolve</a>" +
							   "</td>" +
                            "</tr>";
                    }
                } else {
                    rows = "<tr><td colspan='5'>No tickets assigned to you.</td></tr>";
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='5'>Error loading tickets.</td></tr>");
            });
        });
		function resolveTicket(ticketId) {
		    $.post("/admin/api/ticket/" + ticketId + "/resolve", function (response) {
		        alert(response.message);
		        location.reload();
		    }).fail(function () {
		        alert("Failed to resolve ticket.");
		    });
		}
    </script>
	<p><a href="/home">← Back to Home</a></p>
</body>
</html>
