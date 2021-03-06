package main;

import util.DrawInferface;
import util.ScaleChangeListener;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public abstract class Entity implements DrawInferface, ScaleChangeListener {
	protected float scale;
	protected float speedGUPS;
	protected float boundingRadius;
	protected float FOV;

	public static final BasicStroke noStroke = new BasicStroke(1f);


	public interface Tickable {
		boolean tick(long nanosDelta);
	}

	protected float x, y;
	private float dir;

	protected GameManager gm;


	public Entity(float x, float y, float dir, GameManager gm) {
		// colors.. body: 0xff990000 box: 0xff000000
		this.x = x;
		this.y = y;
		this.gm = gm;
		this.dir = dir;
	}


	public void changeDir(float change) {
		dir += change + 2 * Math.PI;
		dir %= 2 * Math.PI;
	}

	protected void moveDir(long time) {
		float dis = (float) (speedGUPS * time / 1e9);
		float dy = (float) (Math.sin(dir) * dis);
		float dx = (float) (Math.cos(dir) * dis);

		x += dx;
		y += dy;
	}

	protected int tfm(double v) {
		return (int) Math.round(scale * v);
	}

	public void setDir(float dir) {
		this.dir = dir;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	@Override
	public void onScaleChange(float scale) {
		this.scale = scale;
	}

	protected boolean sees(Entity e) {//TODO
		float angle = posAngle((float) ((Math.atan2(e.getY() - y, e.getX() - x))));

		float min = dir - FOV / 2;
		float max = dir + FOV / 2;
		if (min < 0) {
			min += Math.PI * 2;
		} else if (max > Math.PI * 2) {
			max -= Math.PI * 2;
		} else {
			//"normal case"
			return angle > min && angle < max;
		}
		return (angle > min && angle < Math.PI * 2) || (angle > 0 && angle < max);
	}

	public static float posAngle(float angle) {
		return (float) ((angle + 2 * Math.PI) % (2 * Math.PI));
	}

	/**
	 * a ist between padding and max-padding
	 *
	 * @param a
	 * @param change
	 * @param max
	 * @param padding
	 * @return
	 */
	public static float add(float a, float change, float max, float padding) {
		a += change;
		if (a + padding > max)
			a = max - padding;
		else if (a - padding < 0)
			a = padding;
		return a;
	}

	public float getDir() {
		return dir;
	}
}
