package net.qmat.mtf.models;

import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class BaseLayer extends ProcessingObject {

	protected GLGraphicsOffScreen vp;
	protected GLTexture canvas;
	protected CenterMask mask;

	private GLTexture finalTexture;
	private GLTextureFilter maskFilter, radialFilter;

	// textures for bloom/blur
	private static GLTexture bloomMask, bloomTexture, blurTexture;
	private static GLTexture tex0, tex2, tex4, tex8, tex16;
	private static GLTextureFilter extractBloom, blur, blend4, toneMap;

	private static boolean DO_BLOOM = false;

	public BaseLayer() {
		this(null, null);
	}

	public BaseLayer(GLGraphicsOffScreen vp, CenterMask mask) {
		this.vp = vp;
		this.mask = mask;
		canvas = new GLTexture(p, Main.screenWidth, Main.screenHeight);
		maskFilter = new GLTextureFilter(p, "Mask.xml");
		radialFilter = new GLTextureFilter(p, "Radialblur.xml");
		finalTexture = new GLTexture(p, Main.screenWidth, Main.screenHeight, GLTexture.FLOAT);

		// Initializing bloom mask and blur textures.
		extractBloom = new GLTextureFilter(p, "ExtractBloom.xml");
		blur = new GLTextureFilter(p, "Blur.xml");
		blend4 = new GLTextureFilter(p, "Blend4.xml");
		toneMap = new GLTextureFilter(p, "ToneMap.xml");
		int w = Main.screenWidth;
		int h = Main.screenHeight;

		finalTexture = new GLTexture(p, w, h, GLTexture.FLOAT);
		bloomTexture = new GLTexture(p, w, h, GLTexture.FLOAT);
		bloomMask = new GLTexture(p, w, h, GLTexture.FLOAT);
		tex0 = new GLTexture(p, w, h, GLTexture.FLOAT);
		tex2 = new GLTexture(p, w / 2, h / 2, GLTexture.FLOAT);
		tex4 = new GLTexture(p, w / 4, h / 4, GLTexture.FLOAT);
		tex8 = new GLTexture(p, w / 8, h / 8, GLTexture.FLOAT);
		tex16 = new GLTexture(p, w / 16, h / 16, GLTexture.FLOAT);
		blurTexture = new GLTexture(p, w, h, GLTexture.FLOAT);

		float[] res = new float[] {Main.screenWidth, Main.screenHeight};  
		radialFilter.setParameterValue("resolution", res);

	}

	protected void maskAndDraw() {
		float fx = 0.3f - 0.1f * Controllers.getAnalysisController().getBarkBandRelativeMultiple(15, 20);
		float fy = 0.6f - 0.2f * Controllers.getAnalysisController().getBarkBandRelativeMultiple(17, 25);

		GLTexture maskTexture = mask.vp.getTexture();
		GLTexture maskInput;

		if(DO_BLOOM) {
			// Extracting the bright regions from input texture.
			extractBloom.setParameterValue("bright_threshold", fx);
			extractBloom.apply(canvas, tex0);

			// Downsampling with blur.
			tex0.filter(blur, tex2);
			tex2.filter(blur, tex4);    
			tex4.filter(blur, tex8);    
			tex8.filter(blur, tex16);     

			// Blending downsampled textures.
			blend4.apply(new GLTexture[]{tex2, tex4, tex8, tex16}, new GLTexture[]{bloomMask});

			// Final tone mapping into destination texture.
			toneMap.setParameterValue("exposure", fy);
			toneMap.setParameterValue("bright", fx);
			toneMap.apply(new GLTexture[]{canvas, bloomMask}, new GLTexture[]{bloomTexture});
			maskInput = bloomTexture;
		} else {
			maskInput = canvas;
		}

		maskFilter.setParameterValue("mask_factor", 0.0f);
		maskFilter.apply(new GLTexture[]{maskInput, maskTexture}, blurTexture);
		/*
		float t = p.millis() / 1000.0f;
		radialFilter.setParameterValue("time", t);
		radialFilter.apply(blurTexture, finalTexture);
		 */

		vp.beginDraw();
		vp.image(blurTexture, 0, 0);
		//vp.image(finalTexture, 0, 0);
		vp.endDraw();
	}
}
