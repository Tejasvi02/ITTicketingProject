<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>My Tickets</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        th, td {
            border: 1px solid #ccc;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #f0f0f0;
        }
    </style>
</head>
<body>
    <h2>My Tickets</h2>
    <table id="ticketTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Created Date</th>
				<th>Action</th>
            </tr>
        </thead>
        <tbody>
            <!-- Populated by jQuery AJAX -->
        </tbody>
    </table>

    <script>
        $(document).ready(function () {
            $.get("/user/api/tickets", function (tickets) {
                console.log("Tickets received:", tickets);
                let rows = "";
                if (tickets && tickets.length > 0) {
                    for (let t of tickets) {
                        console.log("Ticket:", t);
                        let createdDate = "N/A";
                        try {
                            createdDate = new Date(t.creationDate).toLocaleString();
                        } catch (e) {
                            console.warn("Invalid date for ticket:", t);
                        }

						rows += "<tr onclick='viewTicket(" + t.id + ")'>" +
						    "<td>" + t.id + "</td>" +
						    "<td>" + t.title + "</td>" +
						    "<td>" + t.status + "</td>" +
						    "<td>" + createdDate + "</td>";

							if (t.status === "OPEN" || t.status === "REOPENED") {
							    rows += "<td><button onclick='sendForApproval(" + t.id + ")'>Send for Approval</button></td>";
							} else if (t.status === "RESOLVED") {
							    rows += "<td>" +
							        "<a href='#' onclick='reopenTicket(" + t.id + ")'>Reopen</a> | " +
							        "<a href='#' onclick='closeTicket(" + t.id + ")'>Close</a>" +
							        "</td>";
							} else {
							    rows += "<td>-</td>";
							}

						rows += "</tr>";
                    }
                } else {
                    rows = `<tr><td colspan="4">No tickets found.</td></tr>`;
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='4'>Error loading tickets.</td></tr>");
            });
        });
		
		function viewTicket(ticketId) {
		    window.location.href = "/user/ticket/" + ticketId + "/edit";
		}
		function sendForApproval(ticketId) {
		    $.post("/user/api/ticket/" + ticketId + "/request-approval", function (response) {
		        alert(response.message);
		        location.reload();
		    }).fail(function () {
		        alert("Failed to send for approval.");
		    });
		}
		function reopenTicket(ticketId) {
		    $.post("/user/api/ticket/" + ticketId + "/reopen", function (response) {
		        alert(response.message);
		        location.reload();
		    }).fail(function () {
		        alert("Failed to reopen ticket.");
		    });
		}

		function closeTicket(ticketId) {
		    $.post("/user/api/ticket/" + ticketId + "/close", function (response) {
		        alert(response.message);
		        location.reload();
		    }).fail(function () {
		        alert("Failed to close ticket.");
		    });
		}
    </script>
</body>
</html>
