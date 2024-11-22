1. Delete the previous WebSocketsExample project you may have on your workspace
2. Import this as a Maven project
3. Update the Maven Project
4. Start Tomcat (9)
5. Add the project on Tomcat
6. Start a browser 
7. Add 3-4 tabs and connect each tab to http://localhost:8080/WebSocketServerExample/LoginPage.jsp
8. For tabs 1 & 2 select Item 1
9. For tabs 3&4 select Item 2
10. In tab 1 bid 500 
11. You will see that Tab2 is only notified

Check the file WsServer.java the HashMap data structure that keeps track of <ItemIDs [BidderList]>
When a message that contains the name of the item along with the value (e.g. 500S1) where S1 is say the 
name of the item bid, then only the sessions associated with the key S1 are notified.
The concatenation of value (500) with item name (S1) is a hack here. Normally you will pass a JSON message
with itemName, BidValue, UserID, Timestamp.

Happy Bidding