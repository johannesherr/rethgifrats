package stockfighter.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.google.common.base.Throwables;

public class StockfighterAPI {

	public static final String BUY = "buy";
	public static final String SELL = "sell";
	public static final String BASE_URL = "https://api.stockfighter.io/ob/api";
	private final Client client = ClientBuilder.newBuilder().build();
	private final String apiKey;
	private final String account;

	public StockfighterAPI() {
		try {
			Properties properties = new Properties();
			try (InputStream inputStream = Files.newInputStream(Paths.get("creds.txt"))) {
				properties.load(inputStream);
			}
			apiKey = properties.getProperty("key").trim();
			account = properties.getProperty("account").trim();
			client.register((ClientRequestFilter) requestContext ->
					requestContext.getHeaders().putSingle("X-Starfighter-Authorization", apiKey));
		} catch (IOException e) {
			throw Throwables.propagate(e);
		}
	}

	public TMap heartbeat() {
		return get(base().path("heartbeat"));
	}

	public TMap isVenueUp(String venueName) {
		return get(venue(venueName).path("heartbeat"));
	}

	public TMap listStocks(String venueName) {
		return get(venue(venueName).path("stocks"));
	}

	public TMap getOrderBook(String venue, String symbol) {
		return get(stock(venue, symbol));
	}

	public TMap postOrder(String account, String venue, String stock, int price, int qty, String direction, String orderType) {
		TMap map = new TMap();
		map.put("account", orConfigured(account));
		map.put("venue", venue);
		map.put("stock", stock);
		if (price != -1) {
			map.put("price", price);
		}
		map.put("qty", qty);
		map.put("direction", direction);
		map.put("orderType", orderType);
		Response response = stock(venue, stock).path("orders").request().post(Entity.json(JSON.stringify(map)));
		return JSON.parse(response.readEntity(String.class));
	}

	private String orConfigured(String account) {
		return account == null ? this.account : account;
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
		return get(venue(venue).path("accounts").path(orConfigured(account)).path("orders"));
	}

	public TMap getAllOrderStatus(String venue, String account, String stock) {
		return get(venue(venue).path("accounts").path(orConfigured(account)).path("stocks").path(stock).path("orders"));
	}

}
