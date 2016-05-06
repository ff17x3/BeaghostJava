package main;

import util.ScaleChangeListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 05.05.2016
 */
public class Robot extends Entity implements ScaleChangeListener {
	// statische Winkel für Boxen-----------------------------------------------
	private static final float[] ANGLES; // Winkel für Ecken von Boxen
	public static final float RADIUS = 10;
	private static BufferedImage deadTexture;
	private static final float SIZE = 7 / 4 * RADIUS;
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

		// Textur
		try {
			deadTexture = ImageIO.read(new File("res/a.png"));
		} catch (IOException e) {
			e.printStackTrace();
			deadTexture = null;
		}
	}

	// -----------------------------------------------------------------------
	// Zeichnen: ++++++++
	private float[] angleSins = new float[8], angleCosins = new float[8];
	private Polygon poly = new Polygon();
	private final float distA = (float) (Math.sqrt(2) * RADIUS), distB = (float) (Math.sqrt(4.0625) * RADIUS); // TODO anpassen, wenn sich RADIUS ändert
	private float viewArcRadius = RADIUS * 5, view_arc = (float) Math.PI / 2;
	private boolean isDead = false;
	private Image deadTextureS;
	// ++++++++++++++++++

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

		onScaleChange(1f);
	}

	@Override
	public synchronized void draw(Graphics g, float scale) {
		if (!isDead) {
			// Blöcke links und rechts
			// TODO color
			g.setColor(Color.BLACK);
			// rechter Block
			poly.reset();
			poly.addPoint(Math.round((angleCosins[0] * distA + x) * scale), Math.round((angleSins[0] * distA + y) * scale));
			poly.addPoint(Math.round((angleCosins[1] * distB + x) * scale), Math.round((angleSins[1] * distB + y) * scale));
			poly.addPoint(Math.round((angleCosins[2] * distB + x) * scale), Math.round((angleSins[2] * distB + y) * scale));
			poly.addPoint(Math.round((angleCosins[3] * distA + x) * scale), Math.round((angleSins[3] * distA + y) * scale));
			g.fillPolygon(poly);
			// linker Block
			poly.reset();
			poly.addPoint(Math.round((angleCosins[4] * distA + x) * scale), Math.round((angleSins[4] * distA + y) * scale));
			poly.addPoint(Math.round((angleCosins[5] * distB + x) * scale), Math.round((angleSins[5] * distB + y) * scale));
			poly.addPoint(Math.round((angleCosins[6] * distB + x) * scale), Math.round((angleSins[6] * distB + y) * scale));
			poly.addPoint(Math.round((angleCosins[7] * distA + x) * scale), Math.round((angleSins[7] * distA + y) * scale));
			g.fillPolygon(poly);
			// view sector
			g.setColor(new Color(255, 255, 0, 200));
			((Graphics2D) g).fill(new Arc2D.Float((x - viewArcRadius) * scale, (y - viewArcRadius) * scale, 2 * viewArcRadius * scale, 2 * viewArcRadius * scale, -(float) Math.toDegrees((dir - view_arc / 2)), -(float) Math.toDegrees(view_arc), Arc2D.PIE));

			// Körper
			g.setColor(Color.BLUE);
			g.fillOval(Math.round((x - RADIUS) * scale), Math.round((y - RADIUS) * scale), Math.round(RADIUS * 2 * scale), Math.round(RADIUS * 2 * scale));
		} else {
			g.drawImage(deadTextureS, tfm(x, scale) - deadTextureS.getWidth(null) / 2, tfm(y, scale) - deadTextureS.getHeight(null) / 2, null);
		}
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

	public void die() {
		isDead = true;
	}

	@Override
	public void onScaleChange(float scale) {
		if (deadTexture != null) {
			deadTextureS = deadTexture.getScaledInstance(Math.round(RADIUS * 8 * scale), -1, Image.SCALE_FAST);
		}
	}
}
