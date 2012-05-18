package net.qmat.mtf.controllers;

import net.qmat.mtf.Main;

public class AnalysisController {

	private Float[] barkBands;
	private Float[] barkBandsMax;
	private Float[] barkBandsFade;
	private Float fadeFactor = 0.98f;
	
	public AnalysisController() {
		barkBands = new Float[25];
		barkBandsMax = new Float[25];
		barkBandsFade = new Float[25];
		for(int i=0; i<barkBandsMax.length; i++) {
			barkBands[i] = 0f; 
			barkBandsFade[i] = 0f; 
			barkBandsMax[i] = 0f;
		}
	}
	
	public void setAnalysis(Float[] data) {
		// TODO: split values
		barkBands = data;
		boolean maxChanged = false;
		for(int i=0; i<barkBands.length; i++) {
			if(barkBands[i] > barkBandsMax[i]) {
				barkBandsMax[i] = barkBands[i];
				maxChanged = true;
			}
		}
		if(maxChanged) {
			for(int i=0; i<barkBandsMax.length; i++) {
				System.out.print(barkBandsMax[i] + "\t");
			}
			System.out.println();
		}
			
	}
	
	public float getBarkBand(int n) {
		if(barkBands != null && n < barkBands.length) {
			if(barkBands[n] > barkBandsFade[n]) {
				barkBandsFade[n] = barkBands[n];
				return barkBands[n];
			} else {
				barkBandsFade[n] = barkBandsFade[n] * fadeFactor;
				return barkBandsFade[n];
			}	
		} else
			return 0f;
	}
	
	public float getBarkBandRelative(int n) {
		if(barkBands != null && n < barkBands.length)
			return getBarkBand(n) / barkBandsMax[n];
		else
			return 0f;
	}
	
	// N.B. end is not included
	public float getBarkBandRelativeMultiple(int start, int end) {
		if(barkBands != null && start >= 0 && start < end && start < barkBands.length && end <= barkBands.length) {
			float sum = 0f;
			for(int i=start; i<end; i++) 
				sum += getBarkBandRelative(i);
			return sum/(float)(end-start);
		} else
			return 0f;
	}
	
	public Float[] getBarkBands() {
		return barkBands;
	}
	
}
