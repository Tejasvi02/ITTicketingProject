<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Tickets to Approve</title>
    <!-- Bootstrap CSS CDN -->
	<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" />
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        a.action-link {
            margin-right: 10px;
            color: blue;
            text-decoration: underline;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="container mt-4">
        <h2>Tickets to Approve</h2>
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
	<!-- Rejection Reason Modal -->
	<div class="modal fade" id="rejectModal" tabindex="-1" role="dialog" aria-labelledby="rejectModalLabel" aria-hidden="true">
	  <div class="modal-dialog" role="document">
	    <div class="modal-content">
	      <form id="rejectForm">
	        <div class="modal-header">
	          <h5 class="modal-title" id="rejectModalLabel">Reject Ticket</h5>
			  <button type="button" class="close" onclick="closeRejectModal()" aria-label="Close">
			    <span aria-hidden="true">&times;</span>
	          </button>
	        </div>
	        <div class="modal-body">
	          <input type="hidden" id="rejectTicketId" />
	          <div class="form-group">
	            <label for="rejectionReason">Reason for Rejection</label>
	            <textarea class="form-control" id="rejectionReason" rows="3" required></textarea>
	          </div>
	        </div>
	        <div class="modal-footer">
	          <button type="submit" class="btn btn-danger">Reject</button>
	          <button type="button" class="btn btn-secondary" onclick="closeRejectModal()">Cancel</button>
	        </div>
	      </form>
	    </div>
	  </div>
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

                        rows += "<tr>" +
                            "<td>" + t.id + "</td>" +
                            "<td>" + t.title + "</td>" +
                            "<td>" + t.status + "</td>" +
                            "<td>" + createdDate + "</td>" +
                            "<td>" +
                                "<a href='#' class='btn-sm btn-success mr-2' onclick='approveTicket(" + t.id + ")'>Approve</a>" +
                                "<a href='#' class='btn-sm btn-danger' onclick='rejectTicket(" + t.id + ")'>Reject</a>" +
                            "</td>" +
                            "</tr>";
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
		    $('#rejectTicketId').val(ticketId);
		    $('#rejectionReason').val('');
		    $('#rejectModal').modal('show');
		}

		$('#rejectForm').submit(function(e) {
		    e.preventDefault();
		    const ticketId = $('#rejectTicketId').val();
		    const reason = $('#rejectionReason').val().trim();

		    if (!reason) {
		        alert("Please provide a reason for rejection.");
		        return;
		    }

		    $.post("/manager/api/ticket/" + ticketId + "/reject", { reason: reason }, function (response) {
		        alert(response.message);
		        $('#rejectModal').modal('hide');
		        location.reload();
		    }).fail(function () {
		        alert("Failed to reject ticket.");
		    });
		});
		
		function closeRejectModal() {
		    $('#rejectModal').modal('hide');
		}

    </script>
</body>
</html>
