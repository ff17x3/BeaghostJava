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
	public static final int robotCount = 10;

	private Main main;
	private ClockNano drawClock, tickClock;
	private ArrayList<Robot> robots = new ArrayList<>();

	public GameManager(Main main) {
		this.main = main;

		//TODO spawn robots
		spawnRobots(robotCount);

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

	private void spawnRobots(int robotCount) {
		float mapWidth = main.getMapSize().getWidth();
		float mapHeight = main.getMapSize().getHeight();
	}


	public void startTicking() {
		drawClock.startTicking();
		tickClock.startTicking();
		System.out.println("GameManager.startTicking");
	}

	public void stopTicking() {
		drawClock.stopTicking();
		tickClock.stopTicking();
		System.out.println("GameManager.stopTicking");
	}
}
