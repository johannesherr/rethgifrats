package stockfighter.util;

import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

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
		return get(venue(venue).path("stocks").path(symbol));
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
}
