package net.qmat.mtf.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import net.qmat.mtf.Main;
import net.qmat.mtf.utils.Settings;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

public class MoonModel extends ProcessingObject {

	// textures for crescent moon
	private GLGraphicsOffScreen canvas;
	private GLTexture maskedCanvasTexture;
	private GLTexture moon;
	private GLGraphicsOffScreen moonMask;
	private GLTextureFilter maskFilter;

	// textures for bloom/blur
	private static GLTexture srcTex, finalTexture, bloomMask;
	private static GLTexture tex0, tex2, tex4, tex8, tex16;
	private static GLTextureFilter extractBloom, blur, blend4, toneMap;

	// hacking counter
	private static int HACKING_HOURS = 18;
	private static float diameter = 400.0f;
	private GregorianCalendar start;
	private GregorianCalendar deadline;
	private float hackingMs;
	
	// effect parameters
	public float fx = 0.4f;
	public float fy = 0.25f;



	public MoonModel() {
		
		// time stuff for hacking countdown
		if(Main.debug) {
			int debugMinutes = 1;
			start = new GregorianCalendar();
			deadline = (GregorianCalendar)start.clone();
			deadline.add(Calendar.MINUTE, debugMinutes);
			hackingMs = debugMinutes * 60 * 1000;
		} else {
			deadline = new GregorianCalendar(2012, Calendar.MAY, 19);
			deadline.set(Calendar.HOUR_OF_DAY, 16);
			start = (GregorianCalendar)deadline.clone();
			start.add(Calendar.HOUR_OF_DAY, -HACKING_HOURS);
			hackingMs = HACKING_HOURS * 60f * 60f * 1000f;
		}

		printStartAndDeadline();
	}

	private void initGL() {
		int canvasSize = (int)(diameter*1.2);
		
		canvas = new GLGraphicsOffScreen(p, canvasSize, canvasSize);
		moon = new GLTexture(p, "images/moon1.jpg");
		moonMask = new GLGraphicsOffScreen(p, canvasSize, canvasSize);
		maskedCanvasTexture = new GLTexture(p, canvasSize, canvasSize);
		maskFilter = new GLTextureFilter(p, "Mask.xml");

		// Initializing bloom mask and blur textures.
		extractBloom = new GLTextureFilter(p, "ExtractBloom.xml");
		blur = new GLTextureFilter(p, "Blur.xml");
		blend4 = new GLTextureFilter(p, "Blend4.xml");
	    toneMap = new GLTextureFilter(p, "ToneMap.xml");
		int w = canvasSize;
		int h = canvasSize;

		finalTexture = new GLTexture(p, w, h, GLTexture.FLOAT);
	    bloomMask = new GLTexture(p, w, h, GLTexture.FLOAT);
		tex0 = new GLTexture(p, w, h, GLTexture.FLOAT);
		tex2 = new GLTexture(p, w / 2, h / 2, GLTexture.FLOAT);
		tex4 = new GLTexture(p, w / 4, h / 4, GLTexture.FLOAT);
		tex8 = new GLTexture(p, w / 8, h / 8, GLTexture.FLOAT);
		tex16 = new GLTexture(p, w / 16, h / 16, GLTexture.FLOAT);
	}

	private void printStartAndDeadline() {
		// define output format and print
		SimpleDateFormat sdf = new SimpleDateFormat("d MMM yyyy hh:mm aaa");
		String date = sdf.format(start.getTime());
		System.out.println("Start of hacking set to " + date);
		date = sdf.format(deadline.getTime());
		System.out.println("End of hacking set to " + date);
	}

	private float getCrescentIndex() {
		float index = (System.currentTimeMillis() - start.getTimeInMillis()) / hackingMs;
		if(index < 0.0f)
			index = 0.0f;
		if(index > 1.0f)
			index = 1.0f;

		if(index < 0.5f) {
			index *= 0.8f;
			index += 0.1f;
		}
		return index;
	}

	public void draw() {
		if(finalTexture == null) initGL();

		// calculate mask coordinates
		float index = getCrescentIndex();
		float middleX = 0.5f * diameter - index * diameter;
		float a = Main.sqrt(middleX*middleX + (diameter/2f)*(diameter/2f));
		float b = a;
		float c = diameter;
		float s = 0.5f * (a + b + c);
		float K = Main.sqrt(s * (s-a) * (s-b) * (s-c));
		float R = a*b*c/(4*K);
		float centerX;
		if(index < 0.5)
			centerX = middleX - R;
		else
			centerX = middleX + R;

		// draw shadow mask
		moonMask.beginDraw();
		moonMask.background(index < 0.5f ? 255 : 0);
		moonMask.fill(index < 0.5f ? 0 : 255);
		moonMask.noStroke();
		moonMask.translate(moonMask.width/2, moonMask.height/2);
		moonMask.ellipseMode(Main.CENTER);
		moonMask.translate(centerX, 0);
		moonMask.ellipse(0, 0, R*2, R*2);
		moonMask.endDraw();

		// draw moon
		canvas.beginDraw();
		canvas.translate(canvas.width/2, canvas.height/2);
		canvas.imageMode(Main.CENTER);
		canvas.image(moon, 0, 0, diameter, diameter);
		canvas.endDraw();

		GLTexture moonMaskTexture = moonMask.getTexture();
		GLTexture canvasTexture = canvas.getTexture();
		maskFilter.setParameterValue("mask_factor", 0.0f);
		maskFilter.apply(new GLTexture[]{canvasTexture, moonMaskTexture}, maskedCanvasTexture);

		// Extracting the bright regions from input texture.
		srcTex = maskedCanvasTexture;
		extractBloom.setParameterValue("bright_threshold", fx);
		extractBloom.apply(srcTex, tex0);

		// Downsampling with blur.
		tex0.filter(blur, tex2);
		tex2.filter(blur, tex4);    
		tex4.filter(blur, tex8);    
		tex8.filter(blur, tex16);     

		// Blending downsampled textures.
		blend4.apply(new GLTexture[]{tex2, tex4, tex8, tex16}, new GLTexture[]{bloomMask});
		
	    // Final tone mapping into destination texture.
	    toneMap.setParameterValue("exposure", fy);
	    toneMap.setParameterValue("bright", fx);
	    toneMap.apply(new GLTexture[]{srcTex, bloomMask}, new GLTexture[]{finalTexture});

		p.image(finalTexture, 0, 0);
	}
}
