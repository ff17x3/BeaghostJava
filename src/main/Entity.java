package main;

import util.DrawInferface;
import util.ScaleChangeListener;

/**
 * Created by Florian on 05.05.2016
 */
public abstract class Entity implements DrawInferface, ScaleChangeListener {
	protected float scale;

	public interface Tickable{
		void tick();
	}

    protected float x, y, dir;

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
}
