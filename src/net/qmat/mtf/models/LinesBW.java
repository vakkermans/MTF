package net.qmat.mtf.models;
import java.util.ArrayList;

import net.qmat.mtf.Main;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class LinesBW extends BaseLayer {

	private GLGraphicsOffScreen offscreen;
	private ArrayList<Worm> worms;
	private static int NR_WORMS = 25;

	public LinesBW(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		offscreen = new GLGraphicsOffScreen(Main.p, Main.screenWidth, Main.screenHeight);
		worms = new ArrayList<Worm>();
		for(int i=0; i<NR_WORMS; i++) {
			worms.add(new Worm());
		}
		
	}

	public void draw() {

		offscreen.beginDraw();
		offscreen.background(0);
		for(Worm worm : worms) {
			worm.draw();
		}
		offscreen.endDraw();
		canvas = offscreen.getTexture();

		maskAndDraw();
	}

	class Worm {

		int randomX;
		int randomY;

		int circleSize;
		int circledegree;

		float [] xpos;
		float [] ypos;


		float x1, y1, rad1;
		int deg=0;


		public Worm (){

			xpos = new float[9];
			ypos = new float[9];

			randomX = (int) p.random(11, Main.screenWidth-10);
			randomY = (int) p.random(11, Main.screenHeight-10);

			circleSize = (int) p.random(30,200);
			circledegree = (int) p.random(100,180);

			for (int i = 0; i < xpos.length; i++) {
				xpos[i] = 50;
				ypos[i] =0; 

			}

		}



		void draw() {
			offscreen.noStroke();



			//shift array values
			for (int i=0; i < xpos.length-1; i++) {
				xpos[i] = xpos [i+1];
				ypos[i] = ypos [i+1];

			}

			//new location
			deg=deg+5;
			rad1= (Main.PI/circledegree)*deg;
			xpos [xpos.length -1] = Main.sin(rad1)*circleSize+(randomX);
			ypos [ypos.length -1]= Main.cos(rad1)*circleSize+(randomY);

			for (int i =0; i <xpos.length;i++) {
				offscreen.strokeWeight(2);

				offscreen.stroke(255);
				offscreen.fill(255,150);
				offscreen.ellipse(xpos[i], ypos[i], 2,2);
				offscreen.strokeWeight(2);
				offscreen.line(Main.screenWidth/2+xpos[i]/10, Main.screenHeight/2+xpos[i]/10,xpos[i], ypos[i]);
			}


		}


	}


}
