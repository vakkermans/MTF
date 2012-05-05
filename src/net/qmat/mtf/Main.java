/*
 * Main class, this is the main processing applet, the origin of EVERYTHING.
 * 
 */

// TODO: change implementation of spore moving through outer perimeter to categoryBits

package net.qmat.mtf;

import com.google.gson.Gson;

import net.qmat.mtf.controllers.ContactController;
import net.qmat.mtf.controllers.Controllers;
import net.qmat.mtf.models.Models;
import net.qmat.mtf.utils.Settings;
import pbox2d.PBox2D;
import codeanticode.glgraphics.GLConstants;

import processing.core.PApplet;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	public static Main p;
	public static PBox2D box2d;
	public int frameCount = 0; 
	public Gson gson = new Gson();

	public Main() {
		p = this;
	}

	public void setup() {

		/* Set up processing stuff, size() should always be the first call in setup() */
		size(Settings.getInteger(Settings.PR_WIDTH),
			 Settings.getInteger(Settings.PR_HEIGHT),
			 GLConstants.GLGRAPHICS);
		p.hint(DISABLE_DEPTH_TEST);
		p.hint(DISABLE_OPENGL_2X_SMOOTH);
		p.hint(ENABLE_OPENGL_4X_SMOOTH);

		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0.0f, 0.0f);
		box2d.world.setContactListener(new ContactController());
		
		/* 
		 * N.B. Keep in mind that order matters. Wouldn't want to receive OSC
		 * messages without the models set up properly. 
		 */
		Controllers.init(); 
		Models.init(); 
	}

	public void draw() {
		background(0);
		
		Controllers.update();
		Models.update();
		Models.draw();

		box2d.step();
		frameCount++;
		
		smooth();
	}

	public static void main(String args[]) {
		/* 
		 * Load settings at the start of the program so all the settings
		 * are cached before the rest of the code needs them. Sadly, can't be 
		 * loaded before setup(), otherwise code in setup could execute twice.
		 */
		
		/*
		Options options = new Options();
		options.addOption("t", true, "specify the table number");
		
		
		CommandLineParser parser = new PosixParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			Main.table = Integer.parseInt(cmd.getOptionValue("t"));
			if(Main.table < 1 || Main.table > 3)
				throw new Exception();
		} catch(ParseException e) {
			System.err.println("Could not parse command line options, exiting.");
			System.exit(1);
		} catch(Exception e) {
			System.err.println("Please provide a valid table number with the -t option (1, 2, or 3).");
			System.exit(1);
		}
		*/
		
		Settings.init();
		String location = "--location=0,0";
		PApplet.main(new String[] {location, "net.qmat.mtf.Main" });
	}

	public void init(){
		if(frame!=null){
			//make the frame not displayable
			frame.removeNotify();
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.addNotify();
		}
		super.init();
	}

	public void mousePressed() {
		// TODO: implement masking controls
	}
	
	public void keyPressed() {
		if(key == 's') {
			Models.getWindowMasksModel().saveMasks();
		}
	}
}