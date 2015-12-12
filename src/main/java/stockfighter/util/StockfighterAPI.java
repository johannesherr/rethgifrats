package stockfighter.util;

import java.util.Date;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import stockfighter.Stuff;

public class StockfighterAPI {

	Client client = ClientBuilder.newBuilder().build();

	public TMap heartbeat() {
		return get(base().path("heartbeat"));
	}

	public TMap isVenueUp(String venueName) {
		return get(base().path("venues").path(venueName).path("heartbeat"));
	}

	private TMap get(WebTarget target) {
		System.out.println(new Date() + ": sending GET request to " + target);
		String heartbeat = target.request().get(String.class);
		System.out.println(new Date() + ": response received");

		return JSON.parse(heartbeat);
	}

	private WebTarget base() {
		return client.target(Stuff.BASE_URL);
	}
}
