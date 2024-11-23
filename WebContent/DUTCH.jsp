<%@ page import="server.ws.BidUtility"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tomcat WebSocket</title>
</head>
<body>
	AUCTION TYPE : ${auction_type} <br> 
	ITEM NAME : ${item_name} <br>
	CURRENT_PRICE: <span id="current_price">${starting_bid}</span>
	
	<!-- Timer for Dutch auction -->
	<div id="timer-container" style="display: none;">
		Time Remaining: <span id="timer"></span>
	</div>

	<form>
		<input id="message" type="hidden"> 
		<input type="hidden" name="proceed" value="True" /> 
		<input onclick="wsSendMessage();" value="Bid" type="button"> <input
			onclick="wsCloseConnection();" value="Disconnect" type="button">
	</form>
	<br>
	<textarea id="echoText" rows="5" cols="30"></textarea>
	
	<!-- Pass JSP variables to JavaScript -->
    <script>
        // Use EL to pass server-side values to JavaScript
        var auctionType = '${auction_type}';
        var startingBid = parseFloat('${starting_bid}');
    </script>
	
	<script type="text/javascript">
	   
		var item_id = '<%=session.getAttribute("item_id")%>';
	    var bidTime = '<%=new java.util.Date()%>';
		var message = document.getElementById("message");
		var session_id = '<%=session.getAttribute("session_id")%>'; 
		var item_name = '<%=session.getAttribute("item_name")%>';
		var bid_current = '<%=session.getAttribute("starting_bid")%>';
		var currentPrice = startingBid;
	</script>	
	
	<script>
			if (auctionType === "dutch"){
				startTimer(2); // Example: 300 seconds (5 minutes)
				document.getElementById("timer-container").style.display = "block";
			}

		function startTimer(duration) {
			var timerElement = document.getElementById("timer");

			var timer = duration, minutes, seconds;
			var countdown = setInterval(
					function() {
						minutes = parseInt(timer / 60, 10);
						seconds = parseInt(timer % 60, 10);

						//formatting timer so 2 digits are shown no matter what
						minutes = minutes < 10 ? "0" + minutes : minutes;
						seconds = seconds < 10 ? "0" + seconds : seconds;

						timerElement.textContent = minutes + ":" + seconds;

						// Example: Decrease price over time
						if (timer % 10 === 0 && timer > 0) {
							currentPrice -= 10; // Decrease price every 10 seconds
							document.getElementById("current_price").textContent = currentPrice.toFixed(2);
						}

						if (--timer < 0) {
							clearInterval(countdown);
							alert("Auction ended!");
						}
					}, 1000);
		}
	</script>
	
	<script>
	
		var webSocket = new WebSocket(
				"ws://localhost:8080/WebSocketServerExample/websocketendpoint");
		var echoText = document.getElementById("echoText");
		//echoText.value = "";
	<%-- Below are anonymous functions
		THESE ARE THE 4 EVENTS THAT WE LISTEN FOR
		https://developer.mozilla.org/en-US/docs/Web/API/WebSocket--%>
		webSocket.onopen = function(message) {
			wsOpen(message);
		};
		webSocket.onmessage = function(message) {
			wsGetMessage(message);
		};
		webSocket.onclose = function(message) {
			wsClose(message);
		};
		webSocket.onerror = function(message) {
			wsError(message);
		};

		function wsOpen(message) {
			var initial = {
				item_id : item_id,
				bid : "",
			};
			webSocket.send(JSON.stringify(initial));
			echoText.value += "Connected to dutch auction ... \n";
		}

		function wsSendMessage() {
			var bidValue = message.value.trim();
			if (bidValue === "") {
				// Construct a JSON object with session attributes and form input
				var data = {
					item_id : item_id,
					item_name : item_name,
					bid : currentPrice,
					bid_time : bidTime,
					//TODO: replace session_id with the username 
					session_id : session_id
				};
				// Send the JSON object as a string to the WebSocket server
					webSocket.send(JSON.stringify(data));
					echoText.value += "Bid Placed!\n" + JSON.stringify(data)+ "\n";
				// Clear the input field
				// Clearing sessionID makes the code run and idk if that's a good thing
				sessionID = "";
			}
		}
		function wsCloseConnection() {
			webSocket.close();
		}
		function wsGetMessage(message) {
			echoText.value += "Message received from the server: "
					+ message.data + "\n";

		}
		function wsClose(message) {
			echoText.value += "Disconnect ... \n";
		}

		function wserror(message) {
			echoText.value += "Error ... \n";
		}
	</script>	
</body>
</html>