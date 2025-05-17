<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head><title>Create Ticket</title></head>
<body>
<h2>Create Ticket</h2>

<form action="/user/ticket/submitTicket" method="post" enctype="multipart/form-data">
    <label>Title:</label> <input type="text" name="title"/><br/>
    <label>Description:</label> <textarea name="description"></textarea><br/>
    <label>Priority:</label>
    <select name="priority">
        <option value="LOW">LOW</option>
        <option value="MEDIUM">MEDIUM</option>
        <option value="HIGH">HIGH</option>
    </select><br/>
    <label>Category:</label> <input type="text" name="category"/><br/>

    <label>Attachments:</label> 
    <input type="file" name="files" multiple onchange="displaySelectedFiles(this)"/><br/>

    <ul id="selectedFilesList"></ul> <!-- Shows selected files -->

    <input type="submit" value="Create Ticket"/>
</form>

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
</script>
</html>
