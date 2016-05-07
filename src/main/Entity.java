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
		void tick(long nanosDelta);
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
		dir += change;
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

	protected boolean sees(Entity e) {
		float angle = (float) Math.atan2(e.getY() - y, e.getX() - x);
		return angle >= dir - FOV / 2 && angle <= dir + FOV / 2;
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
	protected float add(float a, float change, float max, float padding) {
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
