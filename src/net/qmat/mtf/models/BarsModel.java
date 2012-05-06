package net.qmat.mtf.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.qmat.mtf.Main;
import net.qmat.mtf.controllers.Controllers;
import net.qmat.mtf.utils.Settings;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class BarsModel extends ProcessingObject {
	
	private ArrayList<Bar> bars;
	private static int NR_BANDS = 24;

	public BarsModel() {
		bars = new ArrayList<Bar>();
		for(int i=0; i<NR_BANDS; i++) {
			bars.add(new Bar(i));
		}
	}

	public void draw() {
		p.pushMatrix();
		for(int i=0; i<NR_BANDS; i++) {
			p.translate(0, 13);
			bars.get(i).draw();
		}
		p.popMatrix();
	}
}
