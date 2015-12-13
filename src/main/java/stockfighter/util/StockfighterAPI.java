package stockfighter.util;

import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

public class StockfighterAPI {

	public static final String BASE_URL = "https://api.stockfighter.io/ob/api";
	private final Client client = ClientBuilder.newBuilder().build();

	public TMap heartbeat() {
		return get(base().path("heartbeat"));
	}

	public TMap isVenueUp(String venueName) {
		return get(venue(venueName).path("heartbeat"));
	}

	public TMap listStocks(String venueName) {
		return get(venue(venueName).path("stocks"));
	}

	public TMap orderBook(String venue, String symbol) {
		return get(stock(venue, symbol));
	}

	// 21:44-
	public static void main(String[] args) {
		String[] split = "account venue stock price qty direction orderType".split(" ");
		for (String s : split) {
			System.out.printf("map.put(\"%1$s\", %1$s);%n", s);
		}
		System.out.println();
	}

	public TMap postOrder(String account, String venue, String stock, int price, int qty, String direction, String orderType) {
		TMap map = new TMap();
		map.put("account", account);
		map.put("venue", venue);
		map.put("stock", stock);
		map.put("price", price);
		map.put("qty", qty);
		map.put("direction", direction);
		map.put("orderType", orderType);
		Response response = stock(venue, stock).path("orders").request().post(Entity.json(JSON.stringify(map)));
		return JSON.parse(response.readEntity(String.class));
	}

	private WebTarget stock(String venue, String symbol) {
		return venue(venue).path("stocks").path(symbol);
	}

	private TMap get(WebTarget target) {
		System.out.println(new Date() + ": sending GET request to " + target);
		String heartbeat = target.request().get(String.class);
		System.out.println(new Date() + ": response received");

		return JSON.parse(heartbeat);
	}

	private WebTarget base() {
		return client.target(BASE_URL);
	}

	private WebTarget venue(String venueName) {
		return base().path("venues").path(venueName);
	}

	public TMap getQuote(String venue, String stock) {
		return get(stock(venue, stock).path("quote"));
	}

	public TMap getOrderStatus(String venue, String stock, int orderId) {
		return get(stock(venue, stock).path("orders").path(String.valueOf(orderId)));
	}

	public TMap cancelOrder(String venue, String stock, int orderId) {
		String json = stock(venue, stock).path("orders").path(String.valueOf(orderId)).request().delete(String.class);
		return JSON.parse(json);
	}

	public TMap getAllOrderStatus(String venue, String account) {
		return get(venue(venue).path("accounts").path(account).path("orders"));
	}

	public TMap getAllOrderStatus(String venue, String account, String stock) {
		return get(venue(venue).path("accounts").path(account).path("stocks").path(stock).path("orders"));
	}

}
