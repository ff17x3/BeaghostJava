package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Player extends Entity implements Entity.Tickable {

	private static final float SPEED_PS = 20;
	private static final float RADIUS = 5;

	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;

	// drawing
	float radius, distA = (float) Math.sqrt(Math.pow(radius * 1.5, 2) + Math.pow(radius / 2, 2));
	float[] angleSins, angleCosins;

	static float a = (float) Math.atan(1 / 3d);
	static final float[] ANGLES;

	static {
		ANGLES = new float[4];
		ANGLES[0] = a;
		ANGLES[1] = (float) Math.PI - a;
		ANGLES[2] = (float) Math.PI + a;
		ANGLES[3] = 2 * (float) Math.PI - a;
	}

	public Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		radius = 10;
		angleSins = new float[4];
		angleCosins = new float[4];
		distA = (float) Math.sqrt(Math.pow(radius * 1.5, 2) + Math.pow(radius / 2, 2));
		calcAngles();
	}

	@Override
	public void draw(Graphics g, float s) {
		g.fillOval(tfm(x - RADIUS, s), tfm(y - RADIUS, s), tfm(2 * RADIUS, s), tfm(2 * RADIUS, s));
	}

	private static int tfm(double x, float scale) {
		return (int) Math.round(scale * x);
	}

	@Override
	public void tick() {
		//move Player when keys pressed
		keyUpTimestamp = gm.getKeyUpTimestamp();
		keyDownTimestamp = gm.getKeyDownTimestamp();

		tickTimestamp = System.nanoTime();

		for (int key = 0; key < keyUpTimestamp.length; key++) {

			if (keyUpTimestamp[key] < keyUpTimestamp[key]) {
				//key still pressed
				downtime = tickTimestamp - lastTickTimestamp;
			} else if (keyUpTimestamp[key] > lastTickTimestamp) {
				//key was released in last tick
				downtime = keyUpTimestamp[key] - lastTickTimestamp;
			}
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
