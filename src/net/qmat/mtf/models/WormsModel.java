package net.qmat.mtf.models;
import java.util.ArrayList;

import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class WormsModel extends BaseLayer {

	private GLGraphicsOffScreen offscreen;
	private ArrayList<Worm> worms;
	private static int NR_WORMS = 20;
	
	public WormsModel(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
		worms = new ArrayList<Worm>();
		for(int i=0; i<NR_WORMS; i++) {
			worms.add(new Worm());
		}
	}

	public void draw() {

		offscreen.beginDraw();
		
		for(Worm worm : worms) {
			drawWorm(worm);
		}
		
		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}

	
	public void drawWorm(Worm worm) {
		
		
	}
}
