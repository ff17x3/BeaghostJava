package main;

/**
 * Created by Ma on 05.05.2016
 */
public class Robot {
    private float radius;
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

    public Robot() {
        angleSins = new float[8];
        angleCosins = new float[8];
    }
}
