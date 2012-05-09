package net.qmat.mtf.models;

public class MasksModel extends ProcessingObject {
	
	public BoxMask maskScreen1;
	public MoireMask maskScreen2;
	
	public MasksModel() {
		maskScreen1 = new BoxMask();
		maskScreen2 = new MoireMask();
	}

	public void draw() {
		maskScreen1.draw();
		maskScreen2.draw();
	}
}
