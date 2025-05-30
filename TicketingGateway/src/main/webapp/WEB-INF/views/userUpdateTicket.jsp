This is my view/update ticket jsp, now I need to correct a small thing, send for approval button should appear after the update ticket button is clicked unless the ticket is in created state
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Update Ticket</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"/>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body class="container mt-4">
<h2 class="mb-4">Update Ticket - ID: ${ticket.id}</h2>

<form id="updateForm" enctype="multipart/form-data" class="mb-4">
    <div class="mb-3">
        <label class="form-label">Title:</label>
        <p class="form-control-plaintext">${ticket.title}</p>
    </div>

    <div class="mb-3">
        <label class="form-label">Description:</label>
        <textarea name="description" class="form-control" rows="4">${ticket.description}</textarea>
    </div>

    <div class="mb-3">
        <label class="form-label">Priority:</label>
        <select name="priority" class="form-select">
            <option value="LOW" ${ticket.priority == 'LOW' ? 'selected' : ''}>LOW</option>
            <option value="MEDIUM" ${ticket.priority == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
            <option value="HIGH" ${ticket.priority == 'HIGH' ? 'selected' : ''}>HIGH</option>
        </select>
    </div>

    <div class="mb-3">
        <label class="form-label">Category:</label>
        <input type="text" name="category" class="form-control" value="${ticket.category}" />
    </div>

    <div class="mb-3">
        <label class="form-label">Existing Attachments:</label>
        <ul class="list-group">
            <c:forEach var="file" items="${ticket.fileNames}">
                <li class="list-group-item">${file}</li>
            </c:forEach>
        </ul>
    </div>

    <div class="mb-3">
        <label class="form-label">Upload more files:</label>
        <input type="file" name="files" multiple class="form-control" />
    </div>

    <button type="submit" class="btn btn-primary">Update Ticket</button>
    <a href="/user/tickets" class="btn btn-secondary ms-2">← Back to My Tickets</a>
    <a href="/home" class="btn btn-outline-secondary ms-2">← Back to Home</a>
</form>

<c:if test="${ticket.status == 'OPEN' || ticket.status == 'REOPENED' || ticket.status == 'REJECTED'}">
    <button id="sendApprovalBtn" onclick="sendForApproval(${ticket.id})" class="btn btn-success mb-3" style="display: none;">Send for Approval</button>
</c:if>

<c:if test="${ticket.status == 'RESOLVED'}">
    <div class="mb-3">
        <button onclick="changeStatus(${ticket.id}, 'REOPENED')" class="btn btn-warning">Reopen</button>
        <button onclick="changeStatus(${ticket.id}, 'CLOSED')" class="btn btn-danger ms-2">Close</button>
    </div>
</c:if>

<hr/>

<h3>Ticket History</h3>
<button onclick="fetchHistory(${ticket.id})" class="btn btn-info mb-3">View History</button>
<div id="historyContainer" class="mb-4 p-3 border rounded bg-light"></div>

<script>
    const originalData = {
        description: $('textarea[name="description"]').val(),
        priority: $('select[name="priority"]').val(),
        category: $('input[name="category"]').val()
    };

    function checkForChanges() {
        const currentData = {
            description: $('textarea[name="description"]').val(),
            priority: $('select[name="priority"]').val(),
            category: $('input[name="category"]').val()
        };

        const changed =
            currentData.description !== originalData.description ||
            currentData.priority !== originalData.priority ||
            currentData.category !== originalData.category;

        if ('${ticket.status}' === 'REJECTED' && changed) {
            $('#sendApprovalBtn').show();
        } else if ('${ticket.status}' === 'OPEN' || '${ticket.status}' === 'REOPENED') {
            $('#sendApprovalBtn').show();
        } else {
            $('#sendApprovalBtn').hide();
        }
    }

    $('textarea[name="description"], select[name="priority"], input[name="category"]').on('input change', checkForChanges);
    checkForChanges();

    $('#updateForm').on('submit', function (e) {
        e.preventDefault();
        let formData = new FormData(this);
        formData.append("id", ${ticket.id});

        $.ajax({
            url: "/user/api/ticket/${ticket.id}/update",
            method: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function () {
                alert("Ticket updated successfully.");
                location.reload();
            },
            error: function () {
                alert("Ticket update failed.");
            }
        });
    });

    function sendForApproval(ticketId) {
        $.post("/user/api/ticket/" + ticketId + "/request-approval", function (response) {
            alert(response.message);
            location.reload();
        }).fail(function () {
            alert("Failed to send for approval.");
        });
    }

    function changeStatus(ticketId, newStatus) {
        let url = newStatus === 'REOPENED'
            ? "/user/api/ticket/" + ticketId + "/reopen"
            : "/user/api/ticket/" + ticketId + "/close";

        $.post(url, function (response) {
            alert(response.message);
            location.reload();
        }).fail(function () {
            alert("Failed to change ticket status.");
        });
    }

    function fetchHistory(ticketId) {
        $('#historyContainer').html("Loading...");

        $.get("/user/api/ticket/" + ticketId + "/history", function (data) {
            if (!Array.isArray(data) || data.length === 0) {
                $('#historyContainer').html("<p>No history available.</p>");
                return;
            }

            let html = "";

            data.forEach(entry => {
                const action = entry.action || "-";
                const date = entry.actionDate ? new Date(entry.actionDate).toLocaleString() : "-";
                const by = entry.actionBy || "-";
                const comments = entry.comments || "-";

                html += '' +
                    '<div class="mb-3 p-3 border-start border-primary bg-white rounded shadow-sm">' +
                    '<p><strong>🛠️ Action:</strong> ' + action + '</p>' +
                    '<p><strong>📅 Date:</strong> ' + date + '</p>' +
                    '<p><strong>👤 By:</strong> ' + by + '</p>' +
                    '<p><strong>💬 Comments:</strong> ' + comments + '</p>' +
                    '</div>';
            });

            $('#historyContainer').html(html);
        }).fail(function () {
            $('#historyContainer').html("<p class='text-danger'>Failed to load history.</p>");
        });
    }
</script>
</body>
</html>
