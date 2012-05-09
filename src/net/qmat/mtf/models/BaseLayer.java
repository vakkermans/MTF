package net.qmat.mtf.models;

import net.qmat.mtf.Main;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class BaseLayer extends ProcessingObject {
	
	protected GLGraphicsOffScreen vp;
	protected GLTexture canvas;
	protected CenterMask mask;
	
	private GLTexture finalTexture;
	private GLTextureFilter maskFilter;
	
	public BaseLayer() {
		this(null, null);
	}
	
	public BaseLayer(GLGraphicsOffScreen vp, CenterMask mask) {
		this.vp = vp;
		this.mask = mask;
		canvas = new GLTexture(p, Main.screenWidth, Main.screenHeight);
		maskFilter = new GLTextureFilter(p, "Mask.xml");
		finalTexture = new GLTexture(p, Main.screenWidth, Main.screenHeight, GLTexture.FLOAT);
	}
	
	protected void maskAndDraw() {
		GLTexture maskTexture = mask.vp.getTexture();
		maskFilter.setParameterValue("mask_factor", 0.0f);
		maskFilter.apply(new GLTexture[]{canvas, maskTexture}, finalTexture);
		vp.beginDraw();
		vp.image(finalTexture, 0, 0);
		vp.endDraw();
	}
}
