package server.ws;

public final class BidUtility {

	public static boolean check_bid(int bid_current, int bid_placed) {
		if(bid_current <= bid_placed) {
			return false; 
		}
		else {
			return true; 
		}
	}
}
