package net.qmat.mtf.models;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sun.opengl.util.FileUtil;

import net.qmat.mtf.Main;
import net.qmat.mtf.utils.Settings;

public class WindowMasksModel extends ProcessingObject {
	
	private ArrayList<WindowMask> masks;
	
	private boolean showStrokesP = true;
	
	public WindowMasksModel() {
		loadMasks();
	}
	
	public void draw() {
		p.background(0);
		if(showStrokesP)
			p.stroke(255, 0, 0);
		else
			p.noStroke();
		p.ellipseMode(Main.CENTER);
		
		for(WindowMask mask : masks) {
			p.ellipse(mask.x, mask.y, mask.diameter, mask.diameter);
		}
	}
	
	public File getMasksFile() {
		return Settings.getConfigurationFile(Settings.CONF_WINDOW_MASKS);
	}
	
	public void loadMasks() {
		Type t = new TypeToken<ArrayList<WindowMask>>(){}.getType();
		try {
			File f = getMasksFile();
			masks = p.gson.fromJson(FileUtils.readFileToString(f), t);
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
		masks.add(mask);
		return mask;
	}
	
	public void removeMask(WindowMask mask) {
		masks.remove(mask);
	}

}
