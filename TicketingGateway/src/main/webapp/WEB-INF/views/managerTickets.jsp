<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Tickets to Approve</title>
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
        a.action-link {
            margin-right: 10px;
            color: blue;
            text-decoration: underline;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <h2>Tickets to Approve</h2>
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

                        rows += "<tr>" +
                            "<td>" + t.id + "</td>" +
                            "<td>" + t.title + "</td>" +
                            "<td>" + t.status + "</td>" +
                            "<td>" + createdDate + "</td>" +
                            "<td>" +
                                "<a href='#' class='action-link' onclick='approveTicket(" + t.id + ")'>Approve</a>" +
                                "<a href='#' class='action-link' onclick='rejectTicket(" + t.id + ")'>Reject</a>" +
                            "</td>" +
                            "</tr>";
                    }
                } else {
                    rows = `<tr><td colspan="5">No pending tickets to approve.</td></tr>`;
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='5'>Error loading tickets.</td></tr>");
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
</body>
</html>
