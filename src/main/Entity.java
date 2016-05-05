package main;

import util.DrawInferface;

/**
 * Created by Florian on 05.05.2016
 */
public abstract class Entity implements DrawInferface {
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

    public void setDir(float dir) {
        this.dir = dir;
    }

}
