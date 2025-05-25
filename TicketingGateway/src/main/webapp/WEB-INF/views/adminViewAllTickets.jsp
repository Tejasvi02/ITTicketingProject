<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Admin View Tickets</title>
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
    <h2>All Tickets</h2>
    <table id="ticketTable">
        <thead>
            <tr>
                <th>ID</th>
                <th>Title</th>
                <th>Status</th>
                <th>Priority</th>
                <th>Category</th>
				<th>Created By</th>
                <th>Created On</th>
                <th>Attachment</th>
            </tr>
        </thead>
        <tbody>
            <!-- Populated by jQuery AJAX -->
        </tbody>
    </table>

    <script>
        $(document).ready(function () {
            $.get("/admin/api/tickets", function (tickets) {
                let rows = "";
                if (tickets && tickets.length > 0) {
                    for (let t of tickets) {
                        let fileName = "None";
                        if (t.fileAttachmentPaths && t.fileAttachmentPaths.length > 0) {
                            fileName = t.fileAttachmentPaths[0].split("\\\\").pop();
                        }

						rows += "<tr>" +
						    "<td>" + t.id + "</td>" +
						    "<td>" + t.title + "</td>" +
						    "<td>" + t.status + "</td>" +
						    "<td>" + t.priority + "</td>" +
						    "<td>" + t.category + "</td>" +
							"<td>" + t.createdBy + "</td>" +
						    "<td>" + new Date(t.creationDate).toLocaleString() + "</td>" +
						    "<td>" + fileName + "</td>" +
						"</tr>";
                    }
                } else {
                    rows = `<tr><td colspan="8">No tickets found.</td></tr>`;
                }
                $("#ticketTable tbody").html(rows);
            }).fail(function () {
                $("#ticketTable tbody").html("<tr><td colspan='8'>Error loading tickets.</td></tr>");
            });
        });
    </script>
	<p><a href="/home">← Back to Home</a></p>
</body>
</html>

