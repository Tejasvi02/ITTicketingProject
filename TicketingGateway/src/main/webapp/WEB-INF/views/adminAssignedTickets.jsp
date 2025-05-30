<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Assigned Tickets</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
        th { background-color: #f0f0f0; }
        .attachment-list div { margin-bottom: 4px; }
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
                <th>Attachments</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <!-- Populated by jQuery -->
        </tbody>
    </table>

    <script>
    $(document).ready(function () {
        $.get("/admin/api/assigned-tickets")
         .done(function (tickets) {
            let rows = "";
            if (Array.isArray(tickets) && tickets.length > 0) {
                tickets.forEach(function(t) {
                    // Build the first four columns exactly as before
                    rows += "<tr>" +
                        "<td>" + t.id + "</td>" +
                        "<td>" + t.title + "</td>" +
                        "<td>" + t.status + "</td>" +
                        "<td>" + t.assignedTo + "</td>";

                    // Attachments column
                    let attachCell = "No files";
                    if (Array.isArray(t.fileAttachmentPaths) && t.fileAttachmentPaths.length > 0) {
                        const listId = "att-list-" + t.id;
                        attachCell =
                            '<a href="#" class="view-attachments" data-target="' + listId + '">' +
                                'View Attachments (' + t.fileAttachmentPaths.length + ')' +
                            '</a>' +
                            '<div id="' + listId + '" class="attachment-list" style="display:none;">' +
                                t.fileAttachmentPaths
                                    .filter(function(p) { return p && p.trim().length; })
									.map(function(p) {
									    const fn = p.replace(/.*[\\/]/, "");
									    let url;
									    if (p.startsWith("/admin/api/download")) {
									        url = p;
									    } else {
									        url = "/admin/api/download?path=" + encodeURIComponent(fn);
									    }
									    return '<div><a href="' + url + '" target="_blank">' + fn + '</a></div>';
									})
                                    .join("") +
                            '</div>';
                    }
                    rows += "<td>" + attachCell + "</td>";

                    // Action column (resolve)
                    rows += "<td>" +
                        "<input type='text' id='comment_" + t.id + "' placeholder='Resolution comment' style='width: 150px;' />" +
                        "<br/>" +
                        "<a href='#' onclick='resolveTicket(" + t.id + ")'>Resolve</a>" +
                    "</td>";

                    rows += "</tr>";
                });
            } else {
                rows = "<tr><td colspan='6'>No tickets assigned to you.</td></tr>";
            }
            $("#ticketTable tbody").html(rows);
        })
        .fail(function () {
            $("#ticketTable tbody").html("<tr><td colspan='6'>Error loading tickets.</td></tr>");
        });

        // Toggle the hidden attachment lists
        $(document).on("click", "a.view-attachments", function(e) {
            e.preventDefault();
            const target = $(this).data("target");
            $("#" + target).slideToggle(150);
        });
    });

    function resolveTicket(ticketId) {
        let comment = $("#comment_" + ticketId).val().trim();
        if (!comment) {
            alert("Please enter a resolution comment.");
            return;
        }
        $.ajax({
            url: "/admin/api/ticket/" + ticketId + "/resolve",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({ comment: comment })
        })
        .done(function(response) {
            alert(response.message || "Ticket resolved.");
            location.reload();
        })
        .fail(function() {
            alert("Failed to resolve ticket.");
        });
    }
    </script>

    <p><a href="/home">← Back to Home</a></p>
</body>
</html>
