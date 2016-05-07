package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Player extends Entity implements Entity.Tickable {

	//const
	public static final int CM_KEYS = 0, CM_MOUSE = 1;

	// movement
	private float speed_ps = 100;
	private float spawnPrtRadius = 100f;

	// keys
	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;
	private int controlMode = CM_MOUSE;

	//ticking
	private float angleTick;// in welche Richtung bewegt sich der Player in diesem Tick
	private float angleTickDowntime;
	private float maxDowntime;

	// drawing#####################
	private float[] angleSins, angleCosins;
	private Polygon poly;
	private final long weaponShowTime = (long) (0.3 * 1e9);
	private long weaponShowStartTime;
	private boolean isPunching;

	// hardcode CONSTANTS
	private static final float[] ANGLES;
	private static final float RADIUS = 10;
	private static final float distA, distB, distC;

	static {
		float a = (float) Math.atan(1 / 3d);
		ANGLES = new float[7];
		ANGLES[0] = (float) Math.PI / 2 + a;
		ANGLES[1] = 3 * (float) Math.PI / 2 - a;
		ANGLES[2] = 3 * (float) Math.PI / 2 + a;
		ANGLES[3] = (float) Math.PI / 2 - a;

		ANGLES[4] = (float) Math.PI / 2;
		ANGLES[5] = 3 * (float) Math.PI / 2;

		ANGLES[6] = 0f;

		distA = (float) Math.sqrt(Math.pow(RADIUS * 1.5, 2) + Math.pow(RADIUS / 2, 2));
		distB = RADIUS * 1.5f;
		distC = (float) Math.sqrt(Math.pow(RADIUS * 1.5f, 2) + Math.pow(GameManager.playerPunchRange - RADIUS, 2));
	}
	// ########################

	public Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		angleSins = new float[ANGLES.length];
		angleCosins = new float[ANGLES.length];
		poly = new Polygon();
		calcAngles();
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		// TODO Colors
		// shoulders
		g.setColor(Color.DARK_GRAY);
		g.fillOval(tfm(angleCosins[4] * distB + x - RADIUS / 2), tfm(angleSins[4] * distB + y - RADIUS / 2), tfm(RADIUS), tfm(RADIUS));
		g.fillOval(tfm(angleCosins[5] * distB + x - RADIUS / 2), tfm(angleSins[5] * distB + y - RADIUS / 2), tfm(RADIUS), tfm(RADIUS));
		poly.reset();
		poly.addPoint(tfm(angleCosins[0] * distA + x), tfm(angleSins[0] * distA + y));
		poly.addPoint(tfm(angleCosins[1] * distA + x), tfm(angleSins[1] * distA + y));
		poly.addPoint(tfm(angleCosins[2] * distA + x), tfm(angleSins[2] * distA + y));
		poly.addPoint(tfm(angleCosins[3] * distA + x), tfm(angleSins[3] * distA + y));
		g.fillPolygon(poly);
		// center circle
		g.setColor(Color.GRAY);
		g.fillOval(tfm(x - RADIUS), tfm(y - RADIUS), tfm(2 * RADIUS), tfm(2 * RADIUS));

//		g.setColor(Color.RED);
//		drawCross(g, new Point(tfm(x), tfm(y)), 3);
		if (isPunching) {
			if ((System.nanoTime() - weaponShowStartTime) > weaponShowTime) {
				isPunching = false;
			} else {
				Graphics2D g2d = ((Graphics2D) g);
				g2d.setStroke(new BasicStroke(RADIUS / 6));
				g2d.drawLine(tfm(angleCosins[5] * distB + x),
						tfm(angleSins[5] * distB + y),
						tfm(angleCosins[6] * distC + x),
						tfm(angleSins[6] * distC + y));
				g2d.fillOval(tfm(angleCosins[6] * distC + x - RADIUS / 2),
						tfm(angleSins[6] * distC + y - RADIUS / 2),
						tfm(RADIUS),
						tfm(RADIUS));
				g2d.setStroke(new BasicStroke(1f));
			}
		}
	}

	@Override
	public synchronized void tick() {
		// move Player when keys pressed
		keyUpTimestamp = gm.getKeyUpTimestamp();
		keyDownTimestamp = gm.getKeyDownTimestamp();

		tickTimestamp = System.nanoTime();

		if (controlMode == CM_MOUSE)
			updateDir();
		angleTick = 0;
		angleTickDowntime = 0;
		maxDowntime = 0;
		for (int key = 0; key < keyUpTimestamp.length; key++) {

			if (keyUpTimestamp[key] < keyDownTimestamp[key]) {
				// key still pressed
				downtime = tickTimestamp - lastTickTimestamp;
			} else if (keyUpTimestamp[key] > lastTickTimestamp) {
				//key was released in last tick
				downtime = keyUpTimestamp[key] - lastTickTimestamp;
			} else
				continue;

			if (controlMode == CM_MOUSE)
				moveDirKey(key, downtime);

			float keyAngle = getAngleByKey(key);

			if (angleTick - keyAngle < -Math.PI)
				angleTick += Math.PI * 2;
			else if (angleTick - keyAngle > Math.PI)
				angleTick -= Math.PI * 2;

			angleTick = (angleTick * angleTickDowntime + keyAngle * downtime) / (angleTickDowntime + downtime);
			angleTickDowntime += downtime;
			maxDowntime = Math.max(maxDowntime, downtime);
		}
		if (angleTickDowntime != 0 && controlMode == CM_KEYS) {
			setDir(angleTick);
			moveDir(maxDowntime);
		}

		lastTickTimestamp = tickTimestamp;
	}


	private Float getAngleByKey(int key) {
		float angle;
		switch (key) {
			case 1://a,left
				angle = (float) (Math.PI);
				break;
			case 2://s,back
				angle = (float) (Math.PI / 2);
				break;
			case 3://d,right
				angle = 0;
				break;
			default:
				angle = (float) (-Math.PI / 2);
		}
		return angle;
	}

	private void updateDir() {
		float mouseX = gm.getMouseOnscreenX() / scale;
		float mouseY = gm.getMouseOnscreenY() / scale;
		setDir((float) Math.atan2((mouseY - y), (mouseX - x)));
	}

	/**
	 * @param dirKey 0:w, 1:a, 2:s, 3:d
	 * @param time
	 */
	private void moveDirKey(int dirKey, long time) {
		if (dirKey == 0)//=='w'
			moveDir(time);
	}

	private void moveDir(float time) {
		float dis = (float) (speed_ps * time / 1e9);
		float dy = (float) (Math.sin(dir) * dis);
		float dx = (float) (Math.cos(dir) * dis);

		x = add(x, dx, gm.getMapWidth(), 2 * RADIUS);
		y = add(y, dy, gm.getMapHeight(), 2 * RADIUS);
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

	public void punchStart() {
		weaponShowStartTime = System.nanoTime();
		isPunching = true;
	}

	public float getSpawnPrtRadius() {
		return spawnPrtRadius;
	}
	//	public static void drawCross(Graphics g, Point middle, int halfBoxSize) {
//		g.drawLine(middle.x - halfBoxSize, middle.y - halfBoxSize, middle.x + halfBoxSize, middle.y + halfBoxSize);
//		g.drawLine(middle.x - halfBoxSize, middle.y + halfBoxSize, middle.x + halfBoxSize, middle.y - halfBoxSize);
//	}
}
