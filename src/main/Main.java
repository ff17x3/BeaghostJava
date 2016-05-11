package main;

import util.DimensionF;
import util.DrawFrame;
import util.FrameInitInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Florian on 05.05.2016. 16:48
 * test edit 2.1
 */
public class Main implements FrameInitInterface {
	public static void main(String[] args) {

//		System.out.println(Math.atan2(-10, 5));
		new Main();
	}

	private DrawFrame frame;
	private Dimension frameSize = new Dimension(800, 800);
	private DimensionF mapSize = new DimensionF(900, 500);
	private GameManager gm;
	private long[] keyDownTimestamp = new long[4];//w,a,s,d
	private long[] keyUpTimestamp = new long[4];//w,a,s,d
	private char[] keys = {'w', 'a', 's', 'd'};
	private int mouseOnscreenX, mouseOnscreenY;

	public Main() {
		gm = new GameManager(this, mapSize);
		frame = new DrawFrame(frameSize, this, gm, mapSize);
		frame.addScaleChangeListener(gm);

		Point mp = MouseInfo.getPointerInfo().getLocation();
		mouseOnscreenX = mp.x;
		mouseOnscreenY = mp.y;

		gm.startTicking();
	}


	@Override
	public void initFrame(JFrame f, DrawFrame.DrawPanel dp) {
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				if (!writeTimestamp(e, keyDownTimestamp)) {//only for wasd
					if (e.getKeyChar() == ' ') {
						gm.playerPunch();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				writeTimestamp(e, keyUpTimestamp);//only for wasd
			}
		});
		dp.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				mouseMove(e);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				super.mouseDragged(e);
				mouseMoved(e);
			}
		});
		dp.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				gm.playerPunch();
			}
		});
		f.setLocation(100, 100);
	}

	private synchronized void mouseMove(MouseEvent e) {
		mouseOnscreenX = e.getX();
		mouseOnscreenY = e.getY();
	}

	/**
	 * @param e
	 * @param keyTimestamp
	 * @return if press has been handled
	 */
	private synchronized boolean writeTimestamp(KeyEvent e, long[] keyTimestamp) {
		char key = e.getKeyChar();
		int i = 0;
		while (i < keys.length && key != keys[i]) {
			i++;
		}
		if (i != keys.length) {
			keyTimestamp[i] = System.nanoTime();
			return true;
		}
		return false;
	}

	public DrawFrame getFrame() {
		return frame;
	}

	public synchronized int getMouseOnscreenX() {
		return mouseOnscreenX;
	}

	public synchronized int getMouseOnscreenY() {
		return mouseOnscreenY;
	}

	public synchronized long[] getKeyDownTimestamp() {
		return keyDownTimestamp;
	}

	public synchronized long[] getKeyUpTimestamp() {
		return keyUpTimestamp;
	}
}
