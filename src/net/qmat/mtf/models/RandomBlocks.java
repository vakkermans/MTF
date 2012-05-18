package net.qmat.mtf.models;
import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class RandomBlocks extends BaseLayer {




	private GLGraphicsOffScreen offscreen;

	int [] xpos = new int[50];
	int [] ypos = new int[50];

	float x1,y1, rad1;
	int deg= 0;

	public RandomBlocks(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
	}

	public void draw() {

		offscreen.beginDraw();
		
		offscreen.strokeWeight(Main.abs(50* Main.pow(Main.sin(Main.frameCount/30f),5)));
		offscreen.stroke(Main.p.random(1,255), Main.p.random(1,255), Main.p.random(1,255), 50);
		offscreen.fill(Main.p.random(1,255), Main.p.random(1,255), Main.p.random(1,255), 50);
		offscreen.rect(Main.p.random(0, Main.screenWidth), Main.p.random(0, Main.screenHeight), Main.p.random(10,155), Main.p.random(10,155));

		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}

}
