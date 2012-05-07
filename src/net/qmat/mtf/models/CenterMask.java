package net.qmat.mtf.models;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class CenterMask extends ProcessingObject {
	
	GLGraphicsOffScreen vp;
	
	public CenterMask() {
		vp = null;
	}
	
	public CenterMask(GLGraphicsOffScreen vp) {
		this.vp = vp;
	}

}
