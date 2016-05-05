package main;

import util.DimensionF;
import util.DrawFrame;
import util.DrawInferface;
import util.FrameInitInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Created by Florian on 05.05.2016. 16:48
 */
public class Main implements DrawInferface, FrameInitInterface {
	public static void main(String[] args) {
		new Main();
	}

	private DrawFrame frame;
	private Dimension frameSize = new Dimension(500, 500);
	private DimensionF mapSize = new DimensionF(500, 500);
	private GameManager gm;
	private long[] keyDownTimestamp = new long[4];//w,a,s,d
	private char[] keys = {'w', 'a', 's', 'd'};

	public Main() {

		frame = new DrawFrame(frameSize, this, this, mapSize);
		gm = new GameManager(this);
		gm.startTicking();
	}

	@Override
	public void draw(Graphics g, float scale) {

	}

	@Override
	public void initFrame(JFrame f, DrawFrame.DrawPanel dp) {
		f.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyPressed(e);
				char key = e.getKeyChar();
				int i = 0;
				while (key != keys[i] && i < keys.length;) {
					i++;
				}
				if(key != keys.length){
					time
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
			}
		});
	}

	public DrawFrame getFrame() {
		return frame;
	}

	public DimensionF getMapSize() {
		return mapSize;
	}
}
