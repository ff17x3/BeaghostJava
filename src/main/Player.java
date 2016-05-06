package main;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016.
 */
public class Player extends Entity implements Entity.Tickable {

	private long[] keyUpTimestamp, keyDownTimestamp;
	private long lastTickTimestamp = System.nanoTime(), tickTimestamp, downtime;

	private Player(float x, float y, float dir, GameManager gm) {
		super(x, y, dir, gm);
	}

	@Override
	public void draw(Graphics g, float scale) {

	}

	@Override
	public void tick() {
		//move Player when keys pressed
		keyUpTimestamp = gm.getKeyUpTimestamp();
		keyDownTimestamp = gm.getKeyDownTimestamp();

		tickTimestamp = System.nanoTime();

		for (int key = 0; key < keyUpTimestamp.length; key++) {
			if (keyDownTimestamp[key] > lastTickTimestamp) {
				if ((downtime = keyUpTimestamp[key] - keyDownTimestamp[key]) < 0)
					downtime = keyDownTimestamp[key] - lastTickTimestamp;
				//key war für downtime micros gedrückt


			} else if (keyUpTimestamp[key] < keyUpTimestamp[key]) {
				//key still pressed

			}
		}


		lastTickTimestamp = tickTimestamp;
	}
}
