package net.qmat.mtf.models;

import java.util.ArrayList;
import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;
import codeanticode.glgraphics.GLGraphicsOffScreen;

public class BoxMask extends CenterMask {
	
	private static int NR_BOXES = 10;
	
	public float aperture;
	public float restlessness;
	
	private ArrayList<Box> boxes;
	
	public BoxMask() {
		boxes = new ArrayList<Box>();
		for(int i=0; i<NR_BOXES; i++) {
			boxes.add(new Box(this));
		}
	}
	
	public void draw() {
		clearBackground();
		
		//aperture = (float)p.mouseX / Main.screenWidth;
		aperture = Controllers.getAnalysisController().getBarkBandRelative(5);
		//restlessness = (float)p.mouseY / Main.screenHeight;
		restlessness = Controllers.getAnalysisController().getBarkBandRelative(10);
		
		for(Box box : boxes) {
			box.draw();
		}
	}

}
