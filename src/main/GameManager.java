package main;

import util.ClockNano;
import util.DimensionF;
import util.DrawInferface;
import util.ScaleChangeListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by Florian on 05.05.2016
 */
public class GameManager implements DrawInferface, ScaleChangeListener { // bla


	// params:
	public static final int FPS = 60;
	public static final int robotCount = 10;
	public static final float playerPunchRange = 20;
	public static final float playerPunchAngle = (float) Math.toRadians(7.5f);//for each side

	//vars
	private Main main;
	private ClockNano drawClock, tickClock;
	private ArrayList<Robot> robots = new ArrayList<>();
	private Player player;
	private DimensionF mapSize;
	private int counter = 0;
	//    private long timeOld = System.nanoTime(), time;
	private int inteval = 60;
	private long[] times = new long[inteval];
	private float avg = 0;
	private int avgcount = 0;
	private Stack<Robot> toRemove = new Stack<>();
	private boolean playerIsPunching = false;

	public GameManager(Main main, DimensionF mapSize) {
		this.main = main;
		this.mapSize = mapSize;

        player = new Player(200, 200, 0, this);
        //fills the ArrayList robots
        spawnRobots(robotCount);

		System.out.println("spawend " + robotCount + " Robots");
		drawClock = new ClockNano(FPS, millisDelta -> {
			main.getFrame().redraw();

			if (counter == inteval) {
				printTimes();
//                timeOld = time;
				counter = 0;
			} else {
				times[counter] = System.nanoTime();
				counter++;
			}
		});
		tickClock = new ClockNano(FPS, millisDelta -> mainTick(FPS, millisDelta));
	}

	private synchronized void mainTick(int fps, int millisDelta) {
		if (playerIsPunching) {
			playerIsPunching = false;
			removeAllRobotsInPlayersReach();
		}

		for (Robot r : robots) {
			if (r instanceof Entity.Tickable)
				r.tick();
		}
		player.tick();
		for (Robot r : toRemove)
			robots.remove(r);
	}

	private void removeAllRobotsInPlayersReach() {
		for (Robot r : robots) {
			if (entf(r, player) <= playerPunchRange)
				if (robotInFOV(r))
					toRemove.add(r);
		}
	}

	private boolean robotInFOV(Robot r) {
		float angle = (float) Math.atan2(r.getY() - player.getY(), r.getX() - player.getX());
		return angle >= player.getDir() - playerPunchAngle && angle <= player.getDir() + playerPunchAngle;
	}


	private void printTimes() {
		new Thread() {
			public void run() {
				for (int i = 1; i < times.length; i++) {
					float t = (float) ((times[i] - times[i - 1]) * 1e-6);
					if (t > 0) {//avoiding strange bugs
						avg = (avg * avgcount + t) / (avgcount + 1);
						avgcount++;
						if (t > 1.2 * avg)
							System.out.println("FRAMEDROP: " + t + "ms needed (avg: " + avg + ")");
					}

				}

			}
		}.start();
	}

	private void spawnRobots(int robotCount) {
		float mapWidth = mapSize.getWidth();
		float mapHeight = mapSize.getHeight();
		float size = Robot.SIZE_RAD;

		float x, y, dir;
		for (int i = 0; i < robotCount; i++) {
			do {
				x = (float) (Math.random() * (mapWidth - 2 * size) + size);
				y = (float) (Math.random() * (mapHeight - 2 * size) + size);
			} while (!isFree(x, y, size));
			dir = (float) (Math.random() * (2 * Math.PI));
			robots.add(new Robot(x, y, dir, this));
		}
		robots.add(new KIRobot1(200, 200, 0, this));
	}

	private boolean isFree(float x, float y, float size) {
		for (Robot r : robots) {
			if (Math.sqrt(Math.pow((r.getX() - x), 2) + Math.pow((r.getY() - y), 2)) <= 2 * size)
				return false;
		}
		return Math.sqrt(Math.pow((player.getX() - x), 2) + Math.pow((player.getY() - y), 2)) >= player.getSpawnPrtRadius();
	}

	@Override
    public synchronized void draw(Graphics g1, float s) {
        Graphics2D g = (Graphics2D) g1;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, tfm(mapSize.getWidth(), s), tfm(mapSize.getHeight(), s));
		g.setColor(Color.RED);
		for (Robot robot : robots) {
			robot.draw(g, s);
		}
		g.setColor(Color.BLUE);
		player.draw(g, s);
	}

	private static float entf(Entity a, Entity b) {
		return (float) Math.sqrt(Math.pow((a.getX() - b.getX()), 2) + Math.pow((a.getY() - b.getX()), 2));
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

	public long[] getKeyDownTimestamp() {
		return main.getKeyDownTimestamp();
	}

	public long[] getKeyUpTimestamp() {
		return main.getKeyUpTimestamp();
	}

	public int getMouseOnscreenX() {
		return main.getMouseOnscreenX();
	}

	public int getMouseOnscreenY() {
		return main.getMouseOnscreenY();
	}

	private static int tfm(double x, float scale) {
		return (int) Math.round(scale * x);
	}

	public float getMapWidth() {
		return mapSize.getWidth();
	}

	public float getMapHeight() {
		return mapSize.getHeight();
	}


	@Override
	public void onScaleChange(float scale) {
		for (Robot r : robots) {
			r.onScaleChange(scale);
		}
	}

	public void playerPunch() {
		player.punchStart();//for drawing
		playerIsPunching = true;
	}

    public synchronized void remove(Entity e) {
        if (e instanceof Robot) {
            toRemove.add(((Robot) e));
        }
    }
}
