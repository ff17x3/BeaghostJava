package main;

import java.awt.*;

/**
 * Created by Ma on 06.05.2016
 */
public class KIRobot1 extends Robot implements Entity.Tickable {
	public static final int LOOK_AROUND = 0, WALK_TO_TARGET = 1, SLEEP = 2, ROTATE_TO_TARGET = 3;
	public static final long MIN_LOOK_AROUND_DURATION = (long) 2e9, MAX_LOOK_AROUND_DURATION = (long) 4e9;
	public static final long MIN_SLEEP_DURATION = (long) 3e9, MAX_SLEEP_DURATION = (long) 5e9;
	// RPS = radians per second; GUPS = game units per second
	public static final int MIN_ROTATION_RPS = (int) (Math.PI / 3f), MAX_ROTATION_RPS = (int) (Math.PI);
	public static final int MIN_SPEED_GUPS = 20, MAX_SPEED_GUPS = 75;
	public static final float VIEWRAD = RADIUS * 7;
	public static final float MAX_ATTENTION = 20f;

	private int state;
	private long stateStartNanos, stateDurationNanos;
	private float speedGUPS = 0f, rotationRPS = 0f;
	private float destX, destY;
	private float attention = 0f;


	//drawing
	private float dash[] = {10f, 20f};
	private float lineThickness = 2f;
	private BasicStroke dashedStroke = new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);


	public KIRobot1(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
		nextRandomState();
	}

	@Override
	public void tick(long nanos) {
		super.tick(nanos);
		if (state != SLEEP && sees(gm.getPlayer())) {
			attention += 0.1f;
			setLineToAtLvl();

		} else if (attention > 0) {
			attention = add(attention, -0.005f, Float.MAX_VALUE, 0);
			setLineToAtLvl();
		}
		switch (state) {
			case WALK_TO_TARGET:
				float distance = Math.abs(x - destX) + Math.abs(y - destY);
				if (distance > 0.5f)
					moveDir(nanos);
				else
					nextRandomState();
				break;
			case ROTATE_TO_TARGET:
				dir += rotationRPS;
				float angleToTarget = (float) Math.atan2(destY - y, destX - x);
				if (Math.abs(dir - angleToTarget) < 0.01) {
					dir = angleToTarget;
					enableWalk();
				}
				break;
			case LOOK_AROUND:
				// TODO zwischendrin zufällig warten oder nicht drehen oder Richtung/Geschwindigkeit ändern
				dir += rotationRPS;
				if (!stateStillRunning())
					nextRandomState();
				break;
			case SLEEP:
				if (!stateStillRunning())
					nextRandomState();
				break;
		}
	}

	private void nextRandomState() {
		int state = (int) (Math.random() * 3);
		switch (state) {
			case 0:
				enableRotateToTarget();
				break;
			case 1:
				enableLookaround();
				break;
			case 2:
				enableSleep();
				break;
		}
	}

	private void setLineToAtLvl() {
		lineThickness += 10 * (1 - attention / MAX_ATTENTION);
		dash[1] = 20 * (1 - attention / MAX_ATTENTION);
		dashedStroke = new BasicStroke(lineThickness, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, dash, 0.0f);
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		super.draw(g, scale);
		((Graphics2D) g).setStroke(dashedStroke);
		g.drawLine(tfm(x), tfm(y), tfm(gm.getPlayer().x), tfm(gm.getPlayer().y));
		((Graphics2D) g).setStroke(noStroke);
	}

	private void enableSleep() {
		setDrawViewField(0f);
		rotationRPS = 0f;
		speedGUPS = 0f;
		state = SLEEP;
		makeStateTimes();
	}

	// OK
	private void enableRotateToTarget() {
		destX = (float) Math.random() * gm.getMapWidth();
		destY = (float) Math.random() * gm.getMapHeight();

		state = ROTATE_TO_TARGET;
		setDrawViewField(VIEWRAD);

		float angleToTarget = (float) Math.atan2(destY - y, destX - x);
		float diffRight = Math.abs(angleToTarget - dir), diffLeft = Math.abs(dir - angleToTarget);
		rotationRPS = (float) (Math.random() * (MAX_ROTATION_RPS - MIN_ROTATION_RPS) + MIN_ROTATION_RPS);
		if (diffLeft < diffRight) {
			rotationRPS *= -1;
		}
	}

	// OK
	private void enableWalk() {
		speedGUPS = (float) (Math.random() * (MAX_SPEED_GUPS - MIN_SPEED_GUPS) + MAX_SPEED_GUPS);
		rotationRPS = 0f;
		state = WALK_TO_TARGET;
	}

	private void enableLookaround() {
		rotationRPS = Math.random() > 0.5f ? -1 : 1 * (float) (Math.random() * (MAX_ROTATION_RPS - MIN_ROTATION_RPS) + MIN_ROTATION_RPS);
		speedGUPS = 0f;
		setDrawViewField(VIEWRAD);
		state = LOOK_AROUND;

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
