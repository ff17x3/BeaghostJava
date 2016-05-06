package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Player extends Entity implements Entity.Tickable {

	// movement
	private float speed_ps = 100;

	// others
	private float view_arc = (float) Math.PI / 2;

	// keys
	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;

	// drawing#####################
	private float lastScale;
	private final float radius, distA, distB;
	private float[] angleSins, angleCosins;
	private Polygon poly;

	private static final float[] ANGLES;

	static {
		float a = (float) Math.atan(1 / 3d);
		ANGLES = new float[6];
		ANGLES[0] = (float) Math.PI / 2 + a;
		ANGLES[1] = 3 * (float) Math.PI / 2 - a;
		ANGLES[2] = 3 * (float) Math.PI / 2 + a;
		ANGLES[3] = (float) Math.PI / 2 - a;

        ANGLES[4] = (float) Math.PI / 2;
        ANGLES[5] = 3 * (float) Math.PI / 2;
    }
    // ########################

	public Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		radius = 20; // hardcode
		angleSins = new float[6];
		angleCosins = new float[6];
		poly = new Polygon();
		distA = (float) Math.sqrt(Math.pow(radius * 1.5, 2) + Math.pow(radius / 2, 2));
		distB = radius * 1.5f;
		calcAngles();
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		lastScale = scale;
		// TODO Colors
		// view sector
		//shoulders
		g.setColor(Color.DARK_GRAY);
		g.fillOval(tfm(angleCosins[4] * distB + x - radius / 2, scale), tfm(angleSins[4] * distB + y - radius / 2, scale), tfm(radius, scale), tfm(radius, scale));
		g.fillOval(tfm(angleCosins[5] * distB + x - radius / 2, scale), tfm(angleSins[5] * distB + y - radius / 2, scale), tfm(radius, scale), tfm(radius, scale));
		poly.reset();
		poly.addPoint(tfm(angleCosins[0] * distA + x, scale), tfm(angleSins[0] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[1] * distA + x, scale), tfm(angleSins[1] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[2] * distA + x, scale), tfm(angleSins[2] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[3] * distA + x, scale), tfm(angleSins[3] * distA + y, scale));
		g.fillPolygon(poly);
		// center circle
		g.setColor(Color.GRAY);
		g.fillOval(tfm(x - radius, scale), tfm(y - radius, scale), tfm(2 * radius, scale), tfm(2 * radius, scale));

//		g.setColor(Color.RED);
//		drawCross(g, new Point(tfm(x, scale), tfm(y, scale)), 3);
	}

	private static int tfm(double v, float scale) {
		return (int) Math.round(scale * v);
	}

	@Override
	public synchronized void tick() {
		// move Player when keys pressed
		keyUpTimestamp = gm.getKeyUpTimestamp();
		keyDownTimestamp = gm.getKeyDownTimestamp();

		tickTimestamp = System.nanoTime();
		updateDir();

		for (int key = 0; key < keyUpTimestamp.length; key++) {

			if (keyUpTimestamp[key] < keyDownTimestamp[key]) {
				// key still pressed
				downtime = tickTimestamp - lastTickTimestamp;
			} else if (keyUpTimestamp[key] > lastTickTimestamp) {
				//key was released in last tick
				downtime = keyUpTimestamp[key] - lastTickTimestamp;
			} else
				continue;
			moveDir(key, downtime);
		}

		lastTickTimestamp = tickTimestamp;
	}

	private void updateDir() {
		float mouseX = gm.getMouseOnscreenX() / lastScale;
		float mouseY = gm.getMouseOnscreenY() / lastScale;
		setDir((float) Math.atan2((mouseY - y), (mouseX - x)));
	}

	/**
	 * @param dirKey 0:w, 1:a, 2:s, 3:d
	 * @param time
	 */
	private void moveDir(int dirKey, long time) {
		float dis = (float) (speed_ps * time / 1e9);
		float angle;
		switch (dirKey) {
			case 1://a,left
				angle = (float) (dir - Math.PI / 2);
				break;
			case 2://s,back
				angle = (float) (dir + Math.PI);
				break;
			case 3://d,right
				angle = (float) (dir + Math.PI / 2);
				break;
			default:
				angle = dir;
		}
		float dy = (float) (Math.sin(angle) * dis);
		float dx = (float) (Math.cos(angle) * dis);

		x = add(x, dx, gm.getMapWidth(), 2 * radius);
		y = add(y, dy, gm.getMapHeight(), 2 * radius);
		if (x + 2 * radius > gm.getMapWidth())
			x = gm.getMapWidth() - 2 * radius;

	}

	private float add(float a, float change, float max, float padding) {
		a += change;
		if (a + padding > max)
			a = max - padding;
		else if (a - padding < 0)
			a = padding;
		return a;
	}

	@Override
	public void setDir(float dir) {
		super.setDir(dir);
		calcAngles();
	}

    private void calcAngles() {
        // Werte für Boxen an den Seiten an Winkel anpassen
        for (int i = 0; i < ANGLES.length; i++) {
            float actangle = dir + ANGLES[i];
            actangle %= 2 * Math.PI;
            angleSins[i] = (float) Math.sin(actangle);
            angleCosins[i] = (float) Math.cos(actangle);
        }
    }

//	public static void drawCross(Graphics g, Point middle, int halfBoxSize) {
//		g.drawLine(middle.x - halfBoxSize, middle.y - halfBoxSize, middle.x + halfBoxSize, middle.y + halfBoxSize);
//		g.drawLine(middle.x - halfBoxSize, middle.y + halfBoxSize, middle.x + halfBoxSize, middle.y - halfBoxSize);
//	}
}
