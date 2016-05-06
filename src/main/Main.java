package main;

import util.DimensionF;
import util.DrawFrame;
import util.FrameInitInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Florian on 05.05.2016. 16:48
 * test edit 2.1
 */
public class Main implements FrameInitInterface {
	public static void main(String[] args) {
		new Main();
	}

	private DrawFrame frame;
	private Dimension frameSize = new Dimension(500, 500);
	private DimensionF mapSize = new DimensionF(500, 500);
	private GameManager gm;
	private long[] keyDownTimestamp = new long[4];//w,a,s,d
	private long[] keyUpTimestamp = new long[4];//w,a,s,d
	private char[] keys = {'w', 'a', 's', 'd'};

	public Main() {
		gm = new GameManager(this, mapSize);
		frame = new DrawFrame(frameSize, this, gm, mapSize);
		gm.startTicking();
	}


	@Override
	public void initFrame(JFrame f, DrawFrame.DrawPanel dp) {
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				writeTimestamp(e, keyDownTimestamp);
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				writeTimestamp(e, keyUpTimestamp);
			}
		});
	}

	private void writeTimestamp(KeyEvent e, long[] keyTimestamp) {
		char key = e.getKeyChar();
		int i = 0;
		while (i < keys.length && key != keys[i]) {
			i++;
		}
		if (i != keys.length) {
			keyTimestamp[i] = System.nanoTime();
		}
	}

	public DrawFrame getFrame() {
		return frame;
	}

	public long[] getKeyDownTimestamp() {
		return keyDownTimestamp;
	}

	public long[] getKeyUpTimestamp() {
		return keyUpTimestamp;
	}
}
