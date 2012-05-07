package net.qmat.mtf.models;

import codeanticode.glgraphics.GLGraphicsOffScreen;
import net.qmat.mtf.Main;

public class Box extends ProcessingObject {

	float x = 0;
	float y = 0;
	float w = 0;
	float h = 0;

	float lastChange;
	float changeInterval = 1000;
	float maxChangeInterval = 2000;
	float smallChangeRange = 30;
	
	BoxMask boxMask;

	Box(BoxMask boxMask) {
		this.boxMask = boxMask;
		lastChange = p.millis();
	}

	public void draw() {

		float now = p.millis();
		if(now - lastChange > changeInterval) {
			doBigChange(now);
		}

		GLGraphicsOffScreen vp = boxMask.vp;
		vp.beginDraw();
		vp.noStroke();
		vp.fill(255, 255, 255, 200);
		vp.rectMode(Main.CENTER);
		vp.pushMatrix();
		vp.translate(Main.screenWidth/2, Main.screenHeight/2);
		vp.rect(x, y, w + getSmallChange(), h + getSmallChange());
		vp.popMatrix();
		vp.endDraw();
	}

	float getSmallChange() {
		return p.random(smallChangeRange*boxMask.restlessness) - 0.5f * smallChangeRange * boxMask.restlessness;
	}

	float getPositionIndex() {
		float n = Main.pow(p.random(0.5f), 1.0f + (1.0f - boxMask.aperture) * 5f);
		if(p.random(1f) > 0.5f)
			return n;
		else
			return -n;
	}

	void doBigChange(float now) {
		float power = 1f + (1f - boxMask.aperture) * 4f;
		w = Main.pow(p.random(0.8f), power) * Main.screenWidth;
		h = Main.pow(p.random(0.8f), power) * Main.screenHeight;
		float ratio = Main.pow((1f - (w*h) / (Main.screenWidth*Main.screenHeight)), 5f);
		changeInterval = ratio * maxChangeInterval;   
		lastChange = now;

		x = getPositionIndex() * Main.screenWidth;
		y = getPositionIndex() * Main.screenHeight;   
	}

}
