package main;

import util.ClockNano;

import java.util.ArrayList;

/**
 * Created by Florian on 05.05.2016
 */
public class GameManager { // bla

	// params:
	public static final int FPS = 60;
	public static final int robotCount = 10;


	//vars
	private Main main;
	private ClockNano drawClock, tickClock;
	private ArrayList<Robot> robots = new ArrayList<>();
	private Player player;

	public GameManager(Main main) {
		this.main = main;

		//fills the ArrayList robots
		spawnRobots(robotCount);

		drawClock = new ClockNano(FPS, millisDelta -> {
			main.getFrame().redraw();
		});
		tickClock = new ClockNano(FPS, millisDelta -> {
			for (Robot r : robots) {
				r.tick();
			}
		});

	}

	private void spawnRobots(int robotCount) {
		float mapWidth = main.getMapSize().getWidth();
		float mapHeight = main.getMapSize().getHeight();

		for (int i = 0; i < robotCount; i++) {
			robots.add(Robot.spawnRandom(mapWidth, mapHeight, this));
		}
	}


	public void startTicking() {
		tickClock.startTicking();
		drawClock.startTicking();
		System.out.println("GameManager.startTicking");
	}

	public void stopTicking() {
		drawClock.stopTicking();
		tickClock.stopTicking();
		System.out.println("GameManager.stopTicking");
	}
}
