package stockfighter.levels;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
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
		String venue = "RTSPEX";
		String stock = "WSCM";
		StockfighterAPI api = new StockfighterAPI();

		Consumer<TMap> update = visualise();

		repeat(() -> {
			TMap orderBook = api.getOrderBook(venue, stock);
			System.out.println("orderBook = " + orderBook);
			update.accept(orderBook);
		});

//		while (true) {
//			TMap quote = api.getQuote(venue, stock);
//			System.out.println("quote = " + quote);
//			Thread.sleep(500);
//		}

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
