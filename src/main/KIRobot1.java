package main;

/**
 * Created by Ma on 06.05.2016
 */
public class KIRobot1 extends Robot implements Entity.Tickable {
	public static final int LOOK_AROUND = 0, WALK = 1, SLEEP = 2;
	public static final long MIN_LOOK_AROUND_DURATION = (long) 2e9, MAX_LOOK_AROUND_DURATION = (long) 4e9;
	public static final long MIN_SLEEP_DURATION = (long) 3e9, MAX_SLEEP_DURATION = (long) 5e9;
	// RPS = radians per second
	public static final int MIN_ROTATION_RPS = (int) (Math.PI / 3f), MAX_ROTATION_RPS = (int) (Math.PI);

	private int state;
	private long stateStartNanos, stateDurationNanos;

	public KIRobot1(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
	}

	@Override
	public void tick() {
		super.tick();
		die();
	}


	private void enableSleep() {
		setDrawViewField(0f);
	}
}
