package main;

/**
 * Created by Florian on 05.05.2016.
 */
public class Robot extends Entity {
	public static final float RADIUS = 5;

	private Robot(float x, float y, float dir) {
		super(x, y, dir);
	}


	public static Robot spawnRandom(float mapWidth, float mapHeight) {
		float x = (float) (Math.random() * (mapWidth - 2 * RADIUS) + RADIUS);
		float y = (float) (Math.random() * (mapHeight - 2 * RADIUS) + RADIUS);
		float dir = (float) (Math.random() * (2 * Math.PI));
		return new Robot(x, y, dir);
	}
}
