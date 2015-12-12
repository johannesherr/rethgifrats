package stockfighter;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Stuff {

	// 13:21-13:48
	public static void main(String[] args) {
		StockfighterAPI api = new StockfighterAPI();
		TMap heartbeat = api.heartbeat();
		System.out.println("heartbeat = " + heartbeat);
		System.out.println();

		TMap venueUp = api.isVenueUp("TESTEX");
		System.out.println("venueUp = " + venueUp);
		System.out.println();

		TMap stocks = api.listStocks("TESTEX");
		System.out.println("stocks = " + stocks);
		System.out.println();

		TMap orderBook = api.orderBook("TESTEX", "FOOBAR");
		System.out.println("orderBook = " + orderBook);
		System.out.println("bids:");
		orderBook.getList("bids").forEach(System.out::println);
		System.out.println("asks:");
		orderBook.getList("asks").forEach(System.out::println);
		System.out.println();
	}

}
