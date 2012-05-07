package net.qmat.mtf.models;


import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;
import net.qmat.mtf.utils.Settings;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class MasksModel extends ProcessingObject {
	
	public BoxMask maskScreen1;
	public MoireMask maskScreen2;
	
	public MasksModel() {
		maskScreen1 = new BoxMask(vp1);
		maskScreen2 = new MoireMask(vp2);
	}

	public void draw() {
		maskScreen1.draw();
		maskScreen2.draw();
	}
}
