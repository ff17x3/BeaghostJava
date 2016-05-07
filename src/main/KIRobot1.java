package main;

/**
 * Created by Ma on 06.05.2016
 */
public class KIRobot1 extends Robot implements Entity.Tickable {
	public static final int LOOK_AROUND = 0, WALK = 1, SLEEP = 2;
	public static final long MIN_LOOK_AROUND_DURATION = (long) 2e9, MAX_LOOK_AROUND_DURATION = (long) 4e9;
	public static final long MIN_SLEEP_DURATION = (long) 3e9, MAX_SLEEP_DURATION = (long) 5e9;
	// RPS = radians per second; GUPS = game units per second
	public static final int MIN_ROTATION_RPS = (int) (Math.PI / 3f), MAX_ROTATION_RPS = (int) (Math.PI);
	public static final int MIN_SPEED_GUPS = 20, MAX_SPEED_GUPS = 75;

	private int state;
	private long stateStartNanos, stateDurationNanos;
	private float speedGUPS = 0f, rotationRPS = 0f;

	public KIRobot1(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
	}

	@Override
	public void tick() {
		super.tick();
	}


	private void enableSleep() {
		setDrawViewField(0f);
		rotationRPS = 0f;
		speedGUPS = 0f;
	}

	private void enableWalk() {
		speedGUPS = (float) (Math.random() * (MAX_SPEED_GUPS - MIN_SPEED_GUPS) + MAX_SPEED_GUPS);
	}

	private void enableLookaround() {
		rotationRPS = Math.random() > 0 ? -1 : 1 * (float) (Math.random() * (MAX_ROTATION_RPS - MIN_ROTATION_RPS) + MIN_ROTATION_RPS);
	}
}
