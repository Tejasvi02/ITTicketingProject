<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Update Ticket</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
<h2>Update Ticket - ID: ${ticket.id}</h2>

<form id="updateForm" action="/user/ticket/${ticket.id}/update" method="post" enctype="multipart/form-data">
    <p>Title: ${ticket.title}</p>

    <p>Description:<br/>
        <textarea name="description">${ticket.description}</textarea>
    </p>

    <p>Priority:
        <select name="priority">
            <option ${ticket.priority == 'LOW' ? 'selected' : ''}>LOW</option>
            <option ${ticket.priority == 'MEDIUM' ? 'selected' : ''}>MEDIUM</option>
            <option ${ticket.priority == 'HIGH' ? 'selected' : ''}>HIGH</option>
        </select>
    </p>

    <p>Category:
		<input type="text" name="category" value="${ticket.category}" />
    </p>

    <p>Existing Attachments:</p>
    <ul id="attachments">
		<c:forEach var="file" items="${ticket.fileNames}">
		    <li>${file}
		    </li>
		</c:forEach>
    </ul>

    <p>Upload more files: <input type="file" name="files" multiple/></p>

    <button type="submit">Update Ticket</button>
</form>

<hr>
<c:choose>
    <c:when test="${ticket.status == 'OPEN' || ticket.status == 'REOPENED'}">
        <button onclick="sendForApproval(${ticket.id})">Send for Approval</button>
    </c:when>
</c:choose>

<c:choose>
    <c:when test="${ticket.status == 'RESOLVED'}">
        <button onclick="changeStatus(${ticket.id}, 'REOPENED')">Reopen</button>
        <button onclick="changeStatus(${ticket.id}, 'CLOSED')">Close</button>
    </c:when>
</c:choose>
<p><a href="/user/tickets">← Back to My Tickets</a></p>
<a href="/home" class="btn btn-secondary">← Back to Home</a>
<script>
    $('#updateForm').on('submit', function (e) {
        e.preventDefault();
        let form = new FormData(this);

        $.ajax({
            url: "/user/api/ticket/${ticket.id}/update",
            method: "POST",
            data: form,
            contentType: false,
            processData: false,
            success: function () {
                alert("Updated successfully");
                location.reload();
            },
            error: function () {
                alert("Update failed");
            }
        });
    });

    function performAction(action) {
        $.post("/user/api/ticket/${ticket.id}/" + action, function (res) {
            alert(res.message);
            location.reload();
        }).fail(function () {
            alert("Action failed.");
        });
    }
	function sendForApproval(ticketId) {
	       $.post("/user/api/ticket/" + ticketId + "/request-approval", function (response) {
	           alert(response.message);
	           location.reload();
	       }).fail(function () {
	           alert("Failed to send for approval.");
	       });
	   }

	   function changeStatus(ticketId, newStatus) {
	       let url = "";
	       if (newStatus === 'REOPENED') {
	           url = "/user/api/ticket/" + ticketId + "/reopen";
	       } else if (newStatus === 'CLOSED') {
	           url = "/user/api/ticket/" + ticketId + "/close";
	       } else {
	           alert("Invalid status change");
	           return;
	       }

	       $.post(url, function (response) {
	           alert(response.message);
	           location.reload();
	       }).fail(function () {
	           alert("Failed to change status.");
	       });
	   }

</script>
</body>
</html>
