<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Logout</title>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.0/css/bootstrap.min.css">
    <style>
        .logout-container {
            max-width: 500px;
            margin-top: 100px;
            padding: 30px;
            border: 1px solid #ccc;
            border-radius: 15px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
    </style>
</head>
<body>
<div class="container d-flex justify-content-center">
    <div class="logout-container text-center">
        <h2 class="mb-4">You have been logged out.</h2>
        <a href="/login" class="btn btn-primary">Login Again</a>
    </div>
</div>
</body>
</html>
