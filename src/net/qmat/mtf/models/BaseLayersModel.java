package net.qmat.mtf.models;

public class BaseLayersModel extends ProcessingObject {
	
	public LabVideo labVideo1;
	//public LabVideo labVideo2;
	private MasksModel masksModel;
	
	public BaseLayersModel(MasksModel masksModel) {
		labVideo1 = new LabVideo(vp1, masksModel.maskScreen1);
		//labVideo2 = new LabVideo(vp2, masksModel.maskScreen2);
	}

	public void draw() {
		labVideo1.draw();
		//labVideo2.draw();
	}
}
