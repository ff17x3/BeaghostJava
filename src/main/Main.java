package main;

import util.DimensionF;
import util.DrawFrame;
import util.DrawInferface;
import util.FrameInitInterface;

import javax.swing.*;
import java.awt.*;

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

	public Main() {
		frame = new DrawFrame(frameSize, this, this, mapSize);
	}

	@Override
	public void draw(Graphics g, float scale) {

	}

	@Override
	public void initFrame(JFrame f, DrawFrame.DrawPanel dp) {

	}

	public DimensionF getMapSize() {
		return mapSize;
	}
}
