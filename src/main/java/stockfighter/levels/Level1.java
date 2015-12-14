package stockfighter.levels;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Level1 {

	public static final String EXCHANGE = "ECPHEX";
	public static final String STOCK = "LSE";

	// 14:40-
	public static void main(String[] args) throws InterruptedException {
		StockfighterAPI api = new StockfighterAPI();

//		while (true) {
//			TMap quote = api.getQuote(EXCHANGE, STOCK);
//			System.out.println("quote = " + quote);
//			System.out.println(quote.getInt("last"));
//		System.out.println(api.getOrderBook(EXCHANGE, STOCK));
//			Thread.sleep(500);
//		}

		TMap order = api.postOrder(null, EXCHANGE, STOCK, -1, 100, StockfighterAPI.BUY, "market");
		System.out.println("order = " + order);
	}
}
