package main;

import util.ClockNano;
import util.DimensionF;
import util.DrawInferface;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Florian on 05.05.2016
 */
public class GameManager implements DrawInferface { // bla

	// params:
	public static final int FPS = 60;
	public static final int robotCount = 10;


	//vars
	private Main main;
	private ClockNano drawClock, tickClock;
	private ArrayList<Robot> robots = new ArrayList<>();
	private Player player;
	private DimensionF mapSize;

	public GameManager(Main main, DimensionF mapSize) {
		this.main = main;
		this.mapSize = mapSize;

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
		float mapWidth = mapSize.getWidth();
		float mapHeight = mapSize.getHeight();

		for (int i = 0; i < robotCount; i++) {
			robots.add(Robot.spawnRandom(mapWidth, mapHeight, this));
		}
	}

	@Override
	public void draw(Graphics g, float s) {
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, tfm(mapSize.getWidth(), s), tfm(mapSize.getHeight(), s));
		for (Robot robot : robots) {
			robot.draw(g, s);
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

	private static int tfm(double x, float scale) {
		return (int) Math.round(scale * x);
	}
}
