package net.qmat.mtf.models;
import codeanticode.gsvideo.*;

import codeanticode.glgraphics.GLGraphicsOffScreen;

public class LabVideo extends BaseLayer {

	private GSMovie movie;

	public LabVideo(GLGraphicsOffScreen vp, CenterMask mask) {
		super(vp, mask);
		movie = new GSMovie(p, "movies/lab_video.mp4");
		//movie = new GSMovie(p, "movies/pnr.mp4");
		movie.setPixelDest(canvas);  
		movie.loop();
	}

	public void draw() {
		canvas.putPixelsIntoTexture();
		maskAndDraw();
	}

}
