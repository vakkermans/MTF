package net.qmat.mtf.models;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;

public class MoireMask extends CenterMask {
	float aperture = 0.1f;
	float restlessness = 0.1f;
	float dia = 300f;

	public void draw() {
		//aperture = (float)p.mouseX / Main.screenWidth;
		aperture = Controllers.getAnalysisController().getBarkBandRelativeMultiple(0, 4);
		//restlessness = (float)p.mouseY / Main.screenHeight;
		restlessness = Controllers.getAnalysisController().getBarkBandRelative(10);

		vp.beginDraw();
		vp.background(0);
		vp.stroke(255, 255, 255, 235);

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
		float lineWeight = 1;
		float[][] positions = new float[][] {{aperture * 100, 0}, 
				{aperture * -100, 0},
				{0, aperture * -100},
				{0, aperture * 100}};
		for(int i=0; i<positions.length; i++) {
			drawCircles(lineWeight, 8, lineLength, positions[i][0], positions[i][1]);
		}
	}

	void drawCircles(float lineWeight, float spacing, float lineLength, float extraX, float extraY) {
		float nrLines = lineLength * 0.7f + 10;
		
		vp.strokeWeight(lineWeight);
		vp.pushMatrix();
		vp.translate(extraX + Main.screenWidth/2+(p.random(restlessness)-0.5f*restlessness)*10, Main.screenHeight/2 + extraY);
		float nrCircles = 2*lineLength / (float)(lineWeight+spacing) + 2;
		vp.noFill();
		for(int i=0; i<(int)nrCircles; i++) {
		    vp.ellipse(0, 0, (lineWeight+spacing)*i, (lineWeight+spacing)*i);
		}
		vp.popMatrix();

		FloatBuffer vbuffer, vbuffer2;
		FloatBuffer cbuffer, cbuffer2;

		vp.beginGL();
		GL gl = vp.beginGL();

		vbuffer = BufferUtil.newFloatBuffer(((int)nrLines)* 2 * 2);
		cbuffer = BufferUtil.newFloatBuffer(((int)nrLines)* 2 * 3);
		
		for (int i = 0; i < (int)nrLines; i++) {
			float ll = lineLength; //-0.05f*lineLength*p.random(restlessness);
			float rot = (Main.TWO_PI/(int)nrLines) * i * 2;
			//float centerX = Main.screenWidth/2+(p.random(restlessness)-0.5f*restlessness)*10;
			vbuffer.put(0);
			vbuffer.put(0);
			float x = Main.cos(rot) * ll;// + lineLength-0.05f*lineLength*p.random(restlessness);
			float y = Main.sin(rot) * ll; 
			vbuffer.put(x);
			vbuffer.put(y);
			// random r,g,b
			cbuffer.put(1.0f);
			cbuffer.put(1.0f);
			cbuffer.put(1.0f);
			cbuffer.put(1.0f);
			cbuffer.put(1.0f);
			cbuffer.put(1.0f);
		}
		vbuffer.rewind();
		cbuffer.rewind();
		
		// start drawing
		gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
		gl.glVertexPointer(2, GL.GL_FLOAT, 0, vbuffer);

		gl.glEnableClientState(GL.GL_COLOR_ARRAY);
		gl.glColorPointer(3, GL.GL_FLOAT, 0, cbuffer);
		gl.glLineWidth(lineWeight);
		
		gl.glPushMatrix();
		gl.glScalef(2, 2, 1);
		gl.glTranslatef(Main.screenWidth/2 + extraX, Main.screenHeight/2 + extraY, 0);
		gl.glRotatef(Main.TWO_PI * Controllers.getAnalysisController().getBarkBandRelativeMultiple(20, 25), 1, 0, 0);
		
		if((int)nrLines > 2)
			gl.glDrawArrays(GL.GL_LINE_STRIP, 0, (int)nrLines);
		
		gl.glPopMatrix();

		vp.endGL();
	}

}
