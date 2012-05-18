package net.qmat.mtf.models;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

import codeanticode.glgraphics.GLGraphicsOffScreen;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.qmat.mtf.Main;
import net.qmat.mtf.utils.Settings;

public class WindowMasksModel extends ProcessingObject {
	
	private ArrayList<WindowMask> masks;
	
	private boolean showStrokesP = false;
	private WindowMask selectedMask = null;
	
	public WindowMasksModel() {
		loadMasks();
	}
	
	public void draw() {
		p.ellipseMode(Main.CENTER);
		
		for(WindowMask mask : masks) {
			GLGraphicsOffScreen vp = (mask.screen == 1) ? vp1 : vp2;
			vp.strokeWeight(1f);
			vp.beginDraw();
			if(showStrokesP) {
				vp.fill(0, 0, 0, 200);
				if(selectedMask == mask)
					vp.stroke(255, 255, 0);
				else
					vp.stroke(255, 0, 0);
			} else {
				vp.fill(0);
				vp.noStroke();
			}
			vp.ellipse(mask.x, mask.y, mask.width, mask.height);
			vp.endDraw();
		}
	}
	
	public boolean selectedMaskP() {
		return selectedMask != null;
	}
	
	public void selectMask(float x, float y) {
		for(int i=masks.size()-1; i>=0; i--) {
			WindowMask mask = masks.get(i);
			if(mask.screen == Main.selectedScreen) {
				if(insideMask(x, y, mask)) {
					selectedMask = mask;
					return;
				}
			}
		}
		unselectMask();
	}
	
	private boolean insideMask(float x, float y, WindowMask mask) {
		float dx = x - mask.x;
		float dy = y - mask.y;
		return (dx*dx) / ((mask.width/2)*(mask.width/2)) + (dy*dy) / ((mask.height/2)*(mask.height/2)) < 1f;
	}
	
	private void unselectMask() {
		selectedMask = null;
	}
	
	public void moveMask(float dx, float dy) {
		if(selectedMask != null) {
			selectedMask.x += dx;
			selectedMask.y += dy;
		}
	}
	
	private File getMasksFile() {
		return Settings.getConfigurationFile(Settings.CONF_WINDOW_MASKS);
	}
	
	private void loadMasks() {
		Type t = new TypeToken<ArrayList<WindowMask>>(){}.getType();
		try {
			File f = getMasksFile();
			masks = Main.gson.fromJson(FileUtils.readFileToString(f), t);
			System.out.println("Loaded " + masks.size() + " masks from disk.");
		} catch (JsonSyntaxException e) {
			System.err.print("There was an error interpreting the serialised window masks.");
			System.err.println(" Will initialise without any masks.");
			masks = new ArrayList<WindowMask>();
		} catch (IOException e) {
			System.err.print("Could not access window masks saved on disk.");
			System.err.println(" Will initialise without any masks.");
			masks = new ArrayList<WindowMask>();
		}
	}
	
	public void saveMasks() {
		File f = getMasksFile();
		try {
			FileUtils.writeStringToFile(f, p.gson.toJson(masks));
			System.out.println("Saved " + masks.size() + " masks to disk.");
		} catch (IOException e) {
			System.err.println("Could not save window masks to disk.");
		}
	}
	
	public WindowMask addMask() {
		WindowMask mask = new WindowMask();
		mask.x = p.mouseX;
		mask.y = p.mouseY;
		mask.screen = Main.selectedScreen;
		masks.add(mask);
		return mask;
	}
	
	public void removeMask() {
		if(selectedMask != null)
			masks.remove(selectedMask);
	}
	
	public void changeWidth(float amount) {
		if(selectedMask != null)
			selectedMask.width += amount;
	}
	
	public void changeHeight(float amount) {
		if(selectedMask != null)
			selectedMask.height += amount;
	}
	
	public void toggleBorderHiding() {
		showStrokesP = !showStrokesP;
	}

}
