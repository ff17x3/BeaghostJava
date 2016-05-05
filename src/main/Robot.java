package main;

import java.awt.*;

/**
 * Created by Ma on 05.05.2016
 */
public class Robot {
    // ZEICHNEN +++++++++++++++++++++++++++++++++++++++++++++++

    // statische Winkel für Boxen-----------------------------------------------
    private static final float[] angles; // Winkel für Ecken von Boxen

    static {
        // angles init
        angles = new float[8];
        angles[0] = pi / 4;
        angles[1] = a;
        angles[2] = pi - a;
        angles[3] = 0.75f * pi;
        angles[4] = 1.25f * pi;
        angles[5] = pi + a;
        angles[6] = 2 * pi - a;
        angles[7] = 1.75f * pi;
    }

    // -----------------------------------------------------------------------
    private final float distA = (float) (Math.sqrt(2) * radius), distB = (float) (Math.sqrt(4.0625) * radius); // TODO anpassen, wenn sich radius ändert

    private Polygon poly = new Polygon(); //
    // Entfernungen zu den Ecken der Boxen zum Zeichnen
    private float[] angleSins = new float[8], angleCosins = new float[8];

    private boolean initOK = false;
    public SimpleRobot(float x, float y, float dir, GameManager gm, int bodyColor) {
        // colors
        bodyPaint.setColor(bodyColor); // body: 0xff990000 box: 0xff000000
    }

    @Override
    public void draw(Canvas c) {
        // Blöcke links und rechts
        // rechter Block
        drawPath.reset();
        drawPath.moveTo(angleCosins[0] * distA + x, angleSins[0] * distA + y);
        drawPath.lineTo(angleCosins[1] * distB + x, angleSins[1] * distB + y);
        drawPath.lineTo(angleCosins[2] * distB + x, angleSins[2] * distB + y);
        drawPath.lineTo(angleCosins[3] * distA + x, angleSins[3] * distA + y);
        drawPath.close();
        c.drawPath(drawPath, boxPaint);
        // linker Block
        drawPath.reset();
        drawPath.moveTo(angleCosins[4] * distA + x, angleSins[4] * distA + y);
        drawPath.lineTo(angleCosins[5] * distB + x, angleSins[5] * distB + y);
        drawPath.lineTo(angleCosins[6] * distB + x, angleSins[6] * distB + y);
        drawPath.lineTo(angleCosins[7] * distA + x, angleSins[7] * distA + y);
        drawPath.close();
        c.drawPath(drawPath, boxPaint);
        // sichtfeld
        drawViewField(fov / 2, fov, c);
        // Körper
        c.drawCircle(x, y, radius, bodyPaint);
    }



    @Override
    protected void changeDir(float change) {
        super.changeDir(change);
        // Werte für Boxen an den Seiten an Winkel anpassen
        for (int i = 0; i < angles.length; i++) {
            float actangle = getDir() + angles[i];
            actangle %= 2 * pi;
            if(!initOK){
                initOK = true;
                angleSins = new float[8];
                angleCosins = new float[8];}
            angleSins[i] = (float) Math.sin(actangle);
            angleCosins[i] = (float) Math.cos(actangle);
        }
    }
}
