<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>WELCOME TO AUCTIONS-R-US</title>
</head>
<body bgcolor = "#f0f0f0">
<h1 align = "center"> WELCOME TO AUCTIONS-R-US </h1>
	<form action="LoginServlet" method="GET">
  <p>Please select the item you want to bid for:</p>
  <input type="radio" id="s1" name="checked" value="forward:50:TV:id123">
  <label for="s1"> Item 1</label><br>
  <input type="radio" id="s2" name="checked" value="dutch:100:Lamp:id456">
  <label for="s2"> Item 2</label><br>  
  <input type="radio" id="s3" name="checked" value="forward:34:Golf Club:id789">
  <label for="s3"> Item 3</label><br><br>
  <input type="submit" value="Select Item">
	</form>
</body>
</html>