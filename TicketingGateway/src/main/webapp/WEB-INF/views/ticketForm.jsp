<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Create Ticket</title>
</head>
<body>
<h2>Create Ticket</h2>

<form id="ticketForm" action="/user/ticket/submitTicket" method="post" enctype="multipart/form-data">
    <label>Title:</label> 
    <input type="text" name="title" id="title"/><br/>

    <label>Description:</label> 
    <textarea name="description" id="description"></textarea><br/>

    <label>Priority:</label>
    <select name="priority" id="priority">
        <option value="">-- Select Priority --</option>
        <option value="LOW">LOW</option>
        <option value="MEDIUM">MEDIUM</option>
        <option value="HIGH">HIGH</option>
    </select><br/>

    <label>Category:</label> 
    <input type="text" name="category" id="category"/><br/>

    <label>Attachments:</label> 
    <input type="file" name="files" multiple onchange="displaySelectedFiles(this)"/><br/>

    <ul id="selectedFilesList"></ul> <!-- Shows selected files -->

    <input type="submit" value="Create Ticket"/>
</form>

<p><a href="/home">Back to Home</a></p>

<script>
function displaySelectedFiles(input) {
    var fileList = document.getElementById("selectedFilesList");
    fileList.innerHTML = ""; // Clear previous list

    for (var i = 0; i < input.files.length; i++) {
        var listItem = document.createElement("li");
        listItem.textContent = input.files[i].name;
        fileList.appendChild(listItem);
    }
}

document.getElementById("ticketForm").addEventListener("submit", function(event) {
    // Fetch values
    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const priority = document.getElementById("priority").value;
    const category = document.getElementById("category").value.trim();

    // Validate
    if (!title || !description || !priority || !category) {
        alert("Please fill in all required fields (Title, Description, Priority, Category).");
        event.preventDefault(); // Prevent form submission
    }
});
</script>
</body>
</html>
