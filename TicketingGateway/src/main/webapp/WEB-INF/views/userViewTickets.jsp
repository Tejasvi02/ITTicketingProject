<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>My Tickets</title>
    <!-- Bootstrap CSS CDN -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" />
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        /* Override Bootstrap default for cursor pointer on clickable rows */
        #ticketTable tbody tr {
            cursor: pointer;
        }
        /* Small margin for action buttons */
        button, a.action-link {
            margin-right: 8px;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <c:if test="${not empty ticketCreated}">
            <script>
                alert("Ticket created successfully!");
            </script>
        </c:if>
        <c:if test="${not empty error}">
            <script>
                alert("${error}");
            </script>
        </c:if>

        <h2>My Tickets</h2>
        <table id="ticketTable" class="table table-bordered table-striped table-hover mt-4">
            <thead class="thead-dark">
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

        <p><a href="/home" class="btn btn-outline-primary">← Back to Home</a></p>
    </div>

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
                            rows += "<td><button class='btn btn-sm btn-primary' onclick='event.stopPropagation();sendForApproval(" + t.id + ")'>Send for Approval</button></td>";
                        } else if (t.status === "RESOLVED") {
							rows += "<td>" +
							    "<button class='btn btn-sm btn-warning mr-2' onclick='event.stopPropagation();reopenTicket(" + t.id + ")'>Reopen</button>" +
							    "<button class='btn btn-sm btn-danger' onclick='event.stopPropagation();closeTicket(" + t.id + ")'>Close</button>" +
							    "</td>";
                        } else {
                            rows += "<td>-</td>";
                        }

                        rows += "</tr>";
                    }
                } else {
                    rows = `<tr><td colspan="5" class="text-center">No tickets found.</td></tr>`;
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='5' class='text-center text-danger'>Error loading tickets.</td></tr>");
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
