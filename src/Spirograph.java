import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

public class Spirograph extends JPanel {

	// drawing parameters
	private double X1, Y1, R1, S1, Q1, L1;
	private double X2, Y2, R2, S2, Q2, L2;
	private double S3, Q3;

	// positions of pen and arm joints
	private double x = Double.NaN, y;
	private double p1x, p1y;
	private double p2x, p2y;

	private BufferedImage img;
	private Graphics2D g;

	private Spirograph() {

		// disc 1: position, radius, rot. speed & offset, arm length
		X1 = 250;
		Y1 = 100;
		R1 = 50;
		S1 = .16;
		Q1 = 0;
		L1 = 200;

		// disc 2
		X2 = 550;
		Y2 = 100;
		R2 = 50;
		S2 = .166;
		Q2 = 0;
		L2 = 200;

		// rotation of canvas
		S3 = 0.001;
		Q3 = 0;

		init();
	}

	public static void main(String[] args) {
		Spirograph circles = new Spirograph();

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.add(circles);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		circles.run();
	}

	private void init() {
		Dimension dim = new Dimension(900, 900);
		setPreferredSize(dim);
		img = new BufferedImage(dim.width, dim.height, BufferedImage.TYPE_INT_RGB);
		g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.rotate(Q3, img.getWidth() / 2.0, img.getHeight() / 2.0);
	}

	private void run() {
		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {

				// arm joint positions
				p1x = X1 + R1 * Math.cos(Q1);
				p1y = Y1 + R1 * Math.sin(Q1);
				p2x = X2 + R2 * Math.cos(Q2);
				p2y = Y2 + R2 * Math.sin(Q2);

				double d = (p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y);
				double ld1 = L1 * L1 / d, ld2 = L2 * L2 / d;
				double l = (ld1 - ld2 + 1) / 2;
				double h = Math.sqrt(ld1 - l * l);

				double prevX = x, prevY = y;
				x = l * (p2x - p1x) - h * (p2y - p1y) + p1x;
				y = l * (p2y - p1y) + h * (p2x - p1x) + p1y;

				g.setColor(Color.getHSBColor((float) (Q3 / (2 * Math.PI)), 1, 1));
				// prevX is NaN only on the very first frame to avoid drawing a line from the origin
				if (!Double.isNaN(prevX)) g.draw(new Line2D.Double(prevX, prevY, x, y));

				g.rotate(S3, img.getWidth() / 2.0, img.getHeight() / 2.0);

				Q1 += S1;
				Q2 += S2;
				Q3 += S3;
				repaint();
			}
		}, 0, 8);
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// draw canvas
		g.drawImage(img, 0, 0, null);

		// draw overlay
		// 1. canvas rotation
		g.rotate(Q3, img.getWidth() / 2.0, img.getHeight() / 2.0);

		// 2. disc 1
		g.setColor(Color.GREEN);
		g.drawOval((int) (X1 - R1), (int) (Y1 - R1), (int) (2 * R1), (int) (2 * R1));
		g.drawLine((int) (p1x), (int) (p1y), (int) (x), (int) (y));

		// 3. disc 2
		g.setColor(Color.ORANGE);
		g.drawOval((int) (X2 - R2), (int) (Y2 - R2), (int) (2 * R2), (int) (2 * R2));
		g.drawLine((int) (p2x), (int) (p2y), (int) (x), (int) (y));
	}

}
