<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="./css/style.css">
    <script src="./scripts/inputValidation.js"></script>
    <title>Start Game Form</title>
</head>
<body>
<h1>Welcome to Millionaire Game!</h1>
<form action="player" method="post" id="playerRegistrationForm">
    <label for="playerName">Enter your name:</label><br>
    <input type="text" id="playerName" name="playerName"><br><br>
    <input type="submit" value="Start Game">
</form>
</body>
</html>
