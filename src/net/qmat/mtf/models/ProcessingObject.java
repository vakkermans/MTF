package net.qmat.mtf.models;
import codeanticode.glgraphics.GLGraphicsOffScreen;
import net.qmat.mtf.Main;
import pbox2d.PBox2D;

public class ProcessingObject {
	
	protected Main p;
	protected GLGraphicsOffScreen vp1, vp2;
	protected PBox2D box2d;
	private boolean markedForRemovalP = false;
	
	/*
	 * Give all ProcessingObjects a reference to the main applet and 
	 * the box2d world.
	 */
	public ProcessingObject()
	{
		this.p = Main.p;
		this.vp1 = Main.vp1;
		this.vp2 = Main.vp2;
		this.box2d = Main.box2d;
	}
	
	/*
	 * If an object needs to do some calculations before it is drawn, put that
	 * stuff in the update() function. Flocking behaviour should be implemented 
	 * here for example.
	 */ 
	public void update() {}
	
	/*
	 * This draws the object on the screen
	 */
	public void draw() {}
	
	/*
	 *  This does any clean up, should only be necessary when dealing with box2d.
	 */
	public void destroy() {}
	

	/*
	 * Handy for objects that have to be removed in the update call.
	 */
	public void markForRemoval() {
		markedForRemovalP = true;
	}
	
	public boolean isMarkedForRemovalP() {
		return markedForRemovalP;
	}

}
