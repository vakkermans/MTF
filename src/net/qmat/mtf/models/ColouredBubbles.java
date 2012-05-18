package net.qmat.mtf.models;
import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class ColouredBubbles extends BaseLayer {

	private GLGraphicsOffScreen offscreen;
	
	public ColouredBubbles(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
	}

	public void draw() {

		offscreen.beginDraw();
		if(p.random(1) < 0.1) {
			for (int p=0; p<10; p=p+40) {
				int range = Main.screenWidth/2;
				offscreen.translate(Main.p.random(-range, range), Main.p.random(-range, range));
				for (int i=0; i<100; i=i+20) {
					offscreen.noStroke();
					offscreen.stroke(100, 200, 180, i*2);
					offscreen.fill(Main.p.random(1, 255), Main.p.random(1, 255), Main.p.random(1, 255), i*2);
					offscreen.ellipse(Main.screenWidth/2, Main.screenHeight/2, i, i);
				}
			}
		}
		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}

}
