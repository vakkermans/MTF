package net.qmat.mtf.models;

public class MasksModel extends ProcessingObject {
	
	//public BoxMask maskScreen1;
	public MoireMask maskScreen1;
	//public BoxMask maskScreen2;
	
	public MasksModel() {
		//maskScreen1 = new BoxMask();
		maskScreen1 = new MoireMask();
		//maskScreen2 = new BoxMask();
	}

	public void draw() {
		maskScreen1.draw();
		//maskScreen2.draw();
	}
}
