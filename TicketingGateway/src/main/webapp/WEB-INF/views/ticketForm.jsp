<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ include file="header.jsp" %>
<html>
<head>
    <title>Create Ticket</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"/>
</head>
<body class="container mt-4">
    <h2 class="mb-4">Create Ticket</h2>

    <form id="ticketForm" action="/user/ticket/submitTicket" method="post" enctype="multipart/form-data" class="needs-validation">
        <div class="mb-3">
            <label for="title" class="form-label">Title:</label>
            <input type="text" class="form-control" name="title" id="title" required/>
        </div>

        <div class="mb-3">
            <label for="description" class="form-label">Description:</label>
            <textarea class="form-control" name="description" id="description" rows="4" required></textarea>
        </div>

        <div class="mb-3">
            <label for="priority" class="form-label">Priority:</label>
            <select class="form-select" name="priority" id="priority" required>
                <option value="">-- Select Priority --</option>
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH</option>
            </select>
        </div>

        <div class="mb-3">
            <label for="category" class="form-label">Category:</label>
            <input type="text" class="form-control" name="category" id="category" required/>
        </div>

        <div class="mb-3">
            <label for="attachments" class="form-label">Attachments:</label>
            <input type="file" class="form-control" name="files" id="attachments" multiple onchange="displaySelectedFiles(this)"/>
        </div>

        <ul id="selectedFilesList" class="list-group mb-3"></ul>

        <button type="submit" class="btn btn-primary">Create Ticket</button>
        <a href="/home" class="btn btn-secondary ms-2">Back to Home</a>
    </form>

    <script>
        function displaySelectedFiles(input) {
            var fileList = document.getElementById("selectedFilesList");
            fileList.innerHTML = "";

            for (var i = 0; i < input.files.length; i++) {
                var listItem = document.createElement("li");
                listItem.textContent = input.files[i].name;
                listItem.className = "list-group-item";
                fileList.appendChild(listItem);
            }
        }

        document.getElementById("ticketForm").addEventListener("submit", function(event) {
            const title = document.getElementById("title").value.trim();
            const description = document.getElementById("description").value.trim();
            const priority = document.getElementById("priority").value;
            const category = document.getElementById("category").value.trim();

            if (!title || !description || !priority || !category) {
                alert("Please fill in all required fields.");
                event.preventDefault();
            }
        });
    </script>
</body>
</html>
