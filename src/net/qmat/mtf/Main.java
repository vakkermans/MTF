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
import net.qmat.mtf.models.WindowMasksModel;
import net.qmat.mtf.utils.Settings;
import pbox2d.PBox2D;
import codeanticode.glgraphics.GLConstants;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import codeanticode.glgraphics.GLTexture;
import processing.core.PApplet;

public class Main extends PApplet {

	private static final long serialVersionUID = 1L;
	public static Main p;
	public static PBox2D box2d;
	public static int frameCount = 0; 
	public static Gson gson = new Gson();
	public static boolean debug = false;

	public static GLGraphicsOffScreen vp, vp1, vp2;
	public static int selectedScreen = 1;
	
	public static int screenWidth, screenHeight;
	
	public Main() {
		p = this;
	}

	public void setup() {

		/* Set up processing stuff, size() should always be the first call in setup() */
		// N.B. we have 3 screens
		size(screenWidth * 3, screenHeight, GLConstants.GLGRAPHICS);
		p.hint(DISABLE_DEPTH_TEST);
		p.hint(DISABLE_OPENGL_2X_SMOOTH);
		p.hint(ENABLE_OPENGL_4X_SMOOTH);

		vp1 = new GLGraphicsOffScreen(this, screenWidth, screenHeight);
		vp2 = new GLGraphicsOffScreen(this, screenWidth, screenHeight);
		vp = vp1;
		
		/* Don't need box2d
		box2d = new PBox2D(this);
		box2d.createWorld();
		box2d.setGravity(0.0f, 0.0f);
		box2d.world.setContactListener(new ContactController());
		*/
		
		/* 
		 * N.B. Keep in mind that order matters. Wouldn't want to receive OSC
		 * messages without the models set up properly. 
		 */
		Controllers.init(); 
		Models.init(); 
	}

	public void draw() {
		// clear screens
		background(0);
		vp1.beginDraw();
		vp1.background(0);
		vp1.endDraw();
		vp2.beginDraw();
		vp2.background(0);
		vp2.endDraw();
		
		
		Controllers.update();
		Models.update();
		
		Models.draw();
		
		// if '1' was pressed
		if(Controllers.getKeyController().keyPressedAndChangedP(49)) {
			vp = vp1;
			selectedScreen = 1;
		// if '2' was pressed
		} else if(Controllers.getKeyController().keyPressedAndChangedP(50)) {
			vp = vp2;
			selectedScreen = 2;
		} 
		
		// draw selected screen on the left
		GLTexture vp1t = vp1.getTexture();
		GLTexture vp2t = vp2.getTexture();
		imageMode(CORNER);
		image(selectedScreen == 1 ? vp1t : vp2t, 0, 0);
		// draw screen1 and screen2
		image(vp1t, screenWidth, 0);
		image(vp2t, 2 * screenWidth, 0);
		
		Controllers.updateAtEndOfDraw();

		//box2d.step();
		frameCount++;
	}

	public static void main(String args[]) {
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
		debug = Settings.getBoolean(Settings.DEBUG);
		screenWidth = Settings.getInteger(Settings.PR_WIDTH);
		screenHeight = Settings.getInteger(Settings.PR_HEIGHT);
		
		
		// TODO: set right location
		String location = "--location=0,30";
		PApplet.main(new String[] {location, "net.qmat.mtf.Main" });
		
		String[][] keys = new String[][] {
				{"s", "Save masks to disk."},
				{"a", "Add new mask."},
				{"d", "Delete selected mask."},
				{"arrows", "Resize the selected mask."},
				{"h", "Toggle hiding of the window mask borders."},
				{"e", "Toggle resizing both dimensions of the masks together."}
		};
		System.out.println("\nThe following commands are available.");
		for(String[] key : keys) {
			System.out.println("\t" + key[0] + "\t" + key[1]);
		}
		System.out.println();
		
	}

	public void init(){
		// TODO: uncomment for production
		/*
		if(frame!=null){
			//make the frame not displayable
			frame.removeNotify();
			frame.setResizable(false);
			frame.setUndecorated(true);
			frame.addNotify();
		}
		*/
		super.init();
	}

	public void mousePressed() {
		Controllers.getKeyController().mousePressed(mouseButton);
	}
	
	public void mouseReleased() {
		Controllers.getKeyController().mouseReleased(mouseButton);
	}
	
	public void keyPressed() {
		System.out.println(keyCode);
		Controllers.getKeyController().keyPressed(keyCode);
	}
	
	public void keyReleased() {
		Controllers.getKeyController().keyReleased(keyCode);
	}
}