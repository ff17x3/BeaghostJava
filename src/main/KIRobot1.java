package main;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Ma on 06.05.2016
 */
public class KIRobot1 extends Robot implements Entity.Tickable {
	public static final int LOOK_AROUND = 0, SLEEP = 1, ROTATE_TO_TARGET = 2, WALK_TO_TARGET = 3;
	//	public static final long MIN_LOOK_AROUND_DURATION = (long) 2e9, MAX_LOOK_AROUND_DURATION = (long) 4e9;
	public static final long MIN_SLEEP_DURATION = (long) 3e9, MAX_SLEEP_DURATION = (long) 5e9;
	// RPS = radians per second; GUPS = game units per second
	public static final int MIN_ROTATION_RPS = (int) (Math.PI / 3f), MAX_ROTATION_RPS = (int) (Math.PI);
	public static final int MIN_SPEED_GUPS = 20, MAX_SPEED_GUPS = 75;
	//	public static final int MIN_SPEED_GUPS = 1, MAX_SPEED_GUPS = 3;
	public static final float VIEWRAD = RADIUS * 7;
	public static final float MAX_ATTENTION = 10f;

	private float kIStrength = 0.5f;

	private int state;
	private long stateStartNanos, stateDurationNanos;
	private float rotationRPS = 0f;
	private float destX, destY;

	private float angleToTarget;


	//static:
	private static float dash[] = {10f, 20f};
	private static float lineThickness = 1f;
	private static BasicStroke dashedStroke = new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);

	private boolean isSeeing;


	public KIRobot1(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		nextRandomState();
	}

	@Override
	public void tick(long nanos) {
		super.tick(nanos);
		if (!isDead) {
			if (state != SLEEP && sees(gm.getPlayer())) {
				isSeeing = true;
				if (gm.getAttention() < MAX_ATTENTION) {
//					attention += kIStrength / (1.5 * Math.log(1 + GameManager.entf(this, gm.getPlayer())));
					gm.setAttention((float) (gm.getAttention() + kIStrength / (0.07 * GameManager.entf(this, gm.getPlayer()))));
//					System.out.println("attention = " + attention);
					if (gm.getAttention() > MAX_ATTENTION) {
						gm.setAttention(MAX_ATTENTION);
						gm.stopTicking();
						try {
							Thread.sleep(500);
						} catch (InterruptedException ignored) {
						}
						gm.reset();
						JOptionPane.showMessageDialog(null, "Du wurdest entdeckt!", "GAME OVER", JOptionPane.INFORMATION_MESSAGE);
					}
					setLineToAtLvl(gm.getAttention());
				}
			} else {
				isSeeing = false;

			}
			switch (state) {
				case WALK_TO_TARGET:
					float distance = Math.abs(x - destX) + Math.abs(y - destY);
					float oldX = x, oldY = y;
					moveDir(nanos);
					if (Math.signum(oldX - destX) != Math.signum(x - destX) || Math.signum(oldY - destY) != Math.signum(y - destY)) {
						x = destX;
						y = destY;
//						System.out.println("target reached");
						nextRandomState();
					}
					break;
				case ROTATE_TO_TARGET:
					float dirchange = rotationRPS * nanos / 1e9f;
					float oldDir = getDir();
					changeDir(dirchange);

//				if (Math.abs(getDir() - angleToTarget) < 2 * dirchange) {
					if (Math.signum(oldDir - angleToTarget) != Math.signum(getDir() - angleToTarget)) {
//						System.out.println("target found!");
						setDir(angleToTarget);
						enableWalk();
					}
					break;
				case LOOK_AROUND:
					// TODO zwischendrin zufällig warten oder nicht drehen oder Richtung/Geschwindigkeit ändern
					changeDir(rotationRPS * nanos / 1e9f);
					if (!stateStillRunning())
						nextRandomState();
					break;
				case SLEEP:
					if (!stateStillRunning())
						nextRandomState();
					break;
			}
		}
	}

	private void nextRandomState() {
		int state = (int) (Math.random() * 3d);
		switch (state) {
			case LOOK_AROUND:
				enableLookaround();
				break;
			case SLEEP:
				enableSleep();
				break;
			case ROTATE_TO_TARGET:
				enableRotateToTarget();
				break;
		}
	}

	public static void setLineToAtLvl(float attention) {
		lineThickness = 1 + 10 * (attention / MAX_ATTENTION);
		dash[1] = 20 * (1 - attention / MAX_ATTENTION);
		dashedStroke = new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		super.draw(g, scale);
		if (isSeeing) {
			((Graphics2D) g).setStroke(dashedStroke);
			g.drawLine(tfm(x), tfm(y), tfm(gm.getPlayer().x), tfm(gm.getPlayer().y));
			((Graphics2D) g).setStroke(noStroke);
		}
		if (state == ROTATE_TO_TARGET || state == WALK_TO_TARGET) {
			g.fillOval(tfm(destX - 2), tfm(destY - 2), tfm(4), tfm(4));
		}
	}

	private void enableSleep() {
		setDrawViewField(0f);
		rotationRPS = 0f;
		speedGUPS = 0f;
		state = SLEEP;
		makeStateTimes();
//		System.out.println("enabled sleep");
	}

	// OK
	private void enableRotateToTarget() {
		destX = (float) Math.random() * (gm.getMapWidth() - 2 * boundingRadius) + boundingRadius;
		destY = (float) Math.random() * (gm.getMapHeight() - 2 * boundingRadius) + boundingRadius;

		angleToTarget = ((float) (Math.atan2(destY - y, destX - x) + 2 * Math.PI) % ((float) Math.PI * 2f));

//		System.out.print("destX = " + destX);
//		System.out.print(", destY = " + destY);
//		System.out.println(", angle = " + Math.toDegrees(angleToTarget));
		state = ROTATE_TO_TARGET;
		setDrawViewField(VIEWRAD);

		float diffRight = angleToTarget - getDir();
//		System.out.print("diffRight = " + Math.toDegrees(diffRight));
//		System.out.println(",dir = " + Math.toDegrees(getDir()));
		rotationRPS = (float) (Math.random() * (MAX_ROTATION_RPS - MIN_ROTATION_RPS) + MIN_ROTATION_RPS);
		if (diffRight > Math.PI || diffRight < 0) {
			rotationRPS *= -1;
		}
//		System.out.println("rotate to target enabled, rotationRPS = " + rotationRPS);
	}

	// OK
	private void enableWalk() {
		speedGUPS = (float) (Math.random() * (MAX_SPEED_GUPS - MIN_SPEED_GUPS) + MAX_SPEED_GUPS);
		rotationRPS = 0f;
		state = WALK_TO_TARGET;
//		System.out.println("enabled walk! speed: " + speedGUPS);
	}

	private void enableLookaround() {
		rotationRPS = (Math.random() > 0.5f ? -1 : 1) * (float) (Math.random() * (MAX_ROTATION_RPS - MIN_ROTATION_RPS) + MIN_ROTATION_RPS);
		speedGUPS = 0f;
		setDrawViewField(VIEWRAD);
		state = LOOK_AROUND;

//		System.out.println("lookaround enabled");
		makeStateTimes();
	}

	private void makeStateTimes() {
		stateStartNanos = System.nanoTime();
		stateDurationNanos = (long) (Math.random() * (MAX_SLEEP_DURATION - MIN_SLEEP_DURATION) + MIN_SLEEP_DURATION);
	}

	private boolean stateStillRunning() {
		return stateDurationNanos + stateStartNanos > System.nanoTime();
	}
}
