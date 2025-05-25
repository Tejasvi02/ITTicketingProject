<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Tickets to Approve</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        body {
            padding: 20px;
        }
        h2 {
            margin-bottom: 20px;
        }
        table {
            width: 100%;
        }
        .action-btn {
            margin-right: 8px;
        }
        .back-link {
            margin-top: 20px;
            display: inline-block;
        }
    </style>
</head>
<body>
    <h2>Tickets to Approve</h2>
    <div class="table-responsive">
        <table id="ticketTable" class="table table-bordered table-striped">
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
    </div>

    <script>
        $(document).ready(function () {
            $.get("/manager/api/tickets", function (tickets) {
                let rows = "";
                if (tickets && tickets.length > 0) {
                    for (let t of tickets) {
                        let createdDate = "N/A";
                        try {
                            createdDate = new Date(t.creationDate).toLocaleString();
                        } catch (e) {
                            console.warn("Invalid date for ticket:", t);
                        }

                        rows += `<tr>
                            <td>${t.id}</td>
                            <td>${t.title}</td>
                            <td>${t.status}</td>
                            <td>${createdDate}</td>
                            <td>
                                <button class='btn btn-success btn-sm action-btn' onclick='approveTicket(${t.id})'>Approve</button>
                                <button class='btn btn-danger btn-sm' onclick='rejectTicket(${t.id})'>Reject</button>
                            </td>
                        </tr>`;
                    }
                } else {
                    rows = `<tr><td colspan="5" class="text-center">No pending tickets to approve.</td></tr>`;
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='5' class='text-center text-danger'>Error loading tickets.</td></tr>");
            });
        });

        function approveTicket(ticketId) {
            $.post("/manager/api/ticket/" + ticketId + "/approve", function (response) {
                alert(response.message);
                location.reload();
            }).fail(function () {
                alert("Failed to approve ticket.");
            });
        }

        function rejectTicket(ticketId) {
            $.post("/manager/api/ticket/" + ticketId + "/reject", function (response) {
                alert(response.message);
                location.reload();
            }).fail(function () {
                alert("Failed to reject ticket.");
            });
        }
    </script>

    <a href="/home" class="btn btn-outline-primary back-link">← Back to Home</a>
</body>
</html>
