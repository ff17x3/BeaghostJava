package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Player extends Entity implements Entity.Tickable {

	//const
	public static final int CM_KEYS = 0, CM_MOUSE = 1;

	// movement
	private float spawnPrtRadius = 100f;
	private long weaponCooldown = (long) 3e9;

	// keys
	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;
	private int controlMode = CM_MOUSE;

	private float mouseX, mouseY;

	//ticking
	private float angleTick;// in welche Richtung bewegt sich der Player in diesem Tick
	private long angleTickDowntime;
	private long maxDowntime;

	// drawing#####################
	private float[] angleSins, angleCosins;
	private Polygon poly;
	private final long weaponShowTime = (long) (0.3 * 1e9);
	private boolean isCooldownRunning;
	private long weaponShowStartTime;

	// hardcode CONSTANTS
	private static final float[] ANGLES;
	private static final float RADIUS = 10, MOUSE_RADIUS = 50f;
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
		boundingRadius = RADIUS;
		FOV = (float) Math.toRadians(60);
		speedGUPS = 500;
		poly = new Polygon();
		calcAngles();
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
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
		g.setColor(Color.DARK_GRAY.darker());
		float fakt;
		if (isCooldownRunning) {
			long time = System.nanoTime();
			fakt = 1 - (1f * time - weaponShowStartTime) / weaponCooldown;
			if (fakt <= 0) {
				isCooldownRunning = false;
			}

		} else {
			fakt = 0;
		}
		Graphics2D g2d = ((Graphics2D) g);
		g2d.setStroke(new BasicStroke(RADIUS / 6));
		g2d.drawLine(tfm(angleCosins[5] * distB + x),
				tfm(angleSins[5] * distB + y),
				tfm(angleCosins[6] * distC * fakt + x),
				tfm(angleSins[6] * distC * fakt + y));

		g2d.drawLine(tfm(angleCosins[4] * distB + x),
				tfm(angleSins[4] * distB + y),
				tfm(angleCosins[6] * distC * fakt + x),
				tfm(angleSins[6] * distC * fakt + y));

		g2d.fillOval(tfm(angleCosins[6] * distC * fakt + x - RADIUS / 2),
				tfm(angleSins[6] * distC * fakt + y - RADIUS / 2),
				tfm(RADIUS),
				tfm(RADIUS));
		g2d.setStroke(noStroke);
	}

	@Override
	public synchronized boolean tick(long naosDelta) {
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
		return false;
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
		mouseX = gm.getMouseOnscreenX() / scale;
		mouseY = gm.getMouseOnscreenY() / scale;
		if (!((Math.abs(mouseX - x) <= 2) && (Math.abs(mouseY - y) <= 2)))
			setDir((float) ((Math.atan2((mouseY - y), (mouseX - x)) + 2 * Math.PI) % (2 * Math.PI)));
	}

	/**
	 * @param dirKey 0:w, 1:a, 2:s, 3:d
	 * @param time
	 */
	private void moveDirKey(int dirKey, long time) {
		if (dirKey == 0)//=='w'
			moveDir(time);
	}

	@Override
	protected void moveDir(long time) {
		float dis = (float) (speedGUPS * time / 1e9);
		float dy = (float) (Math.sin(getDir()) * dis);
		float dx = (float) (Math.cos(getDir()) * dis);

		float newX = add(x, dx, gm.getMapWidth(), 2 * boundingRadius);
		float newY = add(y, dy, gm.getMapHeight(), 2 * boundingRadius);
		//old version:
//		if (Math.signum(x - mouseX) != Math.signum(newX - mouseX))
//			newX = mouseX;
//		if (Math.signum(y - mouseY) != Math.signum(newY - mouseY))
//			newY = mouseY;+
		if (Math.sqrt(Math.pow(x - mouseX, 2) + Math.pow(y - mouseY, 2)) - MOUSE_RADIUS > 0.00001f) {
			while (Math.sqrt(Math.pow(newX - mouseX, 2) + Math.pow(newY - mouseY, 2)) < MOUSE_RADIUS) {
				newX = x + (newX - x) / 2;
				newY = y + (newY - y) / 2;
			}
			x = newX;
			y = newY;
		}
	}


	@Override
	public void setDir(float dir) {
		super.setDir(dir);
		calcAngles();
	}

	private void calcAngles() {
		// Werte für Boxen an den Seiten an Winkel anpassen
		for (int i = 0; i < ANGLES.length; i++) {
			float actangle = getDir() + ANGLES[i];
			actangle %= 2 * Math.PI;
			angleSins[i] = (float) Math.sin(actangle);
			angleCosins[i] = (float) Math.cos(actangle);
		}
	}

	/**
	 * @return if move is legit
	 */
	public boolean punchStart() {
		long time = System.nanoTime();
		if (time - weaponShowStartTime >= weaponCooldown) {
			weaponShowStartTime = time;
			isCooldownRunning = true;
			return true;
		}
		return false;
	}

	public float getSpawnPrtRadius() {
		return spawnPrtRadius;
	}
	//	public static void drawCross(Graphics g, Point middle, int halfBoxSize) {
//		g.drawLine(middle.x - halfBoxSize, middle.y - halfBoxSize, middle.x + halfBoxSize, middle.y + halfBoxSize);
//		g.drawLine(middle.x - halfBoxSize, middle.y + halfBoxSize, middle.x + halfBoxSize, middle.y - halfBoxSize);
//	}
}
