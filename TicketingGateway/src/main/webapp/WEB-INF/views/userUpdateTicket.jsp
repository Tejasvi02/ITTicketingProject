<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Update Ticket</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>Update Ticket - ID: ${ticket.id}</h2>

<form id="updateForm" enctype="multipart/form-data">
    <p>Title: ${ticket.title}</p>

    <p>Description:<br/>
        <textarea name="description">${ticket.description}</textarea>
    </p>

    <p>Priority:
        <select name="priority">
            <option value="LOW" ${ticket.priority == 'LOW' ? 'selected' : ''}>LOW</option>
            <option value="MEDIUM" ${ticket.priority == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
            <option value="HIGH" ${ticket.priority == 'HIGH' ? 'selected' : ''}>HIGH</option>
        </select>
    </p>

    <p>Category:
        <input type="text" name="category" value="${ticket.category}" />
    </p>

    <p>Existing Attachments:</p>
    <ul id="attachments">
        <c:forEach var="file" items="${ticket.fileNames}">
            <li>${file}</li>
        </c:forEach>
    </ul>

    <p>Upload more files: <input type="file" name="files" multiple /></p>

    <button type="submit">Update Ticket</button>
</form>

<hr>

<c:if test="${ticket.status == 'OPEN' || ticket.status == 'REOPENED' || ticket.status == 'REJECTED'}">
    <button id="sendApprovalBtn" onclick="sendForApproval(${ticket.id})" style="display: none;">Send for Approval</button>
</c:if>

<c:if test="${ticket.status == 'RESOLVED'}">
    <button onclick="changeStatus(${ticket.id}, 'REOPENED')">Reopen</button>
    <button onclick="changeStatus(${ticket.id}, 'CLOSED')">Close</button>
</c:if>

<hr>

<!-- View History Section -->
<h3>Ticket History</h3>
<button onclick="fetchHistory(${ticket.id})">View History</button>
<div id="historyContainer" style="margin-top: 10px; padding: 10px; border: 1px solid #ccc;"></div>

<p><a href="/user/tickets">← Back to My Tickets</a></p>
<a href="/home" class="btn btn-secondary">← Back to Home</a>

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
	        console.log("✅ Data received:", data);

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
	                '<div style="' +
	                'margin-bottom: 15px;' +
	                'padding: 15px;' +
	                'border-left: 4px solid #007bff;' +
	                'background: #e9f5ff;' +
	                'border-radius: 6px;' +
	                'box-shadow: 0 1px 3px rgba(0,0,0,0.1);' +
	                '">' +
	                '<p><strong>🛠️ Action:</strong> ' + action + '</p>' +
	                '<p><strong>📅 Date:</strong> ' + date + '</p>' +
	                '<p><strong>👤 By:</strong> ' + by + '</p>' +
	                '<p><strong>💬 Comments:</strong> ' + comments + '</p>' +
	                '</div>';
	        });

	        $('#historyContainer').html(html);
	    }).fail(function () {
	        $('#historyContainer').html("<p style='color:red;'>Failed to load history.</p>");
	    });
	}

</script>
</body>
</html>