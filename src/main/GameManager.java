package main;

import util.ClockNano;
import util.Tickable;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian on 05.05.2016
 */
public class GameManager {

	// params:
	public static final int FPS = 60;

	private Main main;
	private ClockNano drawClock, tickClock;
	private ArrayList<Robot> robots = new ArrayList<>();

	public GameManager(Main main) {
		this.main = main;

		//TODO spawn robots

		drawClock = new ClockNano(FPS, (Tickable) millisDelta -> {
			for (Robot r : robots) {
				r.draw();
			}

		});
		tickClock = new ClockNano(FPS, (Tickable) millisDelta -> {
			for (Robot r : robots) {
				r.tick();
			}
		});
	}




	public void startTicking() {

	}

	public void stopTicking() {

	}
}
