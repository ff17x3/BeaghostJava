package main;

/**
 * Created by Ma on 06.05.2016
 */
public class KIRobot1 extends Robot implements Entity.Tickable {
	public static final int LOOK_AROUND = 0, WALK = 1, SLEEP = 2;

	private int state;

	public KIRobot1(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
	}

	@Override
	public void tick() {
		super.tick();
		die();
	}
}
