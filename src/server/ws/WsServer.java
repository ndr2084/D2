package server.ws;

import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.tomcat.util.buf.MessageBytes;

@ServerEndpoint("/websocketendpoint")
public class WsServer {

	private static Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());
	private static ConcurrentHashMap<String, HashSet<Session>> repo = new ConcurrentHashMap<>();
	private static HashSet<Session> toBeNotified = new HashSet<Session>();

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private final DB db = new DB();
	@OnMessage
	public void onMessage(String message, Session session) throws IOException {

		// Adding the json string into a tree bc trees are based
		JsonNode jsonMessage;
		try {
			jsonMessage = objectMapper.readTree(message);
		} catch (Exception e) {
			// System.out.println("Invalid JSON message received: " + message);
			return;
		}

		String item_id = jsonMessage.has("item_id") ? jsonMessage.get("item_id").asText() : null;
		String item_name = jsonMessage.has("item_name") ? jsonMessage.get("item_name").asText() : null;
		String session_id = jsonMessage.has("session_id") ? jsonMessage.get("session_id").asText() : null;
		String bid = jsonMessage.has("bid") ? jsonMessage.get("bid").asText() : null;
		String bid_time = jsonMessage.has("bid_time") ? jsonMessage.get("bid_time").asText() : null;
		
		ArrayList<String> blob = new ArrayList<String>(); 
		Collections.addAll(blob, item_id, item_name, session_id, bid, bid_time);
		

		synchronized (clients) {
			// Add the current session to the list of bidders for the item
			HashSet<Session> bidList = repo.getOrDefault(item_id, new HashSet<Session>());

			/*
			 * if the client has just entered the bidding room then we add them to the
			 * bidding list
			 * 
			 * This allows for us to notify other clients bidding on the same item to be
			 * aware that there's more than one bidder
			 */

			if (bid == null) {
				System.out.println(item_id);
				System.out.println(bid);

				bidList.add(session);
				repo.put(item_id, bidList);
				return;
			}

			/*
			 * If the client hasn't entered a number
			 *  then let the client know they're dumb

			 * this doesn't work so maybe I'm the idiot
			 * regex is correct though. 
			 * Source: https://regex101.com/r/13nNvB/1
			 * 
			 * if(!bid.matches("[0-9]+\.{1}[0-9]{1,2}")) { 
			 * // Handle the case where the input string is not a valid monetary format 
			 * ObjectNode notification = objectMapper.createObjectNode(); notification.put("message",
			 * "Please enter a Number\n"); String notificationMessage = notification.toString();
			 * 
			 * session.getBasicRemote().sendText(notificationMessage); return; }
			 */

			bidList.add(session);
			repo.put(item_id, bidList);

			// Update the list of sessions to be notified
			toBeNotified = repo.get(item_id);

			// Create a JSON response using Jackson's ObjectNode
			ObjectNode notification = objectMapper.createObjectNode();

			/*
			 * if new client has just joined then they haven't placed a bit yet, so send
			 * this message instead
			 */
			if (bid.isEmpty()) {
				notification.put("message", "New user has joined the server!");
			}

			/*
			 * otherwise they have placed a bid
			 */
			else {
				notification.put("bidAmount", bid);
				notification.put("bidTime", bid_time);
				
				// INITIALIZE DB
				try {
					db.initializeDatabase();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				// INSERT BID INTO DB
				try {
					db.insertJson(blob);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				notification.put("message", "New bid placed!");
			}
			String notificationMessage = notification.toString();

			// Notify all clients bidding on the same item
			for (Session client : toBeNotified) {
				if (!client.equals(session) && client.isOpen()) {
					client.getBasicRemote().sendText(notificationMessage);
				}
			}
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		// Add session to the connected sessions set
		clients.add(session);

	}

	@OnClose
	public void onClose(Session session) {
		// Remove session from the connected sessions set
		clients.remove(session);
	}

//	@OnOpen
//	public void onOpen(){
//		System.out.println("Open Connection ...");
//	}
//	
//	@OnClose
//	public void onClose(){
//		System.out.println("Close Connection ...");
//	}

//	@OnMessage
//	public String onMessage(String message){
//		System.out.println("Message from the client: " + message);
//		String echoMsg = "Echo from the server : " + message;
//		return echoMsg;
//	}

	@OnError
	public void onError(Throwable e) {
		e.printStackTrace();
	}
	
	
	
	
}