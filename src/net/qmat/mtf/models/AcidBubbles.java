package net.qmat.mtf.models;
import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class AcidBubbles extends BaseLayer {

	private GLGraphicsOffScreen offscreen;

	int [] xpos = new int[50];
	int [] ypos = new int[50];

	float x1,y1, rad1;
	int deg= 0;

	public AcidBubbles(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
	}

	public void draw() {

		offscreen.beginDraw();

		//shift array values
		for (int i=0; i < xpos.length-1; i++){
			xpos[i] = xpos [i+1];
			ypos[i] = ypos [i+1];

		}

		//new location
		xpos [xpos.length -1] = (int)Main.p.random(0, Main.screenWidth);
		ypos [ypos.length -1]= (int)(Main.p.random(0, Main.screenHeight));

		//draw the snake

		for (int i =0; i <xpos.length;i++) {
			offscreen.ellipse(xpos[i], ypos[i], i, i);
		}

		offscreen.strokeWeight(Main.abs(50* Main.pow(Main.sin(Main.frameCount/30f),5)));
		offscreen.fill(Main.p.random(1,255), Main.p.random(1,255), Main.p.random(1,255),50);
		offscreen.stroke(Main.p.random(1,255), Main.p.random(1,255), Main.p.random(1,255),50);

		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}

}
