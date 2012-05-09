package net.qmat.mtf.models;

import net.qmat.mtf.Main;

public class MoireMask extends CenterMask {
	float aperture = 0.1f;
	float restlessness = 0.1f;
	float dia = 200f;

	public void draw() {
		aperture = (float)p.mouseX / Main.screenWidth;
		restlessness = (float)p.mouseY / Main.screenHeight;

		vp.beginDraw();
		vp.background(0);
		vp.stroke(255, 255, 255, 180);

		drawAllCircles();
		drawLines();

		vp.endDraw();
	}

	void drawLines() {  
		vp.pushMatrix();
		vp.translate(Main.screenWidth/2, Main.screenHeight/2);
		drawLinesPartly(1);
		drawLinesPartly(-1);
		vp.popMatrix(); 
	}

	void drawLinesPartly(int mp) {
		float lineWeight = 5;
		float spacing = 5;
		float nrLines = aperture * (Main.screenHeight/2) / (lineWeight + spacing) / 3;
		vp.strokeWeight(lineWeight);
		vp.pushMatrix();
		for(int i=0; i<nrLines; i++) {
			float lineIndex = i/nrLines;
			if(p.random(1)*0.8 > lineIndex*restlessness) {
				float hw = aperture * Main.screenWidth/4 + 300 + Main.sin(-lineIndex * Main.PI/2) * 240;
				float hy = -i*(lineWeight+spacing) * mp;
				vp.line(-hw, hy, hw, hy);
			}
		}
		vp.popMatrix();
	}

	void drawAllCircles() {
		float lineLength = aperture * Main.screenWidth/4;
		float lineWeight = 3;
		float[][] positions = new float[][] {{aperture * 50, 0}, 
											 {aperture * -50, 0},
											 {0, aperture * -50},
											 {0, aperture * 50}};
		for(int i=0; i<positions.length; i++) {
			vp.pushMatrix();
			vp.translate(positions[i][0], positions[i][1]);
			drawCircles(lineWeight, 5, lineLength);
			vp.popMatrix();
		}
	}

	void drawCircles(float lineWeight, float spacing, float lineLength) {
		float nrLines = lineLength * 0.7f;
		vp.strokeWeight(lineWeight);
		vp.pushMatrix();
		vp.translate(Main.screenWidth/2+(p.random(restlessness)-0.5f*restlessness)*10, Main.screenHeight/2);
		for(int i=0; i<nrLines; i++) {
			vp.line(0, 0, lineLength-0.05f*lineLength*p.random(restlessness), 0);
			vp.rotate(Main.TWO_PI/nrLines);
		}
		vp.popMatrix();
	}

}
