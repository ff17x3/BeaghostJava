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
    private int counter = 0;
    //    private long timeOld = System.nanoTime(), time;
    private int inteval = 60;
    private long[] times = new long[inteval];
    private float avg = 0;
    private int avgcount = 0;

    public GameManager(Main main, DimensionF mapSize) {
        this.main = main;
        this.mapSize = mapSize;

        //fills the ArrayList robots
        spawnRobots(robotCount);
        player = new Player(200, 200, 0, this);
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
        tickClock = new ClockNano(FPS, millisDelta -> {
            for (Robot r : robots) {
                r.tick();
            }
            player.tick();
        });

    }

    private void printTimes() {
        new Thread() {
            public void run() {
                for (int i = 1; i < times.length; i++) {
                    float t = (float) ((times[i] - times[i - 1]) * 1e-6);
                    avg = (avg * avgcount + t) / (avgcount + 1);
                    avgcount++;
                    if (t > 1.2 * avg)
                        System.out.println("FRAMEDROP: " + t + "ms needed (avg: " + avg + ")");

                }

            }
        }.start();
    }

    private void spawnRobots(int robotCount) {
        float mapWidth = mapSize.getWidth();
        float mapHeight = mapSize.getHeight();
        float RADIUS = Robot.RADIUS;

        float x, y, dir;
        for (int i = 0; i < robotCount; i++) {
            do {
                x = (float) (Math.random() * (mapWidth - 2 * RADIUS) + RADIUS);
                y = (float) (Math.random() * (mapHeight - 2 * RADIUS) + RADIUS);
            } while (!isFree(x, y, RADIUS));
            dir = (float) (Math.random() * (2 * Math.PI));
            robots.add(new Robot(x, y, dir, this));
        }
    }

    private boolean isFree(float x, float y, float radius) {
        for (Robot r : robots) {
            if (Math.sqrt(Math.pow((r.x - x), 2) + Math.pow((r.y - y), 2)) <= 2 * radius)
                return false;
        }
        return true;
    }

    private void spawnRobots() {

    }

    @Override
    public void draw(Graphics g1, float s) {
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


}
