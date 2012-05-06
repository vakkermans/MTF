package net.qmat.mtf.controllers;

import net.qmat.mtf.Main;

public class AnalysisController {

	// 
	private Float[] barkBands;
	
	public AnalysisController() {
		barkBands = new Float[25];
	}
	
	public void setAnalysis(Float[] data) {
		// TODO: split values
		barkBands = data;
	}
	
	public float getBarkBand(int n) {
		if(barkBands != null && n < barkBands.length)
			return barkBands[n];
		else
			return 0f;
	}
	
	public Float[] getBarkBands() {
		return barkBands;
	}
	
}
