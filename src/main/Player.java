package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Player extends Entity implements Entity.Tickable {

	private static final float SPEED_PS = 100;
	private static final float RADIUS = 5;

	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;

	// drawing#####################
	private float radius, r2, distA, distB;
	private float[] angleSins, angleCosins;
	private Polygon poly;

	private static float a = (float) Math.atan(1 / 3d);
	private static final float[] ANGLES;

	static {
		ANGLES = new float[6];
		ANGLES[0] = a;
		ANGLES[1] = (float) Math.PI - a;
		ANGLES[2] = (float) Math.PI + a;
		ANGLES[3] = 2 * (float) Math.PI - a;

		ANGLES[4] = 0f;
		ANGLES[5] = (float) Math.PI;
	}
	// ########################

	public Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		radius = RADIUS;
		r2 = radius / 2;
		angleSins = new float[4];
		angleCosins = new float[4];
		poly = new Polygon();
		distA = (float) Math.sqrt(Math.pow(radius * 1.5, 2) + Math.pow(radius / 2, 2));
		distB = radius * 1.5f;
		calcAngles();
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		g.fillOval(tfm(x - RADIUS, scale), tfm(y - RADIUS, scale), tfm(2 * RADIUS, scale), tfm(2 * RADIUS, scale));

		// TODO Colors
		g.setColor(Color.DARK_GRAY);
		// shoulders 1
		g.fillOval(tfm(angleCosins[4] * distB + x - radius / 2, scale), tfm(angleSins[4] * distB + y - radius / 2, scale), Math.round(radius), Math.round(radius));
		// shoulders 2
		poly.reset();
		poly.addPoint(tfm(angleCosins[0] * distA + x, scale), tfm(angleSins[0] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[1] * distA + x, scale), tfm(angleSins[1] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[2] * distA + x, scale), tfm(angleSins[2] * distA + y, scale));
		poly.addPoint(tfm(angleCosins[3] * distA + x, scale), tfm(angleSins[3] * distA + y, scale));
		g.fillPolygon(poly);
		// center circle
		g.setColor(Color.GRAY);
		g.fillOval(tfm(x - radius, scale), tfm(y - radius, scale), tfm(2 * radius, scale), tfm(2 * radius, scale));
	}

	private static int tfm(double v, float scale) {
		return (int) Math.round(scale * v);
	}

	@Override
	public synchronized void tick() {
		//move Player when keys pressed
		keyUpTimestamp = gm.getKeyUpTimestamp();
		keyDownTimestamp = gm.getKeyDownTimestamp();

		tickTimestamp = System.nanoTime();

		for (int key = 0; key < keyUpTimestamp.length; key++) {

			if (keyUpTimestamp[key] < keyDownTimestamp[key]) {
				//key still pressed
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

	/**
	 * @param dirKey 0:w, 1:a, 2:s, 3:d
	 * @param time
	 */
	private void moveDir(int dirKey, long time) {
		float dis = (float) (SPEED_PS * time / 1e9);
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

		x += dx;
		y += dy;
	}

	@Override
	public void setDir(float dir) {
		super.setDir(dir);
		calcAngles();
	}

	@Override
	public void changeDir(float change) {
		super.changeDir(change);
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
}
