package net.qmat.mtf.models;

import net.qmat.mtf.Main;
import codeanticode.glgraphics.GLGraphicsOffScreen;

public class CenterMask extends ProcessingObject {
	
	public GLGraphicsOffScreen vp;
	
	public CenterMask() {
		vp = new GLGraphicsOffScreen(p, Main.screenWidth, Main.screenHeight);
	}
	
	protected void clearBackground() {
		vp.beginDraw();
		vp.background(0);
		vp.endDraw();
	}

}
