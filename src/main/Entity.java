package main;

import util.DrawInferface;

import java.awt.*;

/**
 * Created by Florian on 05.05.2016
 */
public class Entity implements DrawInferface {
    private float x, y, dir;

    // ZEICHNEN +++++++++++++++++++++++++++++++++++++++++++++++
    private final float distA = (float) (Math.sqrt(2) * radius), distB = (float) (Math.sqrt(4.0625) * radius); // TODO anpassen, wenn sich radius ändert

    private Polygon poly = new Polygon();
    // Entfernungen zu den Ecken der Boxen zum Zeichnen
    private float[] angleSins = new float[8], angleCosins = new float[8];

    private GameManager gm;

    public Entity(float x, float y, float dir, GameManager gm, int bodyColor) {
        // colors.. body: 0xff990000 box: 0xff000000
        this.x = x;
        this.y = y;
        this.gm = gm;
        this.dir = dir;
    }

    @Override
    public void draw(Graphics g, float scale) {
        // Blöcke links und rechts
        // rechter Block
        poly.reset();
        poly.addPoint(Math.round(angleCosins[0] * distA * scale + x * scale), Math.round(angleSins[0] * distA * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[1] * distB * scale + x * scale), Math.round(angleSins[1] * distB * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[2] * distB * scale + x * scale), Math.round(angleSins[2] * distB * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[3] * distA * scale + x * scale), Math.round(angleSins[3] * distA * scale + y * scale));
        g.fillPolygon(poly);
        // linker Block
        poly.reset();
        poly.addPoint(Math.round(angleCosins[4] * distA * scale + x * scale), Math.round(angleSins[4] * distA * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[5] * distB * scale + x * scale), Math.round(angleSins[5] * distB * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[6] * distB * scale + x * scale), Math.round(angleSins[6] * distB * scale + y * scale));
        poly.addPoint(Math.round(angleCosins[7] * distA * scale + x * scale), Math.round(angleSins[7] * distA * scale + y * scale));
        g.fillPolygon(poly);
        // sichtfeld TODO
        // Körper
        g.fillOval(Math.round(x - radius), Math.round(y - radius), Math.round(radius * 2), Math.round(radius * 2));
    }


    public void changeDir(float change) {
        dir += change;
        dir %= 2 * Math.PI;
        calcAngles();
    }

    public void setDir(float dir) {
        this.dir = dir;
        calcAngles();
    }

    private void calcAngles() {
        // Werte für Boxen an den Seiten an Winkel anpassen
        for (int i = 0; i < angles.length; i++) {
            float actangle = dir + angles[i];
            actangle %= 2 * Math.PI;
            angleSins[i] = (float) Math.sin(actangle);
            angleCosins[i] = (float) Math.cos(actangle);
        }
    }
}
