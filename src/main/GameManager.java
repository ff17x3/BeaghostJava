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
        player = new Player(200, 200, 0, this);
        System.out.println("spawend " + robotCount + " Robots");
        drawClock = new ClockNano(FPS, millisDelta -> main.getFrame().redraw());
        tickClock = new ClockNano(FPS, millisDelta -> {
            for (Robot r : robots) {
                r.tick();
            }
            player.tick();
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
