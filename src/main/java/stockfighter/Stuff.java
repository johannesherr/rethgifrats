package stockfighter;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Stuff {

	public static final String TESTEX = "TESTEX";
	public static final String FOOBAR = "FOOBAR";
	public static final String EXB_123456 = "EXB123456";

	// 21:40-
	public static void main(String[] args) throws InterruptedException {
		StockfighterAPI api = new StockfighterAPI();

//		TMap heartbeat = api.heartbeat();
//		System.out.println("heartbeat = " + heartbeat);
//		System.out.println();
//
//		TMap venueUp = api.isVenueUp(TESTEX);
//		System.out.println("venueUp = " + venueUp);
//		System.out.println();
//
//		TMap stocks = api.listStocks(TESTEX);
//		System.out.println("stocks = " + stocks);
//		System.out.println();
//
//		TMap getOrderBook = api.getOrderBook(TESTEX, FOOBAR);
//		System.out.println("getOrderBook = " + getOrderBook);
//		System.out.println("bids:");
//		getOrderBook.getList("bids").forEach(System.out::println);
//		System.out.println("asks:");
//		getOrderBook.getList("asks").forEach(System.out::println);
//		System.out.println();
//
//		TMap tMap = api.postOrder(EXB_123456, TESTEX, FOOBAR, 1, 20, BUY, "limit");
//		System.out.println("tMap = " + tMap);
//		System.out.println();

//		TMap quote = api.getQuote(TESTEX, FOOBAR);
//		System.out.println("quote = " + quote);
//		System.out.println();

//		for (int i = 0; i < 5; i++) {
//			TMap orderStatus = api.getOrderStatus(TESTEX, FOOBAR, tMap.getInt("id"));
//			System.out.println("orderStatus = " + orderStatus);
//			System.out.println();
//			Thread.sleep(1000);
//		}
//
//		TMap cancelOrder = api.cancelOrder(TESTEX, FOOBAR, 5);
//		System.out.println("cancelOrder = " + cancelOrder);
//		System.out.println();

//		TMap allOrderStatus = api.getAllOrderStatus(TESTEX, EXB_123456);
//		System.out.println("allOrderStatus = " + allOrderStatus);
//		System.out.println();

//		TMap allOrderStatusStock = api.getAllOrderStatus(TESTEX, EXB_123456, FOOBAR);
		TMap allOrderStatusStock = api.getAllOrderStatus(TESTEX, null, FOOBAR);
		System.out.println("allOrderStatusStock = " + allOrderStatusStock);
		System.out.println();
		allOrderStatusStock.getList("orders").forEach(System.out::println);

	}

}
