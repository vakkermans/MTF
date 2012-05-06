package net.qmat.mtf.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;
import net.qmat.mtf.utils.Settings;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class Bar extends ProcessingObject {
	
	private int band;
	private float energy = 0f;

	public Bar(int band) {
		this.band = band;
	}

	public void draw() {
		energy *= 0.95f;
		p.stroke(255);
		float newEnergy = Controllers.getAnalysisController().getBarkBand(band);
		if(newEnergy > energy) {
			energy = newEnergy;
		}
		
		int blocks = (int)Main.map(energy, 0f, 0.1f, 0f, 100f);
		p.pushMatrix();
		for(int i=0; i<blocks; i++) {
			p.rect(0f, 40f, 10f, 10f);
			p.translate(13, 0);
		}
		p.popMatrix();
	}
}
