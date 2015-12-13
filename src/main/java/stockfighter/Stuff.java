package stockfighter;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Stuff {

	public static final String TESTEX = "TESTEX";
	public static final String FOOBAR = "FOOBAR";
	public static final String EXB_123456 = "EXB123456";

	// 21:40-
	public static void main(String[] args) {
		StockfighterAPI api = new StockfighterAPI();
		TMap heartbeat = api.heartbeat();
		System.out.println("heartbeat = " + heartbeat);
		System.out.println();

		TMap venueUp = api.isVenueUp(TESTEX);
		System.out.println("venueUp = " + venueUp);
		System.out.println();

		TMap stocks = api.listStocks(TESTEX);
		System.out.println("stocks = " + stocks);
		System.out.println();

		TMap orderBook = api.orderBook(TESTEX, FOOBAR);
		System.out.println("orderBook = " + orderBook);
		System.out.println("bids:");
		orderBook.getList("bids").forEach(System.out::println);
		System.out.println("asks:");
		orderBook.getList("asks").forEach(System.out::println);
		System.out.println();

		// missing api key
//		TMap tMap = api.postOrder(EXB_123456, TESTEX, FOOBAR, 200, 2, "bid", "limit");
//		System.out.println("tMap = " + tMap);
//		System.out.println();

		TMap quote = api.getQuote(TESTEX, FOOBAR);
		System.out.println("quote = " + quote);
		System.out.println();

		// missing order
//		TMap orderStatus = api.getOrderStatus(TESTEX, FOOBAR, 42);
//		System.out.println("orderStatus = " + orderStatus);
//		System.out.println();

//		TMap cancelOrder = api.cancelOrder(TESTEX, FOOBAR, 42);
//		System.out.println("cancelOrder = " + cancelOrder);
//		System.out.println();

//		TMap allOrderStatus = api.getAllOrderStatus(TESTEX, EXB_123456);
//		System.out.println("allOrderStatus = " + allOrderStatus);
//		System.out.println();

//		TMap allOrderStatusStock = api.getAllOrderStatus(TESTEX, EXB_123456, FOOBAR);
//		System.out.println("allOrderStatusStock = " + allOrderStatusStock);
//		System.out.println();

	}

}
