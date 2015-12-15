package stockfighter.levels;

import java.util.List;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Stuff {

	// 22:03-
	public static void main(String[] args) {
		String venue = "EXYBEX";
		String stock = "MPI";
		StockfighterAPI api = new StockfighterAPI();
		
		int sum = getAllBought(venue, stock, api);

		System.out.println("sum = " + sum);
	}
	
	public static int getAllBought(String venue, String stock, StockfighterAPI api) {
		TMap allOrderStatus = api.getAllOrderStatus(venue, null, stock);
		List<TMap> orders = allOrderStatus.getList("orders");
		int sum = 0;
		for (TMap order : orders) {
			Boolean open = order.getBool("open");
			Integer totalFilled = order.getInt("totalFilled");
			Integer id = order.getInt("id");
			if (open) {
				System.out.println("order = " + id);
				System.out.println("order.getInt(\"totalFilled\") = " + totalFilled);
				System.out.println("open = " + open);
				System.out.println("price = " + order.getInt("price"));
				System.out.println();
			}

			if (open) {
				api.cancelOrder(venue, stock, id);
			}
			sum += totalFilled;
		}
		
		return sum;
	}
}
