package server.ws; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class DB {
	// must place is static block so driver is loaded before any operations happen
	static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
        }
    }
    private static final String DATABASE_URL = "jdbc:sqlite:AuctionDB.db";

    // Establish a connection to the database
    private Connection connect() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    // Insert JSON data into the database
    public void insertJson(ArrayList<String> blob) throws SQLException {
    	String item_id = blob.get(0); 
    	String item_name = blob.get(1); 
    	String session_id = blob.get(2); 
    	String bid = blob.get(3); 
    	String bid_time = blob.get(4); 
    	
        String sql = "INSERT INTO UserProfile (item_id, item_name, session_id, bid, bid_time) VALUES (?,?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
        	
            pstmt.setString(1, item_id);
            pstmt.setString(2, item_name);
            pstmt.setString(3, session_id);
            pstmt.setString(4, bid);
            pstmt.setString(5, bid_time);
            pstmt.executeUpdate();
        }
    }

    // Initialize the table if it doesn't exist
    public void initializeDatabase() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS UserProfile (
                    item_id TEXT PRIMARY KEY,
                    item_name TEXT NOT NULL, 
                    session_id TEXT NOT NULL, 
                    bid TEXT NOT NULL, 
                    bid_time TEXT NOT NULL
                );
                """;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.execute();
        }
    }
}