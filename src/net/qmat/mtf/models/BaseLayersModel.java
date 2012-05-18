package net.qmat.mtf.models;

public class BaseLayersModel extends ProcessingObject {
	
	public LabVideo labVideo1;
	//public LabVideo labVideo2;
	//public ColouredBubbles bubbles;
	//public AcidBubbles bubbles;
	//public RandomBlocks blocks;
	//public LinesBW linesBW;
	//public BoidVoid boidVoid;
	
	public BaseLayersModel(MasksModel masksModel) {
		//bubbles = new AcidBubbles(vp1, masksModel.maskScreen1);
		//bubbles = new ColouredBubbles(vp1, masksModel.maskScreen1);
		labVideo1 = new LabVideo(vp1, masksModel.maskScreen1);
		//labVideo2 = new LabVideo(vp2, masksModel.maskScreen2);
		//blocks = new RandomBlocks(vp1, masksModel.maskScreen1);  
		//linesBW = new LinesBW(vp1, masksModel.maskScreen1);
		//boidVoid = new BoidVoid(vp1, masksModel.maskScreen1);
	}

	public void draw() {
		//bubbles.draw();
		labVideo1.draw();
		//labVideo2.draw();
		//blocks.draw();
		//linesBW.draw();
		//boidVoid.draw();
	}
}
