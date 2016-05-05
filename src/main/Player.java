package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016.
 */
public class Player extends Entity implements Entity.Tickable {

	private Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
	}

	@Override
	public void draw(Graphics g, float scale) {

	}

	@Override
	public void tick() {

	}
}
