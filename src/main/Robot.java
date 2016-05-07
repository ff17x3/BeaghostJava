package main;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by Max on 05.05.2016
 */
public class Robot extends Entity implements Entity.Tickable {
	// statische Winkel für Boxen-----------------------------------------------
	private static final float[] ANGLES; // Winkel für Ecken von Boxen
	private static final float RADIUS = 10;
	private static BufferedImage deadTexture;
	private static final long bloodVisibilityTime = 4000;
	public static final float SIZE_RAD = 7f / 4 * RADIUS;
	private static final float
			distA = (float) (Math.sqrt(2) * RADIUS),
			distB = (float) (Math.sqrt(4.0625) * RADIUS);

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
	private float[] angleSins = new float[8], angleCosins = new float[8];
	private Polygon poly = new Polygon();
	private float viewArcRadius = RADIUS * 5, view_arc = (float) Math.PI / 2;
	private boolean isDead = false;
	private Image deadTextureS;
	private float textureAlpha = 0f;
	private long dieMillis;



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
		g.setColor(Color.BLACK);
		if (!isDead) {
			// Blöcke links und rechts
			// TODO color
			g.setColor(Color.BLACK);
			// rechter Block
			poly.reset();
			poly.addPoint(tfm(angleCosins[0] * distA + x), tfm(angleSins[0] * distA + y));
			poly.addPoint(tfm(angleCosins[1] * distB + x), tfm(angleSins[1] * distB + y));
			poly.addPoint(tfm(angleCosins[2] * distB + x), tfm(angleSins[2] * distB + y));
			poly.addPoint(tfm(angleCosins[3] * distA + x), tfm(angleSins[3] * distA + y));
			g.fillPolygon(poly);
			// linker Block
			poly.reset();
			poly.addPoint(tfm(angleCosins[4] * distA + x), tfm(angleSins[4] * distA + y));
			poly.addPoint(tfm(angleCosins[5] * distB + x), tfm(angleSins[5] * distB + y));
			poly.addPoint(tfm(angleCosins[6] * distB + x), tfm(angleSins[6] * distB + y));
			poly.addPoint(tfm(angleCosins[7] * distA + x), tfm(angleSins[7] * distA + y));
			g.fillPolygon(poly);
			// view sector
			g.setColor(new Color(255, 255, 0, 100));
			((Graphics2D) g).fill(new Arc2D.Float((x - viewArcRadius) * scale, (y - viewArcRadius) * scale, 2 * viewArcRadius * scale, 2 * viewArcRadius * scale, -(float) Math.toDegrees((dir - view_arc / 2)), -(float) Math.toDegrees(view_arc), Arc2D.PIE));

			// Körper
			g.setColor(Color.BLUE);
			g.fillOval(Math.round((x - RADIUS) * scale), Math.round((y - RADIUS) * scale), Math.round(RADIUS * 2 * scale), Math.round(RADIUS * 2 * scale));
		} else {
			((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, textureAlpha));
			g.drawImage(deadTextureS, tfm(x) - deadTextureS.getWidth(null) / 2, tfm(y) - deadTextureS.getHeight(null) / 2, null);
			((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
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
		if (!isDead) {
			isDead = true;
			dieMillis = System.currentTimeMillis();
		}
	}

	@Override
	public void tick() {
		if (isDead) {
			textureAlpha = 1 - ((System.currentTimeMillis() - dieMillis) / (float) bloodVisibilityTime);
			if (textureAlpha <= 0f) {
				textureAlpha = 0f;
				gm.remove(this);
			}
		}
	}

	@Override
	public void onScaleChange(float scale) {
		super.onScaleChange(scale);
		if (deadTexture != null) {
			deadTextureS = deadTexture.getScaledInstance(Math.round(RADIUS * 8 * scale), -1, Image.SCALE_FAST);
		}
	}

	public void setDrawViewField(float range) {
		viewArcRadius = range;
	}
}
