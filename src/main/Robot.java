package main;

import java.awt.*;

/**
 * Created by Ma on 05.05.2016
 */
public class Robot extends Entity {
    // statische Winkel für Boxen-----------------------------------------------
    private static final float[] angles; // Winkel für Ecken von Boxen

    static {
        // angles init
        angles = new float[8];
        float a = 1.051650213f;
        angles[0] = (float) Math.PI / 4f;
        angles[1] = a;
        angles[2] = (float) Math.PI - a;
        angles[3] = 0.75f * (float) Math.PI;
        angles[4] = 1.25f * (float) Math.PI;
        angles[5] = (float) Math.PI + a;
        angles[6] = 2f * (float) Math.PI - a;
        angles[7] = 1.75f * (float) Math.PI;
    }
    // -----------------------------------------------------------------------
    // Zeichnen:++++++++
    private float[] angleSins = new float[8], angleCosins = new float[8];
    private Polygon poly = new Polygon();
    private float radius;
    private final float distA = (float) (Math.sqrt(2) * radius), distB = (float) (Math.sqrt(4.0625) * radius); // TODO anpassen, wenn sich radius ändert
    // ++++++++++++++

    public Robot(float x, float y, float dir, GameManager gm) {
        // TODO body color
        super(x, y, dir, gm);
        angleSins = new float[8];
        angleCosins = new float[8];
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

    @Override
    public void setDir(float dir) {
        super.setDir(dir);
        calcAngles();
    }

    @Override
    public void changeDir(float change) {
        super.changeDir(change);
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
