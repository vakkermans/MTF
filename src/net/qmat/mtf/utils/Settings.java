package net.qmat.mtf.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class Settings {
	
	// settings for graphics
	public static String DEBUG = "DEBUG";
	public static String PR_WIDTH = "PR_WIDTH";
	public static String PR_HEIGHT = "PR_HEIGHT";
	public static String PR_FULLSCREEN = "PR_FULLSCREEN";
	public static String PR_SCREENS = "PR_SCREENS";
	public static String PR_POSITION_X = "PR_POSITION_X";
	public static String PR_POSITION_Y = "PR_POSITION_Y";
	public static String PR_PRESENTATION = "PR_PRESENTATION";
	
	// settings for network
	public static String OSC_LOCAL_PORT = "OSC_LOCAL_PORT";
	
	// configuration files
	public static String CONF_WINDOW_MASKS = "window_masks.json";
	
	
	private static Settings instance = null;
	Properties properties = new Properties();
	
    public static Settings getInstance() {
       return instance;
    }
    
    public static void init() {
    	if(instance == null) {
            instance = new Settings();
        }
    }
	
	protected Settings() {
		try {
			properties.load(new BufferedReader(new InputStreamReader(this.getClass().getResource("/preferences.properties").openStream())));
		} catch (Exception e) {
			System.err.println("Something went wrong while loading the preferences.");
			e.printStackTrace();
		}
	}
	
	public static String getString(String key) {
		return getInstance().properties.getProperty(key);
	}
	
	public static Boolean getBoolean(String key) {
		String setting = getString(key);
		return setting.equalsIgnoreCase("true") 
			   || setting.equalsIgnoreCase("1") 
			   || setting.equalsIgnoreCase("yes");
	}
	
	public static Integer getInteger(String key) {
		return Integer.parseInt(getString(key));
	}
	
	public static float getFloat(String key) {
		return Float.parseFloat(getString(key));
	}
	
	public static float[] getFloatArray(String key) {
		String array = getString(key);
		String split[] = array.split(",");
		float result[] = new float[split.length];
		for(int i=0; i<split.length; i++)
			result[i] = Float.parseFloat(split[i]);
		return result;
	}
	
	private static String getAppPath() {
		return System.getProperty("user.home") + File.separator + ".net.qmat.mtf";
	}
	
	public static String getConfigurationPath(String filename) {
		return getAppPath() + File.separator + filename;
	}
	
	public static File getConfigurationFile(String filename) {
		return new File(Settings.getConfigurationPath(filename));
	}
	
}
