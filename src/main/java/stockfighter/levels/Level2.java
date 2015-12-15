package stockfighter.levels;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToIntFunction;

import javax.swing.*;

import com.google.common.collect.Lists;

import stockfighter.util.StockfighterAPI;
import stockfighter.util.TMap;

public class Level2 {

	// 22:55-
	// vis 23:13-
	public static void main(String[] args) throws InterruptedException {
		String venue = "EXYBEX";
		String stock = "MPI";
		StockfighterAPI api = new StockfighterAPI();

//		api.cancelOrder(venue, stock, 104);
		Consumer<TMap> update = visualise();

		List<Integer> orders = new LinkedList<>();
		AtomicInteger remaining = new AtomicInteger(100_000 - 59554);
//		int price = 4400;
		int price = -1;

		AtomicInteger sentinel = new AtomicInteger();

		repeat(() -> {
			int bought = Stuff.getAllBought(venue, stock, api);
			int remaining2 = 100_000 - bought;
			
			TMap orderBook = api.getOrderBook(venue, stock);
			System.out.println("orderBook = " + orderBook);
			update.accept(orderBook);

			TMap quote = api.getQuote(venue, stock);
			System.out.println("quote = " + quote.getInt("last"));
			
			TMap allOrderStatus = api.getAllOrderStatus(venue, null, stock);
			List<TMap> orders1 = allOrderStatus.getList("orders");
			for (TMap tMap : orders1) {
				if (tMap.getBool("open") && tMap.getInt("id") != sentinel.get()) {
					api.cancelOrder(venue, stock, tMap.getInt("id"));
				}
			}

			List<TMap> asks = orderBook.getList("asks");
			for (TMap ask : asks) {
				Integer price1 = ask.getInt("price");
				if (price1 < 4557) {
					Integer qty = ask.getInt("qty");
					api.postOrder(null, venue, stock, price1, Math.min(qty, remaining2), StockfighterAPI.BUY, "limit");
				}
			}

			List<TMap> bids = orderBook.getList("bids");
			int max = 0;
			Integer myprice = 0;
			if (sentinel.get() > 0) {
				myprice = api.getOrderStatus(venue, stock, sentinel.get()).getInt("price");
			}
			for (TMap bid : bids) {
				Integer price1 = bid.getInt("price");
				if (price != myprice)
					max = Math.max(max, price1);
			}
			if (max < myprice) {
				if (sentinel.get() > 0) {
					api.cancelOrder(venue, stock, sentinel.get());
				}
			}
			if (max < 4557) {
				if (sentinel.get() > 0) {
					api.cancelOrder(venue, stock, sentinel.get());
				}

				TMap order = api.postOrder(null, venue, stock, max + 1, Math.min(10_000, remaining2), StockfighterAPI.BUY, "limit");
				Integer id = order.getInt("id");
				sentinel.set(id);
			}

//			boolean allClosed = true;
//			for (Integer order : orders) {
//				TMap orderStatus = api.getOrderStatus(venue, stock, order);
//				boolean open = orderStatus.getBool("open");
//				if (!open) {
//					remaining.addAndGet(-1 * orderStatus.getInt("totalFilled"));
//				} else {
//					allClosed = false;
//				}
//			}
//
//			if (price > -1 && allClosed) {
//				TMap order = api.postOrder(null, venue, stock, price, 10_000, StockfighterAPI.BUY, "limit");
//				System.out.println("order = " + order);
//				orders.add(order.getInt("id"));
//			}
//
//			if (remaining.get() == 0) {
//				System.out.println("ALL DONE!!");
//			}
		});

	}

	private static Consumer<TMap> visualise() {
		AtomicReference<TMap> ref = new AtomicReference<>();

		int width = 800, height = 600, margin = 40;

		JFrame jFrame = new JFrame();
		jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		jFrame.setContentPane(new JPanel() {

			{
				setOpaque(true);
			}

			@Override
			public Dimension getPreferredSize() {
				return new Dimension(width, height);
			}

			@Override
			protected void paintComponent(Graphics g) {
				Color color = Color.lightGray;
				g.setColor(Color.gray);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.setColor(color);

				TMap tMap = ref.get();
				if (tMap == null) {
					return;
				}

				List<TMap> bids = tMap.getList("bids");
				List<TMap> asks = tMap.getList("asks");
				ArrayList<TMap> all = Lists.newArrayList();
				if (bids != null) {
					all.addAll(bids);
				}
				if (asks != null) {
					all.addAll(asks);
				}

				Function<Integer, Double> scaleX = createScale(all, m -> m.getInt("price"));
				Function<Integer, Double> scaleY = createScale(all, m -> m.getInt("qty"));

				g.setColor(color);
				g.drawLine(margin, height / 2, width - margin, height / 2);

				for (TMap elem : all) {
					Integer price = elem.getInt("price");
					int x = (int) (scaleX.apply(price) * (width - 2 * margin));
					int y = (int) (scaleY.apply(elem.getInt("qty")) * height / 8 + 20);
					int x1 = x + margin;
					boolean isBuy = elem.getBool("isBuy");
					int y1 = height / 2 + (isBuy ? 15 : -10);
					g.drawString(price + "", x1, y1);
					Rectangle2D labelBounds = g.getFontMetrics().getStringBounds(price + "", g);
					int labelW = (int) labelBounds.getWidth();
					int labelH = (int) labelBounds.getHeight();
					int barW = 5;
					int padding = 5;
					int barX = labelW / 2 + x1 - barW / 2;
					if (isBuy) {
						g.setColor(Color.red);
						g.fillRect(barX, y1 + padding, barW, y);
					} else {
						g.setColor(Color.green);
						g.fillRect(barX, y1 - labelH - padding - y, barW, y);
					}
					g.setColor(color);
				}
			}
		});
		jFrame.pack();
		jFrame.setVisible(true);

		return data -> {
			SwingUtilities.invokeLater(() -> {
				ref.set(data);
				jFrame.repaint();
			});
		};
	}

	private static <T> Function<Integer, Double> createScale(List<T> data, ToIntFunction<T> getter) {
		if (data == null || data.isEmpty()) return num -> 1.0;

		int min = data.stream().mapToInt(getter).min().getAsInt();
		int max = data.stream().mapToInt(getter).max().getAsInt();
		if (max == min) {
			return v -> 1.0;
		} else {
			return num -> (num - min) / (max - (double) min);
		}
	}

	private static void repeat(Runnable run) throws InterruptedException {
		while (true) {
			run.run();
			System.out.println();
			Thread.sleep(1000);
		}
	}
}
