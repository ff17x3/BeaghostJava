package main;

import java.awt.*;

/**
 * Created by Max on 05.05.2016
 */
public class Robot extends Entity implements Entity.Tickable {
	// statische Winkel für Boxen-----------------------------------------------
	private static final float[] ANGLES; // Winkel für Ecken von Boxen
	private static final float RADIUS = 10;

	static {
		// ANGLES init
		ANGLES = new float[8];
		float a = 1.051650213f;
		ANGLES[0] = (float) Math.PI / 4f;
		ANGLES[1] = a;
		ANGLES[2] = (float) Math.PI - a;
		ANGLES[3] = 0.75f * (float) Math.PI;
		ANGLES[4] = 1.25f * (float) Math.PI;
		ANGLES[5] = (float) Math.PI + a;
		ANGLES[6] = 2f * (float) Math.PI - a;
		ANGLES[7] = 1.75f * (float) Math.PI;
	}

	// -----------------------------------------------------------------------
	// Zeichnen:++++++++
	private float[] angleSins = new float[8], angleCosins = new float[8];
	private Polygon poly = new Polygon();
	private final float distA = (float) (Math.sqrt(2) * RADIUS), distB = (float) (Math.sqrt(4.0625) * RADIUS); // TODO anpassen, wenn sich RADIUS ändert
	// ++++++++++++++

	public static Robot spawnRandom(float mapWidth, float mapHeight, GameManager gm) {
		float x = (float) (Math.random() * (mapWidth - 2 * RADIUS) + RADIUS);
		float y = (float) (Math.random() * (mapHeight - 2 * RADIUS) + RADIUS);
		float dir = (float) (Math.random() * (2 * Math.PI));
		return new Robot(x, y, dir, gm);
	}

	public Robot(float x, float y, float dir, GameManager gm) {
		// TODO body color
		super(x, y, dir, gm);
		angleSins = new float[8];
		angleCosins = new float[8];
		calcAngles();
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
		g.fillOval(Math.round((x - RADIUS) * scale), Math.round((y - RADIUS) * scale), Math.round(RADIUS * 2 * scale), Math.round(RADIUS * 2 * scale));
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
		for (int i = 0; i < ANGLES.length; i++) {
			float actangle = dir + ANGLES[i];
			actangle %= 2 * Math.PI;
			angleSins[i] = (float) Math.sin(actangle);
			angleCosins[i] = (float) Math.cos(actangle);
		}
	}

	@Override
	public void tick() {

	}
}
