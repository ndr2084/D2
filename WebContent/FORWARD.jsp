<%@ page import="server.ws.BidUtility"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Tomcat WebSocket</title>
</head>
<body>
	AUCTION TYPE : ${auction_type} ITEM NAME : ${item_name} 
	CURRENT_PRICE: <span id="current_price">${starting_bid}</span>

	<form>
		<input id="message" type="text"> <input type="hidden"
			name="proceed" value="True" /> <input onclick="wsSendMessage();"
			value="Bid" type="button"> <input
			onclick="wsCloseConnection();" value="Disconnect" type="button">
	</form>
	<br>
	<textarea id="echoText" rows="5" cols="30"></textarea>
	<script type="text/javascript">
	   
	
	    var bidTime = '<%=new java.util.Date()%>';
	    var item_id = '${item_id}';
		var session_id = '${session_id}'; 
		var item_name = '${item_name}';
		var bid_current = '${starting_bid}';
		
		var message = document.getElementById("message");
		var webSocket = new WebSocket("ws://localhost:8080/WebSocketServerExample/websocketendpoint");
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
			echoText.value += "Connected to forward auction... \n";
		}

		function wsSendMessage() {
			var bidValue = message.value.trim();
			if (bidValue !== "") {
				// Construct a JSON object with session attributes and form input
				var data = {
					item_id : item_id,
					item_name : item_name,
					bid : message.value,
					bid_time : bidTime,
					//TODO: replace session_id with the username 
					session_id : session_id
				};

				// Send the JSON object as a string to the WebSocket server
				if (message.value > bid_current) {
					webSocket.send(JSON.stringify(data));
					echoText.value += "Bid Placed!\n" + JSON.stringify(data) + "\n";
					document.getElementById("current_price").textContent = message.value;
					bid_current = message.value; 
					message.value = "";
				} else {
					echoText.value += "Bid too low!\n"; 
				}

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