package stockfighter;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Stuff {

	public static final String BASE_URL = "https://api.stockfighter.io/ob/api";

	// 13:21-13:48
	public static void main(String[] args) {
		StockfighterAPI api = new StockfighterAPI();
		TMap heartbeat = api.heartbeat();
		System.out.println("heartbeat = " + heartbeat);
		System.out.println();

		TMap venueUp = api.isVenueUp("TESTEX");
		System.out.println("venueUp = " + venueUp);
		System.out.println();
	}

}
